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
package org.excalibur.core.exec.local;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.exec.TimeoutObserver;
import org.excalibur.core.exec.Watchdog;

import com.google.common.base.Preconditions;

/**
 * This class represents a JVM subprocess. It's a wrapper to {@link Process} with the option to wait the process execution until a given timeout.
 */
public final class JVMProcess implements org.excalibur.core.exec.Process
{
    /**
     * The reference to the JVM {@link java.lang.Process}.
     */
    private final java.lang.Process process_;

    /**
     * The process's id. 
     */
    private final Integer pid_;
    
    /**
     * The exit value of the process.
     */
    private volatile int exitValue_ = Integer.MIN_VALUE;

    /**
     * A flag that the process was killed calling the method {@link #destroy()}.
     */
    private final AtomicBoolean killedProcess_ = new AtomicBoolean(false);

    /**
     * A {@link Watchdog} to indicate if a timeout has occurred.
     */
    private volatile Watchdog watchdog_;

    /** Indicates that this {@link org.excalibur.core.exec.Process} has already started */
    private volatile AtomicBoolean processStarted_ = new AtomicBoolean(false);


    /**
     * Creates a new process.
     * 
     * @param process
     *            The reference to JVM {@link java.lang.Process}. Might not be <code>null</code>.
     */
    public JVMProcess(java.lang.Process process)
    {
        this.process_ = Preconditions.checkNotNull(process);
        this.pid_ = (Integer) new Mirror().on(this.process_).get().field("pid");
    }

    @Override
    public synchronized int waitFor(final long timeout) throws InterruptedException
    {
        if (!processStarted_.get() && timeout > 0)
        {
            TimeoutObserver observer = new TimeoutObserver()
            {
                @Override
                public void timeoutOccured(Watchdog watchdog)
                {
                    if (!killedProcess_.get())
                    {
                        destroy();
                    }
                }
            };

            watchdog_ = new Watchdog(timeout);
            watchdog_.addTimeoutObserver(observer);
            watchdog_.start();
        }
        
        this.waitFor();
        return exitValue_;
    }

    @Override
    public int waitFor() throws InterruptedException
    {
        if (processStarted_.compareAndSet(false, true))
        {
            this.exitValue_ = this.process_.waitFor();

            if (this.watchdog_ != null)
            {
                this.watchdog_.stop();
            }
        }
        return exitValue_;
    }

    @Override
    public int exitValue() throws IllegalStateException
    {
        return (exitValue_ == Integer.MIN_VALUE ? exitValue_ = this.process_.exitValue() : exitValue_);
    }

    @Override
    public void destroy()
    {
        if (this.killedProcess_.compareAndSet(false, true))
        {
            this.process_.destroy();
        }
    }

    @Override
    public InputStream getInputStream()
    {
        return this.process_.getInputStream();
    }

    @Override
    public OutputStream getOutputStream()
    {
        return this.process_.getOutputStream();
    }

    @Override
    public int getPID()
    {
        return this.pid_;
    }
}
