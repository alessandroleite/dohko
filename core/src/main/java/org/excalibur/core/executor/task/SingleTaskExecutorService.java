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
package org.excalibur.core.executor.task;

import java.io.Serializable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nullable;

import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.util.AnyThrow;
import org.excalibur.core.util.concurrent.FutureListener;
import org.excalibur.core.util.concurrent.FutureListener.NullFutureListener;
import org.excalibur.core.util.concurrent.Futures2;
import org.excalibur.core.util.concurrent.ListenableFuture;
import org.excalibur.core.util.concurrent.ListenableFutureImpl;
import org.excalibur.core.util.concurrent.SimpleCountDownFutureCallback;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class SingleTaskExecutorService implements TaskExecutionService
{
    private final ListeningExecutorService executor;

    final Function<TaskContext, Callable<TaskResult<Serializable>>> asCallable = new Function<TaskContext, Callable<TaskResult<Serializable>>>()
    {
        @Override
        @Nullable
        public Callable<TaskResult<Serializable>> apply(@Nullable TaskContext task)
        {
            return new Worker<Serializable>(task.getTask(), task);
        }
    };

    public SingleTaskExecutorService()
    {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("task-executor-%d")
                .setUncaughtExceptionHandler(new TaskUncaughtExceptionHandler()).build();

        executor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor(threadFactory));
    }

    @Override
    public <T extends Serializable, V> ListenableFuture<V> schedule(TaskType<T> task, TaskContext context)
    {
        return this.schedule(task, context, NullFutureListener.<V> build());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Serializable, V> ListenableFuture<V> schedule(TaskType<T> task, TaskContext context, FutureListener<V> listener)
    {
        FutureTask<TaskResult<T>> call = new FutureTask<TaskResult<T>>(new Worker<T>(task, context));
        Future<V> future = (Future<V>) executor.submit(call);

        ListenableFuture<V> listenableFuture = new ListenableFutureImpl<V>().setFuture(future);

        if (listener != null)
        {
            listenableFuture.addListener(listener);
        }

        return listenableFuture;
    }

    static class Worker<T extends Serializable> implements Callable<TaskResult<T>>
    {
        private final TaskType<T> work;
        private final TaskContext context;

        public Worker(TaskType<T> task, TaskContext context)
        {
            this.work = task;
            this.context = context;
        }

        @Override
        public TaskResult<T> call() throws Exception
        {
            TaskResult<T> result = work.execute(context);
            return result;
        }
    }

    static class TaskUncaughtExceptionHandler implements UncaughtExceptionHandler
    {
        @Override
        public void uncaughtException(Thread t, Throwable e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public List<com.google.common.util.concurrent.ListenableFuture<TaskResult<Serializable>>> invokeAll(List<TaskContext> tasks)
    {
        return Futures2.invokeAll(Collections2.transform(tasks, asCallable), this.executor);
    }

    @SuppressWarnings("unchecked")
    public List<com.google.common.util.concurrent.ListenableFuture<TaskResult<Serializable>>> invokeAll(List<TaskContext> tasks,
            FutureCallback<TaskResult<Serializable>> callback)
    {
        return invokeAll(tasks, new FutureCallback[] { callback });
    }

    @Override
    public List<com.google.common.util.concurrent.ListenableFuture<TaskResult<Serializable>>> invokeAll(List<TaskContext> tasks,
            FutureCallback<TaskResult<Serializable>>[] callbacks)
    {
        return Futures2.addCallbacks(this.invokeAll(tasks), callbacks);
    }

    @Override
    public void invokeAllAndWait(List<TaskContext> contexts)
    {
        final CountDownLatch latch = new CountDownLatch(contexts.size());
        invokeAll(contexts, new SimpleCountDownFutureCallback<TaskResult<Serializable>>(latch));

        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            AnyThrow.throwUncheked(e);
        }

        this.executor.shutdown();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invokeAllAndWait(List<TaskContext> contexts, FutureCallback<TaskResult<Serializable>> callback)
    {
        final CountDownLatch latch = new CountDownLatch(contexts.size());
        SimpleCountDownFutureCallback<TaskResult<Serializable>> latchCallback = new SimpleCountDownFutureCallback<TaskResult<Serializable>>(latch);
        
        invokeAll(contexts, new FutureCallback[] { callback, latchCallback });
        
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            AnyThrow.throwUncheked(e);
        }

        this.executor.shutdown();
    }
}
