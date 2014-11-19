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
import java.util.Set;


/**
 * This {@link OverlayFuture} represents the invocation result of {@link Overlay#retrieveAsync(Key)}.
 * 
 * The result can be obtained with help of {@link #getResult()}.
 */
public interface OverlayRetrievalFuture extends OverlayFuture
{

    /**
     * Method to obtain the result of the retrieve operation associated with this. This method blocks the calling thread until the invocation of the
     * retrieve operation has finished (either by obtaining a result or a {@link Throwable}/{@link Exception} that occured).
     * 
     * @return The entries that have been retrieved. Empty {@link Set} if no entries have been found.
     * @throws OverlayException
     *             Thrown if the execution has not been successful. Contains the {@link Throwable} that can be obtained by
     *             {@link OverlayFuture#getThrowable()} as cause.
     * @throws InterruptedException
     *             If the thread, which invokes this method, has been interrupted while waiting for the result.
     */
    public Set<Serializable> getResult() throws OverlayException, InterruptedException;
}
