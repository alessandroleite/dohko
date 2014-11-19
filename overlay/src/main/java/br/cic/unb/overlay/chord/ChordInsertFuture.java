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

import static com.google.common.base.Preconditions.*;

/**
 * Implementation of {@link OverlayFuture} for {@link Chord#insertAsync(Key, Serializable)}.
 */
class ChordInsertFuture extends ChordFutureImpl
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
     * The entry to insert.
     */
    private final Serializable entry;

    /**
     * 
     * @param c
     *            The instance of chord used for the invocation represented by this.
     * @param k
     *            The key used for the insertion.
     * @param entry
     *            The entry to insert.
     */
    private ChordInsertFuture(Overlay c, Key k, Serializable entry)
    {
        this.chord = c;
        this.key = k;
        this.entry = entry;
    }

    /**
     * Factory method to create an instance of this class. This method also prepares execution of the insertion with help of the provided
     * {@link Executor} <code>exec</code>.
     * 
     * @param exec
     *            The executor that should asynchronously execute the insertion of <code>entry</code> with key <code>k</code>.
     * 
     * @param c
     *            The instance of {@link Overlay} that should be used to insert <code>entry</code>.
     * @param k
     *            The {@link Key} for <code>entry</code>.
     * @param entry
     *            The entry to be inserted.
     * @return Instance of this class.
     */
    final static ChordInsertFuture create(Executor exec, Overlay c, Key k, Serializable entry)
    {
        
        checkNotNull(c, "ChordInsertFuture: chord instance must not be null!");
        checkNotNull(k, "ChordInsertFuture: key must not be null!");
        checkNotNull(entry, "ChordInsertFuture: entry must not be null!");

        ChordInsertFuture f = new ChordInsertFuture(c, k, entry);
        exec.execute(f.getTask());
        return f;
    }

    /**
     * 
     * @return A Runnable that executes the operation associated with this.
     */
    private final Runnable getTask()
    {
        return new InsertTask(this.chord, this.key, this.entry);
    }

    /**
     * Runnable that executes the insertion.
     */
    private class InsertTask implements Runnable
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
         * The entry to insert.
         */
        private Serializable entry;

        /**
         * Private constructor.
         * 
         * @param chord
         * @param key
         * @param entry
         */
        InsertTask(Overlay chord, Key key, Serializable entry)
        {
            this.chord = chord;
            this.key = key;
            this.entry = entry;
        }

        public void run()
        {
            try
            {
                this.chord.insert(this.key, this.entry);
            }
            catch (Throwable t)
            {
                setThrowable(t);
            }
            setIsDone();
        }
    }

}
