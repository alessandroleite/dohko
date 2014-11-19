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

import java.util.concurrent.Future;

public interface NotifyListenableFuture<T> extends ListenableFuture<T>
{
    /**
     * Assigns the {@link Future} to fire this listener when finished. It must be called before the methods {@link #notifyDone(Object)} and
     * {@link #notifyFailure(Throwable)}.
     * 
     * @param future The future that this listener is observing. Must not be <code>null</code>.
     * @return The same reference. 
     */
    ListenableFuture<T> setFuture(Future<T> future);

    /**
     * Notifies that the associated {@link Future} finished. Subsequent calls for {@link #isDone()} will return <code>true</code> and subsequent calls
     * for {@link #get()} will return the given {@code result}.
     * 
     * @param result
     *            The result of the {@link Future}.
     * 
     */
    void notifyDone(T result);

    /**
     * Notifies that the associated {@link Future} finished with an {@link Exception}. Subsequent calls for {@link #isDone()} will return
     * <code>true</code> and subsequent calls for {@link #get()} will return the given <code>null</code>.
     * 
     * @param exception
     *            The exception thrown by the {@link Future}.
     */
    void notifyFailure(Throwable exception);
}
