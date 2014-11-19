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
package br.cic.unb.chord.communication;

import java.util.List;
import java.util.Set;

public final class RefsAndEntries implements java.io.Serializable
{
    private static final long serialVersionUID = -2144146590744444954L;

    /**
     * List containing the predecessor (first in this list) and successors of a node.
     */
    private List<Node> refs; 

    /**
     * The entries a node is responsible for.
     */
    private Set<Entry> entries; 

    /**
     * @param refs1
     * @param entries1
     */
    public RefsAndEntries(List<Node> refs1, Set<Entry> entries1)
    {
        this.refs = refs1;
        this.entries = entries1;
    }

    /**
     * @return Returns the entries.
     */
    public Set<Entry> getEntries()
    {
        return this.entries;
    }

    /**
     * Returns references to the nodes contained within this instance.
     * 
     * @return List containing the predecessor (first in this list) and successors of a node.
     * 
     */
    public List<Node> getRefs()
    {
        return this.refs;
    }
}
