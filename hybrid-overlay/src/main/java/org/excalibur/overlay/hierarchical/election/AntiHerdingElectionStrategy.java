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

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.excalibur.discovery.domain.NodeDetails;

public class AntiHerdingElectionStrategy implements LeaderElectionStrategy
{
    @Override
    public NodeDetails findNextLeader(NavigableSet<NodeDetails> members, NodeDetails node)
    {
        NavigableSet<NodeDetails> nodesSortedByUptime = new TreeSet<NodeDetails>(new UptimeNodeComparator());
        nodesSortedByUptime.addAll(members);
        
        NodeDetails leader = members.isEmpty() ? node : nodesSortedByUptime.last();

        return leader == null ? node : leader;
    }

    @Override
    public boolean isLeader(NavigableSet<NodeDetails> nodes, NodeDetails node)
    {
        return nodes.isEmpty() ? true : nodes.first().equals(node);
    }

    public static class UptimeNodeComparator implements Comparator<NodeDetails>
    {
        @Override
        public int compare(NodeDetails o1, NodeDetails o2)
        {
            return o1.getUptime().compareTo(o2.getUptime());
        }
    }
}
