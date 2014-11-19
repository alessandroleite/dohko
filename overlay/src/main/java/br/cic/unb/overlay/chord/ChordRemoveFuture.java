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
import java.util.concurrent.Executor;

import br.cic.unb.overlay.Key;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayFuture;

/**
 * Implementation of {@link OverlayFuture} for removal of an entry from the chord distributed hash table.
 */
class ChordRemoveFuture extends ChordFutureImpl
{

    /**
     * The instance of chord used for the invocation represented by this.
     */
    private final Overlay chord;

    /**
     * The key used for the insertion.
     */
    private final Key key;

    /**
     * The entry to remove.
     */
    private final Serializable entry;

    /**
     * @param c
     * @param k
     * @param entry
     */
    private ChordRemoveFuture(Overlay c, Key k, Serializable entry)
    {
        this.chord = c;
        this.key = k;
        this.entry = entry;
    }

    /**
     * Factory method to create an instance of this class. This method also prepares execution of the removal with help of the provided
     * {@link Executor} <code>exec</code>.
     * 
     * @param exec
     *            The executor that should asynchronously execute the removal of <code>entry</code> with key <code>k</code>.
     * 
     * @param c
     *            The instance of {@link Overlay} that should be used to remove <code>entry</code>.
     * @param k
     *            The {@link Key} for <code>entry</code>.
     * @param entry
     *            The entry to be removed.
     * @return Instance of this class.
     */
    final static ChordRemoveFuture create(Executor exec, Overlay c, Key k, Serializable entry)
    {
        if (c == null)
        {
            throw new IllegalArgumentException("ChordRemoveFuture: chord instance must not be null!");
        }
        if (k == null)
        {
            throw new IllegalArgumentException("ChordRemoveFuture: key must not be null!");
        }
        if (entry == null)
        {
            throw new IllegalArgumentException("ChordRemoveFuture: entry must not be null!");
        }

        ChordRemoveFuture f = new ChordRemoveFuture(c, k, entry);
        exec.execute(f.getTask());
        return f;
    }

    /**
     * @return The runnable that executes the operation associated with this.
     */
    private final Runnable getTask()
    {
        return new RemoveTask(this.chord, this.key, this.entry);
    }

    /**
     * Runnable to execute the removal of entry with help of chord.
     */
    private class RemoveTask implements Runnable
    {

        /**
         * The instance of chord used for the invocation represented by this.
         */
        private Overlay chord;

        /**
         * The key used for the insertion.
         */
        private Key key;

        /**
         * The entry to remove.
         */
        private Serializable entry;

        /**
         * @param chord
         * @param key
         * @param entry
         */
        RemoveTask(Overlay chord, Key key, Serializable entry)
        {
            this.chord = chord;
            this.key = key;
            this.entry = entry;
        }

        public void run()
        {
            try
            {
                this.chord.remove(this.key, this.entry);
            }
            catch (Throwable t)
            {
                setThrowable(t);
            }
            setIsDone();
        }
    }
}
