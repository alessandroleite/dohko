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
package org.excalibur.overlay.hierarchical.election;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.excalibur.discovery.domain.NodeDetails;

public class ElectionState
{
    /**
     * The current node.
     */
    private final NodeDetails node;

    /**
     * The leader of the node.
     */
    private final NodeDetails leader;

    private Date electionTime;

    public ElectionState(NodeDetails node, NodeDetails leader, Date electionDate)
    {
        this.node = checkNotNull(node);
        this.leader = leader;
        this.electionTime = new Date(electionDate.getTime());
    }

    public static ElectionState valueOf(NodeDetails node, NodeDetails leader)
    {
        return new ElectionState(node, checkNotNull(leader), new Date());
    }
    
    public ElectionState valueOf(NodeDetails leader)
    {
        return valueOf(getNode(), checkNotNull(leader));
    }

    /**
     * Sets the node's leader and returns a new {@link ElectionState} with the leader reference.
     * 
     * @param leader
     *            The leader of the node. Must not be <code>null</code>.
     * @param electionTime The time when the election occurred.
     * @return new {@link ElectionState} with the reference for the newest leader.
     */
    public ElectionState setNewLeader(NodeDetails leader, Date electionTime)
    {
        return new ElectionState(this.node, leader, electionTime);
    }
    
    public boolean isLeader(NodeDetails node)
    {
        return this.getLeader().equals(node);
    }

    /**
     * @return the node
     */
    public NodeDetails getNode()
    {
        return node;
    }

    /**
     * @return the leader
     */
    public NodeDetails getLeader()
    {
        return leader;
    }

    /**
     * @return the electionTime
     */
    public Date getElectionTime()
    {
        return electionTime;
    }

    /**
     * @param electionTime
     *            the electionTime to set
     */
    public void setElectionTime(Date electionTime)
    {
        this.electionTime = electionTime;
    }
    
}
