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
package org.excalibur.overlay.hierarchical.grouping;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;

import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.ws.client.DiscoveryUtils;
import org.jgroups.PhysicalAddress;
import org.jgroups.annotations.Property;
import org.jgroups.conf.ClassConfigurator;
import org.jgroups.protocols.Discovery;
import org.jgroups.stack.IpAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.data.Peer;

import com.google.common.collect.Lists;

public class REST_PING extends Discovery
{
    private static final Logger LOG = LoggerFactory.getLogger(REST_PING.class.getName());

    @Property(description = "The URL to retrieve the membership list.")
    protected String server_url;

    @Property(description = "Name of the provider")
    protected String provider_name; 

    @Property(description = "Number of additional ports to be probed for membership. A port_range of 0 does not probe additional ports. "
            + "Example: initial_hosts=A[7800] port_range=0 probes A:7800, port_range=1 probes A:7800 and A:7801")
    private int port_range = 50;

    @Property(description = "The port number being used for cluster membership. The default is 7800.")
    private int port_number = 7800;

    static
    {
        ClassConfigurator.addProtocol((short) 800, REST_PING.class);
    }

    @Override
    public void init() throws Exception
    {
        checkState(!isNullOrEmpty(server_url));
    }

    @Override
    public Collection<PhysicalAddress> fetchClusterMembers(String clusterName)
    {
        List<PhysicalAddress> clusterMembers = Lists.newArrayList();

        for (Peer address : getMembers(clusterName))
        {
            for (int i = port_number; i < port_number + port_range; i++)
            {
                try
                {
                    clusterMembers.add(new IpAddress(address.getHost(), i));
                }
                catch (UnknownHostException e)
                {
                    if (LOG.isDebugEnabled())
                    {
                        LOG.debug("Host %s:%d is unknown", address.getHost(), i, e.getMessage(), e);
                    }
                }
            }
        }

        return clusterMembers;
    }

    protected List<Peer> getMembers(String clusterName)
    {
        List<Peer> peers = Lists.newArrayList();
        
        NavigableSet<NodeDetails> superPeers = DiscoveryUtils.getSuperPeers(this.server_url, this.provider_name);
        
//        NavigableSet<NodeDetails> members = DiscoveryUtils.listClusterMembers(server_url, provider_name, clusterName);
        
        for (NodeDetails node: superPeers)
        {
            peers.add(new Peer().setHost(node.getAddresses().getExternal().getHost()));
        }
        
        return peers;
    }

    @Override
    public boolean sendDiscoveryRequestsInParallel()
    {
        return true;
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }
}
