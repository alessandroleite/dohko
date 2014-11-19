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
package br.cic.unb.chord.communication.net;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.net.RemoteNodeInfo;

/**
 * This class represents entries and {@link RemoteNodeInfo references} that have to be transferred between two nodes.
 */
public final class RemoteRefsAndEntries implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 5120333239284923176L;

    /**
     * Set of {@link Entry}.
     * 
     */
    protected Set<Entry> entries;

    /**
     * List of {@link RemoteNodeInfo}.
     * 
     */
    protected List<RemoteNodeInfo> nodeInfos;

    /**
     * @param entries1
     * @param nodeInfos1
     */
    public RemoteRefsAndEntries(Set<Entry> entries1, List<RemoteNodeInfo> nodeInfos1)
    {
        this.entries = entries1;
        this.nodeInfos = nodeInfos1;
    }

    /**
     * @return Returns the entries.
     */
    public Set<Entry> getEntries()
    {
        return this.entries;
    }

    /**
     * @param entries1
     *            The entries to set.
     */
    protected void setEntries(Set<Entry> entries1)
    {
        this.entries = entries1;
    }

    /**
     * @return Returns the nodeInfos.
     */
    public List<RemoteNodeInfo> getNodeInfos()
    {
        return this.nodeInfos;
    }

    /**
     * @param nodeInfos1
     *            The nodeInfos to set.
     */
    protected void setNodeInfos(List<RemoteNodeInfo> nodeInfos1)
    {
        this.nodeInfos = nodeInfos1;
    }
}
