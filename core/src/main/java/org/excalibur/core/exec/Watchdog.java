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

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;

public class Watchdog implements Runnable
{
    private final List<TimeoutObserver> observers_ = new LinkedList<TimeoutObserver>();

    /**
     * The timeout to wait.
     */
    private final long timeout_;

    /**
     * A flag to indicates that if the watchdog is active.
     */
    private boolean stopped_;

    /**
     * Creates a new {@link Watchdog} with a given timeout.
     * 
     * @param timeout
     *            The timeout.
     */
    public Watchdog(long timeout)
    {
        Preconditions.checkArgument(timeout > 0, "timeout must be at least 1.");
        this.timeout_ = timeout;
    }

    /**
     * Adds a {@link TimeoutObserver} to this {@link Watchdog}.
     * 
     * @param observer
     *            The observer to add. <code>null</code> values are ignored.
     */
    public void addTimeoutObserver(TimeoutObserver observer)
    {
        if (observer != null)
        {
            synchronized (this)
            {
                this.observers_.add(observer);
            }
        }
    }

    /**
     * Removes a give {@link TimeoutObserver}.
     * 
     * @param observer
     *            The observer to remove.
     */
    public void removeTimeoutObserver(TimeoutObserver observer)
    {
        synchronized (this)
        {
            this.observers_.remove(observer);
        }
    }

    /**
     * Notifies the observers about the timeout.
     */
    protected final void notifyObservers()
    {
        for (TimeoutObserver observer : this.observers_)
        {
            observer.timeoutOccured(this);
        }
    }

    /**
     * Starts this {@link Watchdog}.
     */
    public synchronized void start()
    {
        this.stopped_ = false;
        Thread t = new Thread(this, "WATCHDOG");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Stops this {@link Watchdog}.
     */
    public synchronized void stop()
    {
        this.stopped_ = false;
        this.notifyAll();
    }

    @Override
    public void run()
    {
        final long startTime = System.currentTimeMillis();
        boolean isWaiting;

        synchronized (this)
        {
            long timeLeft = timeout_ - (System.currentTimeMillis() - startTime);
            isWaiting = timeLeft > 0;
            while (isWaiting && !stopped_)
            {
                try
                {
                    wait(timeLeft);
                }
                catch (InterruptedException e)
                {
                }
                timeLeft = timeout_ - (System.currentTimeMillis() - startTime);
                isWaiting = timeLeft > 0;
            }
        }

        if (!isWaiting)
        {
            notifyObservers();
        }
    }
}
