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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.excalibur.core.util.concurrent.DynamicThreadPoolExecutor.DynamicQueue;
import org.excalibur.core.util.concurrent.DynamicThreadPoolExecutor.ForceQueuePolicy;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static java.lang.Runtime.*;

public class DynamicExecutors
{
    private DynamicExecutors()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a thread pool using the provided {@link ThreadFactory} to create new threads when needed, and re-scheduling the execution of the tasks
     * when rejected.
     * 
     * @param min
     *            the number of threads to keep in the pool, even if they are idle.
     * @param max
     *            the maximum number of threads to allow in the pool.
     * @param keepAliveTime
     *            when the number of threads is greater than {@code min}, this is the maximum time that excess idle threads will wait for new tasks
     *            before terminating (in milliseconds).
     * @param threadFactory
     *            the factory to use when creating new threads.
     * @return the newly created thread pool.
     */
    public static ExecutorService newScalingThreadPool(int min, int max, long keepAliveTime, ThreadFactory threadFactory)
    {
        return newScalingThreadPool(min, max, keepAliveTime, TimeUnit.MILLISECONDS, threadFactory);
    }

    public static ExecutorService newScalingThreadPool(int min, int max, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
    {
        DynamicQueue<Runnable> queue = new DynamicQueue<Runnable>();
        ThreadPoolExecutor executor = new DynamicThreadPoolExecutor(min, max, keepAliveTime, unit, queue, threadFactory);
        executor.setRejectedExecutionHandler(new ForceQueuePolicy());
        queue.setThreadPoolExecutor(executor);

        return executor;
    }
    
    public static ListeningExecutorService newListeningDynamicScalingThreadPool(int min, int max, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
    {
        return MoreExecutors.listeningDecorator(newScalingThreadPool(min, max, keepAliveTime, unit, threadFactory));
    }
    
    public static ListeningExecutorService newListeningDynamicScalingThreadPool(String threadNamesFormat)
    {
        return newListeningDynamicScalingThreadPool(threadNamesFormat, getRuntime().availableProcessors());
    }
    
    public static ListeningExecutorService newListeningDynamicScalingThreadPool(String threadNamesFormat, int max)
    {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadNamesFormat).build();
        int max_ = max > getRuntime().availableProcessors() ? max : getRuntime().availableProcessors();
        return newListeningDynamicScalingThreadPool(getRuntime().availableProcessors(), max_, 1, TimeUnit.MINUTES, threadFactory);
    }
}
