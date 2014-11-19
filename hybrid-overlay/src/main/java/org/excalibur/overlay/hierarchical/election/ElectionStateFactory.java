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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.overlay.Key;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;
import br.cic.unb.overlay.chord.StringKey;

public class ElectionStateFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(ElectionStateFactory.class.getName());

    private final Overlay overlay_;
    private final Key     leadersKey_;
    private final String  provider_;

    public ElectionStateFactory(Overlay overlay, String provider, String leadersPath)
    {
        this.overlay_ = overlay;
        this.leadersKey_ = new StringKey(leadersPath);
        this.provider_ = provider;
    }

    public ElectionState create() throws OverlayException
    {
//        NodeDetails node = new NodeDetails(NetworkAddress.valueOf(overlay_.getID(), overlay_.getURL()));
//        NodeDetails leader;
//
//        NavigableSet<NodeDetails> leaders = new TreeSet<NodeDetails>(DiscoveryUtils.deserialize(overlay_.retrieve(leadersKey_)));
//
//        if (leaders.isEmpty())
//        {
//            Serializable description = NodeUtils.serialize(((StringKey) leadersKey_).getValue(), node);
//            overlay_.insert(leadersKey_, description);
//            LOG.info("Created the leader {}", description);
//
//            Set<Serializable> retrieve = overlay_.retrieve(new StringKey(String.format(NodeUtils.CLUSTER_NAME_PATH, provider_, node.getID())));
//            NavigableSet<NodeDetails> children = NodeUtils.deserialize(retrieve);
//            leader = ElectionUtils.findNextLeader(node, children);
//        }
//        else
//        {
//            leader = leaders.first();
//        }
//
//        return ElectionState.valueOf(node, leader);
        return null;
    }
    
    public Key getLeadersKey()
    {
        return leadersKey_;
    }
}
