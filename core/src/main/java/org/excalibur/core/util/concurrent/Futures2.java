/**
 *     Copyright (C) 2013-2014  the original author or authors.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License,
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.excalibur.core.util.concurrent;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

@SuppressWarnings("unchecked")
public final class Futures2
{
    private Futures2()
    {
        throw new UnsupportedOperationException();
    }

    public <V> ListenableFuture<V> invokeAll(Future<V> future)
    {
        if (future instanceof ListenableFuture)
        {
            return (ListenableFuture<V>) future;
        }

        return null;
    }

    public static <V> ListenableFuture<V> addCallback(ListenableFuture<V> future, FutureCallback<V> callback)
    {
        return addCallbacks(future, new FutureCallback[] { callback });
    }

    public static <V> ListenableFuture<V> addCallbacks(ListenableFuture<V> future, FutureCallback<V>[] callbacks)
    {
        if (future != null && callbacks != null)
        {
            for (FutureCallback<V> callback : callbacks)
            {
                if (callback != null)
                {
                    Futures.addCallback(future, callback, directExecutor());
                }
            }
        }

        return future;
    }

    /**
     * Registers separate success and failure callbacks to be run when the {@code Future}'s computation is
     * {@linkplain java.util.concurrent.Future#isDone() complete} or, if the computation is already complete, immediately.
     * 
     * @param futures
     * @param callback
     * @param <V>
     * @return The same input.
     */
    public static <V> List<ListenableFuture<V>> addCallback(List<ListenableFuture<V>> futures, FutureCallback<V> callback)
    {
        return addCallbacks(futures, new FutureCallback[] { callback });
    }

    public static <V> List<ListenableFuture<V>> addCallbacks(List<ListenableFuture<V>> futures, FutureCallback<V>[] callbacks)
    {
        if (futures != null && callbacks != null)
        {
            for (ListenableFuture<V> future : futures)
            {
                addCallbacks(future, callbacks);
            }
        }

        return futures;
    }

    public static <V> List<ListenableFuture<V>> invokeAll(Iterable<Callable<V>> tasks, ListeningExecutorService executor)
    {
        List<ListenableFuture<V>> futures = Lists.newArrayList();

        for (Callable<V> task : tasks)
        {
            ListenableFuture<V> future = executor.submit(task);
            futures.add(future);
        }

        return futures;
    }

    public static <V> List<ListenableFuture<V>> invokeAll(Iterable<Callable<V>> tasks, ListeningExecutorService executor,
            FutureCallback<V>[] callbacks)
    {
        List<ListenableFuture<V>> futures = Lists.newArrayList();

        for (Callable<V> task : tasks)
        {
            ListenableFuture<V> future = executor.submit(task);
            futures.add(addCallbacks(future, callbacks));
        }

        return futures;
    }

    public static <V> List<V> invokeAllAndShutdownWhenFinish(List<Callable<V>> tasks, ListeningExecutorService executor)
    {
        return invokeAllAndShutdownWhenFinish(tasks, executor, new FutureCallback[0]);
    }

    public static <V> List<V> invokeAllAndShutdownWhenFinish(List<Callable<V>> tasks, ListeningExecutorService executor, FutureCallback<V>[] callbacks)
    {
        final List<V> results = Lists.newArrayList();
        final CountDownLatch endSignal = new CountDownLatch(tasks.size());

        FutureCallback<V> endSignalCallback = new FutureCallback<V>()
        {
            @Override
            public void onSuccess(@Nullable V result)
            {
                endSignal.countDown();
                results.add(result);
            }

            @Override
            public void onFailure(Throwable t)
            {
                endSignal.countDown();
            }
        };

        List<FutureCallback<V>> l = Lists.newArrayList(callbacks);
        l.add(endSignalCallback);

        invokeAll(tasks, executor, l.toArray(new FutureCallback[l.size()]));

        try
        {
            endSignal.await();
            executor.shutdownNow();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return results;
    }
    
    public static void invokeAllAndShutdownWhenFinish(List<Callable<Boolean>> tasks, String threadName)
    {
        ListeningExecutorService executor = 
                DynamicExecutors.newListeningDynamicScalingThreadPool(threadName);
        
        invokeAllAndShutdownWhenFinish(tasks, executor);
    }
    
    public static <V> List<V> invokeAll(List<Callable<V>> tasks)
    {
        return invokeAllAndShutdownWhenFinish(tasks, MoreExecutors.listeningDecorator(Executors.newCachedThreadPool()));
    }
    
   

    /**
     * Returns the result of calling {@link Future#get()} uninterruptibly on a task known not to throw a checked exception. This makes {@code Future}
     * more suitable for lightweight, fast-running tasks that, barring bugs in the code, will not fail. This gives it exception-handling behavior
     * similar to that of {@code ForkJoinTask.join}.
     * 
     * <p>
     * Exceptions from {@code Future.get} are treated as follows:
     * <ul>
     * <li>Any {@link ExecutionException} has its <i>cause</i> wrapped in an {@link UncheckedExecutionException} (if the cause is an {@code Exception}
     * ) or {@link ExecutionError} (if the cause is an {@code Error}).
     * <li>Any {@link InterruptedException} causes a retry of the {@code get} call. The interrupt is restored before {@code getUnchecked} returns.
     * <li>Any {@link CancellationException} is propagated untouched. So is any other {@link RuntimeException} ({@code get} implementations are
     * discouraged from throwing such exceptions).
     * </ul>
     * 
     * <p>
     * The overall principle is to eliminate all checked exceptions: to loop to avoid {@code InterruptedException}, to pass through
     * {@code CancellationException}, and to wrap any exception from the underlying computation in an {@code UncheckedExecutionException} or
     * {@code ExecutionError}.
     * 
     * <p>
     * For an uninterruptible {@code get} that preserves other exceptions, see {@link Uninterruptibles#getUninterruptibly(Future)}.
     * 
     * @throws UncheckedExecutionException
     *             if {@code get} throws an {@code ExecutionException} with an {@code Exception} as its cause
     * @throws ExecutionError
     *             if {@code get} throws an {@code ExecutionException} with an {@code Error} as its cause
     * @throws CancellationException
     *             if {@code get} throws a {@code CancellationException}
     */
    public static <V> List<V> getUnchecked(List<Future<V>> futures)
    {
        List<V> results = Lists.newArrayList();

        for (Future<V> future : futures)
        {
            results.add(Futures.getUnchecked(future));
        }

        return results;
    }

    public static class FutureCallbackList<V> implements FutureCallback<V>
    {
        private final List<V> successfulResults_ = Lists.newCopyOnWriteArrayList();
        private final List<Throwable> errors_ = Lists.newCopyOnWriteArrayList();

        @Override
        public void onSuccess(@Nullable V result)
        {
            successfulResults_.add(result);
        }

        @Override
        public void onFailure(Throwable t)
        {
            errors_.add(t);
        }

        public List<V> getResults()
        {
            return successfulResults_;
        }

        public List<Throwable> getErrors()
        {
            return errors_;
        }
    }

   
}
