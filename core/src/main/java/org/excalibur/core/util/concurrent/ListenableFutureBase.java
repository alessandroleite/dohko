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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class ListenableFutureBase<T> implements ListenableFuture<T>
{
    private final Set<FutureListener<T>> listeners = new CopyOnWriteArraySet<FutureListener<T>>();
    private final ReadWriteLock          lock = new ReentrantReadWriteLock();
    protected final AtomicBoolean        isFutureCompleted = new AtomicBoolean(false);

    @Override
    public final ListenableFuture<T> addListener(FutureListener<T> listener)
    {
        lock.readLock().lock();
        try
        {
            if (listener != null && !isFutureCompleted.get())
            {
                this.listeners.add(listener);
            }

            if (isFutureCompleted.get())
            {
                listener.futureDone(this);
            }
        }
        finally
        {
            lock.readLock().unlock();
        }
        return this;
    }

    /**
     * Notifies the listeners that this future is done.
     * 
     * @throws IllegalStateException
     *             If the listeners were already fired.
     */
    protected void fireListeners()
    {
        lock.writeLock().lock();

        try
        {
            if (isFutureCompleted.compareAndSet(false, true))
            {
                for (FutureListener<T> listener : listeners)
                {
                    listener.futureDone(this);
                }
            }
            else
            {
                throw new IllegalStateException("The listeners were already fired!");
            }
        }
        finally
        {
            lock.writeLock().unlock();
        }
    }
}
