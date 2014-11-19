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
 * <p>
 * This is the interface that must be implemented by classes that can be used as callback for method invocations on {@link AsynChord}.
 * </p>
 * 
 * <p>
 * An instance of this must be passed as parameter to on of the methods:
 * <ul>
 * <li>{@link Overlay#insert(Key, Serializable, OverlayOperationCallback)}</li>
 * <li>{@link Overlay#remove(Key, Serializable, OverlayOperationCallback)}</li>
 * <li>{@link Overlay#retrieve(Key, ChordCallback)}</li>
 * </ul>
 * On termination of those methods the corresponding callback method on this is called. These methods are:
 * <ul>
 * <li>{@link #inserted(Key, Serializable, Throwable)}</li>
 * <li>{@link #removed(Key, Serializable, Throwable)}</li>
 * <li>{@link #retrieved(Key, Set, Throwable)}</li>
 * </ul>
 * The {@link Throwable} parameter of these methods is <code>null</code> if the corresponding method has been executed successfully.
 * </p>
 */
public interface OverlayOperationCallback
{

    /**
     * This is the callback method for retrieval of values associated with <code>key</code>. This method is called when an invocation of
     * {@link AsynChord#retrieve(Key, ChordCallback)} has finished.
     * 
     * @param key
     *            The {@link Key} that has been used for the retrieval.
     * @param entries
     *            The retrieved entries. Empty Set, if no values are associated with <code>key</code>.
     * @param t
     *            Any {@link Throwable} that occurred during execution of {@link Overlay#retrieve(Key, OverlayOperationCallback)}. This is <code>null</code> if
     *            retrieval of <code>key</code> was successful.
     */
    public void retrieved(Key key, Set<Serializable> entries, Throwable t);

    /**
     * This method is called, when a call to {@link AsynChord#insert(Key, Serializable, OverlayOperationCallback)} has been finished.
     * 
     * @param key
     *            The {@link Key} that should be used for insertion.
     * @param entry
     *            The entry that should be inserted.
     * @param t
     *            Any {@link Throwable} that occurred during execution of {@link Overlay#insert(Key, Serializable, OverlayOperationCallback)}. This is
     *            <code>null</code> if insertion of <code>key</code> and <code>entry</code> was succesful.
     */
    public void inserted(Key key, Serializable entry, Throwable t);

    /**
     * This is the callback method for removal of the <code>entry</code> with <code>key</code>.
     * 
     * @param key
     *            The {@link Key} of the entry that should be removed.
     * @param entry
     *            The entry that should be removed.
     * @param t
     *            Any {@link Throwable} that occurred during execution of {@link Overlay#remove(Key, Serializable, OverlayOperationCallback)}. This is
     *            <code>null</code> if removal of <code>entry</code> was successful.
     */
    public void removed(Key key, Serializable entry, Throwable t);

}
