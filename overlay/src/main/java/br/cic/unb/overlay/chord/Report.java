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

/**
 * Provides the user application with methods for retrieving internal information about the state of a Chord node, e.g. entries or references.
 * 
 */
public interface Report
{
    /**
     * Returns a formatted String containing all entries stored on this node.
     * 
     * @return Formatted String containing all entries stored on this node.
     */
    public abstract String printEntries();

    /**
     * Returns a formatted String containing all references stored in the finger table of this node.
     * 
     * @return Formatted String containing all references stored in the finger table of this node.
     */
    public abstract String printFingerTable();

    /**
     * Returns a formatted String containing all references stored in the successor list of this node.
     * 
     * @return Formatted String containing all references stored in the successor list of this node.
     */
    public abstract String printSuccessorList();

    /**
     * Returns a formatted String containing all references stored on this node.
     * 
     * @return Formatted String containing all references stored on this node.
     */
    public abstract String printReferences();

    /**
     * Returns a formatted String containing the predecessor reference of this node.
     * 
     * @return Formatted String containing the predecessor reference of this node.
     */
    public abstract String printPredecessor();
}
