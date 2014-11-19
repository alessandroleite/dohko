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
package org.excalibur.core.exec;

/**
 * Destroys all registered {@link Process} after a certain event, typically when the JVM exits.
 */
public interface ProcessDestroyer
{
    /**
     * Returns <code>true</code> if the specified {@link Process} was successfully added to the list of processes to be destroy.
     * 
     * @param process
     *            the process to add
     * @return <code>true</code> if the specified {@link Process} was successfully added.
     */
    boolean add(Process process);

    /**
     * Returns <code>true</code> if the specified {@link Process} was successfully removed from the list of processes to be destroy.
     * 
     * @param process
     *            the process to remove
     * @return <code>true</code> if the specified {@link Process} was successfully removed.
     */
    boolean remove(Process process);

    /**
     * Returns the number of registered processes.
     * 
     * @return the number of register process
     */
    int size();
}
