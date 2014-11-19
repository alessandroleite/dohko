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

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Executor;

import br.cic.unb.overlay.Key;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;
import br.cic.unb.overlay.OverlayRetrievalFuture;

/**
 * Implementation of {@link OverlayRetrievalFuture}.
 */
class ChordRetrievalFutureImpl extends ChordFutureImpl implements OverlayRetrievalFuture
{

    /**
     * The result of the retrieval request associated with this.
     */
    private Set<Serializable> result;

    /**
     * The chord instance used for the operation that is associated with this.
     */
    private final Overlay chord;

    /**
     * The key to retrieve the associated entries for.
     */
    private final Key key;

    /**
     * 
     * @param c
     * @param k
     */
    private ChordRetrievalFutureImpl(Overlay c, Key k)
    {
        super();
        this.chord = c;
        this.key = k;
    }

    /**
     * 
     * @param r
     */
    final void setResult(Set<Serializable> r)
    {
        this.result = r;
    }

    /**
     * @see OverlayRetrievalFuture
     */
    public final Set<Serializable> getResult() throws OverlayException, InterruptedException
    {
        synchronized (this)
        {
            while (!this.isDone())
            {
                this.wait();
            }
        }
        Throwable t = this.getThrowable();
        if (t != null)
        {
            throw new OverlayException(t.getMessage(), t);
        }
        return this.result;
    }

    /**
     * 
     * @return Runnable that performs the retrieve operation.
     */
    private Runnable getTask()
    {
        return new RetrievalTask(this.chord, this.key);
    }

    /**
     * Factory method to create an instance of this class. This method also prepares execution of the retrieval with help of the provided
     * {@link Executor} <code>exec</code>.
     * 
     * @param exec
     *            The executor that should asynchronously execute the retrieval of entries with key <code>k</code>.
     * @param c
     *            The {@link Overlay} instance to be used for retrieval.
     * @param k
     *            The {@link Key} for which the entries should be retrieved.
     * @return An instance of this.
     */
    final static ChordRetrievalFutureImpl create(Executor exec, Overlay c, Key k)
    {
        if (c == null)
        {
            throw new IllegalArgumentException("OverlayRetrievalFuture: chord instance must not be null!");
        }
        if (k == null)
        {
            throw new IllegalArgumentException("OverlayRetrievalFuture: key must not be null!");
        }

        ChordRetrievalFutureImpl future = new ChordRetrievalFutureImpl(c, k);
        exec.execute(future.getTask());
        return future;
    }

    /**
     * Runnable to execute the retrieval of entries associated with key from chord.
     * 
     * @author sven
     * @version 1.0
     */
    private class RetrievalTask implements Runnable
    {

        /**
         * The chord instance used for the operation that is associated with this.
         */
        private Overlay chord = null;

        /**
         * The key to retrieve the associated entries for.
         */
        private Key key = null;

        /**
         * @param chord
         * @param key
         */
        private RetrievalTask(Overlay chord, Key key)
        {
            this.chord = chord;
            this.key = key;
        }

        public void run()
        {
            try
            {
                setResult(this.chord.retrieve(this.key));
            }
            catch (Throwable t)
            {
                setThrowable(t);
            }
            setIsDone();
        }
    }

}
