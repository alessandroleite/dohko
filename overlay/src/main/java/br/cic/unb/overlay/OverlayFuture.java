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
package br.cic.unb.overlay;

import java.io.Serializable;


/**
 * <p>
 * This interface represents the result of an asynchronous invocation on an implementation of {@link AsynChord}.
 * </p>
 * <p>
 * The methods:
 * <ul>
 * <li>{@link Overlay#insertAsync(Key, Serializable)}</li>
 * <li>{@link Overlay#removeAsync(Key, Serializable)}</li>
 * <li>{@link Overlay#retrieveAsync(Key)}</li>
 * </ul>
 * return immediately and return an instance of this, which can be used later on to check if the execution of an insertion, removal, or retrieval has
 * been completed.
 * </p>
 */
public interface OverlayFuture
{
    /**
     * 
     * @return Any exception that occurred during execution of the method associated with this. May be <code>null</code>. If {@link #isDone()} returns
     *         <code>true</code> and this returns <code>null</code> the associated method has been executed successfully.
     */
    public abstract Throwable getThrowable();

    /**
     * Method to test if the method associated with this has been finished. This method does not block the calling thread.
     * 
     * @return <code>true</code> if the method associated with this has finished successfully.
     * @throws OverlayException
     *             Thrown if the execution has not been successful. Contains the {@link Throwable} that can be obtained by {@link #getThrowable()} as
     *             cause.
     */
    public abstract boolean isDone() throws OverlayException;

    /**
     * This method blocks the calling thread until the execution of the method associated with this has been finished.
     * 
     * @throws OverlayException
     *             Thrown if the execution has not been successful. Contains the {@link Throwable} that can be obtained by {@link #getThrowable()} as
     *             cause.
     * 
     * @throws InterruptedException
     *             Occurs if the thread waiting with help of this method has been interrupted.
     */
    public abstract void waitForBeingDone() throws OverlayException, InterruptedException;

}
