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
package org.excalibur.core.exec;

import com.google.common.base.Preconditions;

public class ExecuteWatchdog implements TimeoutObserver
{
    public static final int INFINITE_TIMEOUT = -1;

    /** The process to execute and watch for duration. */
    private volatile Process process_;

    private final Watchdog watchdog_;

    /** Is a user-supplied timeout in use */
    private final boolean hasWatchdog_;

    /** Say whether or not the {@link Watchdog} is currently monitoring a process. */
    private boolean watch_;

    /** Say whether or not the process was killed due to running overtime. */
    private boolean killedProcess_;

    /** Indicates that the process is verified as started */
    private volatile boolean processStarted_;

    /**
     * Exception that might be thrown during the process execution.
     */
    private Exception caught_;

    /**
     * Creates a new {@link Watchdog} with a given timeout.
     * 
     * @param timeout
     *            the timeout for the process in milliseconds. It must be greater than 0 or {@link #INFINITE_TIMEOUT}.
     */
    public ExecuteWatchdog(final long timeout)
    {
        this.hasWatchdog_ = timeout != INFINITE_TIMEOUT;
        this.processStarted_ = false;

        if (this.hasWatchdog_)
        {
            this.watchdog_ = new Watchdog(timeout);
            this.watchdog_.addTimeoutObserver(this);
        }
        else
        {
            // null watchdog.
            watchdog_ = new Watchdog(1)
            {
                @Override
                public synchronized void start()
                {
                }

                @Override
                public synchronized void stop()
                {
                }
            };
        }
    }

    @Override
    public synchronized void timeoutOccured(Watchdog watchdog)
    {
        if (process_ != null)
        {
            try
            {
                try
                {
                    this.process_.exitValue();
                }
                catch (IllegalStateException | IllegalThreadStateException exception)
                {
                    killedProcess_ = true;
                    this.process_.destroy();
                }                
            }
            catch (Exception exception)
            {
                this.caught_ = exception;
            }
            finally
            {
                cleanUp();
            }
        }
    }

    /**
     * Watches the given process and terminates it, if it runs for too long. All information from the previous run are reset.
     * 
     * @param process
     *            the process to monitor. It cannot be <tt>null</tt>
     * @throws IllegalStateException
     *             if a process is still being monitored.
     */
    public synchronized void start(Process process)
    {
        Preconditions.checkState(this.process_ == null, "Process has already started!");
        this.process_ = Preconditions.checkNotNull(process);

        this.caught_ = null;
        this.killedProcess_ = false;
        this.processStarted_ = true;
        this.watch_ = true;
        this.notifyAll();
        this.watchdog_.start();
    }

    /**
     * Stops the watcher. It will notify all threads possibly waiting on this object.
     */
    public synchronized void stop()
    {
        this.watchdog_.stop();
        this.watch_ = false;
        this.process_ = null;
    }

    /**
     * Destroy the running process manually.
     */
    public synchronized void destroyProcess()
    {
        ensureStarted();
        this.timeoutOccured(null);
        this.stop();
    }

    /**
     * This method will rethrow the exception that was possibly caught during the run of the process. It will only remains valid once the process has
     * been terminated either by 'error', timeout or manual intervention. Information will be discarded once a new process is ran.
     * 
     * @throws Exception
     *             a wrapped exception over the one that was silently swallowed and stored during the process run.
     */
    public synchronized void checkException() throws Exception
    {
        if (caught_ != null)
        {
            throw caught_;
        }
    }

    /**
     * Indicates whether or not the watchdog is still monitoring the process.
     * 
     * @return <tt>true</tt> if the process is still running, otherwise <tt>false</tt>.
     */
    public synchronized boolean isWatching()
    {
        ensureStarted();
        return watch_;
    }

    /**
     * Indicates whether the last process run was killed.
     * 
     * @return <tt>true</tt> if the process was killed <tt>false</tt>.
     */
    public synchronized boolean killedProcess()
    {
        return killedProcess_;
    }

    /**
     * reset the monitor flag and the process.
     */
    protected synchronized void cleanUp()
    {
        watch_ = false;
        process_ = null;
    }

    /**
     * Ensures that the process is started, so we do not race with asynchronous execution. The caller of this method must be holding the lock on this
     */
    private void ensureStarted()
    {
        while (!processStarted_)
        {
            try
            {
                this.wait();
            }
            catch (InterruptedException exception)
            {
                throw new RuntimeException(exception.getMessage(), exception);
            }
        }
    }
}
