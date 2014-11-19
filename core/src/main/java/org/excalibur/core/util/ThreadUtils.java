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
package org.excalibur.core.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Throwables.*;

public final class ThreadUtils
{

    private static final Logger LOG = LoggerFactory.getLogger(ThreadUtils.class.getName());

    private ThreadUtils()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted,
     * whichever happens first.
     * 
     * @param executor
     *            the executor to wait its tasks finishes.
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @param propagateIfInterrupted
     *            a flag to indicate if the {@link InterruptedException} must be propagated if the thread was interrupted.
     * @return <tt>true</tt> if this executor terminated and <tt>false</tt> if the timeout elapsed before termination.
     * @throws InterruptedException
     *             if interrupted while waiting
     */
    public static final boolean awaitTerminationAndShutdown(ExecutorService executor, long timeout, TimeUnit unit, boolean propagateIfInterrupted)
    {
        boolean result = false;
        
        if (executor != null)
        {
            try
            {
                result = executor.awaitTermination(timeout, unit);
                List<Runnable> waitingTasks = executor.shutdownNow();
                
                LOG.debug("Executor was terminated with [{}] tasks waiting execution", waitingTasks.size());
            }
            catch (InterruptedException e)
            {
                if (propagateIfInterrupted)
                {
                    propagate(e);
                }
                else
                {
                    LOG.debug("Ignoring thread interruption. Error message [{}]", e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted,
     * whichever happens first. It does not throw an exception when the thread is interrupted.
     * 
     * @param executor
     *            the executor to wait its tasks finishes.
     * @param timeout
     *            the maximum time to wait
     * @param unit
     *            the time unit of the timeout argument
     * @return <tt>true</tt> if this executor terminated and <tt>false</tt> if the timeout elapsed before termination.
     */
    public static final boolean awaitTerminationAndShutdownAndIgnoreInterruption(ExecutorService executor, long timeout, TimeUnit unit)
    {
        return awaitTerminationAndShutdown(executor, timeout, unit, false);
    }

    public static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            LOG.error("Thread interrupted on waiting for [{}] millis", millis);
        }
    }
}
