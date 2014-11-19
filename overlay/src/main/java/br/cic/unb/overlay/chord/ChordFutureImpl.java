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
package br.cic.unb.overlay.chord;

import br.cic.unb.overlay.OverlayException;
import br.cic.unb.overlay.OverlayFuture;

/**
 * Abstract implementation of {@link OverlayFuture}. Provides common functionality for all {@link OverlayFuture} implementations in this package.
 */
abstract class ChordFutureImpl implements OverlayFuture
{

    /**
     * Indicates that the request to {@link AsynChord} has been completed.
     */
    private boolean isDone = false;

    /**
     * Any Exception/Throwable that occured during execution of request associated with this future.
     */
    private Throwable throwable = null;

    /**
     * Instances of this can only be created by sub classes.
     * 
     */
    protected ChordFutureImpl()
    {
        super();
    }

    /**
     * Indicate that the method associated with this has completed.
     * 
     */
    final void setIsDone()
    {
        synchronized (this)
        {
            this.isDone = true;
            this.notifyAll();
        }
    }

    /**
     * Set a {@link Throwable} that occured during execution of method associated with this.
     * 
     * @param t
     */
    final void setThrowable(Throwable t)
    {
        this.throwable = t;
    }

    /**
     * @see OverlayFuture
     */
    public Throwable getThrowable()
    {
        return this.throwable;
    }

    /**
     * @see OverlayFuture
     * @return <code>true</code> if operation associated with this has been performed.
     * @throws OverlayException
     */
    public final boolean isDone() throws OverlayException
    {
        if (this.throwable != null)
        {
            throw new OverlayException(this.throwable.getMessage(), this.throwable);
        }
        return this.isDone;
    }

    /**
     * @see OverlayFuture
     */
    public void waitForBeingDone() throws OverlayException, InterruptedException
    {
        synchronized (this)
        {
            while (!this.isDone())
            {
                this.wait();
            }
        }
    }

}
