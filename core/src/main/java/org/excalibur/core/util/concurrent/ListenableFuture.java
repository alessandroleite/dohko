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

/**
 * A sub-interface of a {@link Future}, that allows for listeners to be attached so that observers can be notified of when the future completes.
 * 
 * @param <T>
 */
public interface ListenableFuture<T> extends Future<T>
{
    /**
     * Attaches a listener to notify when this future finish, and returns the same instance.
     * 
     * <p>
     * There is no guaranteed ordering of execution of listeners, but any listener added through this method is guaranteed to be called once the
     * computation is complete.
     * 
     * @param listener
     *            The listener to attach. <code>null</code> values are ignored.
     * @return The same reference for this future.
     */
    ListenableFuture<T> addListener(FutureListener<T> listener);
}
