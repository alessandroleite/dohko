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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class DynamicThreadPoolExecutor extends ThreadPoolExecutor
{
    private final AtomicInteger numberOfThreadsExecuting = new AtomicInteger();

    public DynamicThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    @Override
    public int getActiveCount()
    {
        return numberOfThreadsExecuting.get();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r)
    {
        numberOfThreadsExecuting.incrementAndGet();
//        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
//        super.afterExecute(r, t);
        numberOfThreadsExecuting.decrementAndGet();
    }

    static class DynamicQueue<E> extends LinkedBlockingQueue<E>
    {
        private static final long serialVersionUID = 1L;

        /**
         * The executor this Queue belongs to
         */
        private transient ThreadPoolExecutor executor;

        /**
         * Creates a <tt>DynamicQueue</tt> with a capacity of {@link Integer#MAX_VALUE}.
         */
        public DynamicQueue()
        {
            this(Integer.MAX_VALUE);
        }

        public DynamicQueue(int capacity)
        {
            super(capacity);
        }

        public void setThreadPoolExecutor(ThreadPoolExecutor executor)
        {
            this.executor = executor;
        }

        @Override
        public boolean offer(E e)
        {
            int workingThreads = executor.getActiveCount() + super.size();
            return workingThreads < executor.getPoolSize() && super.offer(e);
        }
    }

    static class ForceQueuePolicy implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
        {
            try
            {
                executor.getQueue().put(r);
            }
            catch (InterruptedException e)
            {
//                AnyThrow.throwUncheked(e);
                throw new RejectedExecutionException(e);
            }
        }
    }

    static class TimeoutBlockingPolicy implements RejectedExecutionHandler
    {
        private final long waitTime;
        private final TimeUnit waitTimeUnit;

        public TimeoutBlockingPolicy(long timeout, TimeUnit unit)
        {
            this.waitTime = timeout;
            this.waitTimeUnit = unit;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
        {
            try
            {
                boolean successful = executor.getQueue().offer(r, waitTime, waitTimeUnit);
                if (!successful)
                {
                    throw new RejectedExecutionException(String.format("Rejected execution after waiting %s (%s) for task: [%s] to be executed!",
                            waitTime, waitTimeUnit.name().toLowerCase(), r.getClass()));
                }
            }
            catch (InterruptedException e)
            {
                throw new RejectedExecutionException(e.getMessage(), e);
            }
        }
    }
}
