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

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicReference;

import org.excalibur.discovery.domain.Addresses;
import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.ws.client.DiscoveryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.ProxyFactory;
import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;

public class LeaderElectorImpl implements LeaderElector
{
    private static final Logger LOG = LoggerFactory.getLogger(LeaderElectorImpl.class.getName());

    @SuppressWarnings("unused")
    private final Overlay overlay;
    private final LeaderElectionStrategy electionStrategy;
    private final AtomicReference<ElectionState> electionState = new AtomicReference<ElectionState>();
    private final ElectionStateFactory electionStateFactory;
    private final Peer bootstrap;
    private String provider_;

    public LeaderElectorImpl(Peer bootstrap, Overlay overlay, ElectionStateFactory electionStateFactory)
    {
        this.bootstrap = checkNotNull(bootstrap);
        this.overlay = checkNotNull(overlay);
        this.electionStateFactory = checkNotNull(electionStateFactory);

        this.electionStrategy = new AntiHerdingElectionStrategy();
    }

    @Override
    public void electNewLeader() throws OverlayException
    {
        NavigableSet<NodeDetails> leaders = getValidLeaders();

        if (!isAlive(getLeader().getAddresses()))
        {
            NodeDetails leader = getLeader();
            LOG.info("Leader node {} is not alive!", leader);

            leader = electionStrategy.findNextLeader(leaders, getNode());
            electionState.set(getState().valueOf(leader));
        }

        if (electionStrategy.isLeader(leaders, this.getNode()))
        {
            LOG.info("Node {} is the elected leader!", getNode());
        }

        LOG.info("Local node is: {}; leader node: {}", getNode(), getLeader());
    }

    /**
     * Returns the leaders that are currently alive.
     * 
     * @return A non-null set with the leaders that are currently alive.
     * @throws OverlayException
     *             if a communication error occurs. For instance, the bootstrap server is not alive.
     */
    protected NavigableSet<NodeDetails> getValidLeaders() throws OverlayException
    {
        NavigableSet<NodeDetails> members = DiscoveryUtils.getSuperPeers(this.bootstrap, provider_);

        for (Iterator<NodeDetails> iter = members.iterator(); iter.hasNext();)
        {
            NodeDetails member = iter.next();

            if (!isAlive(member.getAddresses()))
            {
                iter.remove();
            }
        }

        return members;
    }

   

    public boolean isAlive(Addresses address)
    {
        boolean isAlive = true;

        if (!this.getNode().getAddresses().getExternal().equals(address.getExternal()))
        {
            try
            {
                Node proxy = ProxyFactory.create(URL.valueOf(getNode().getAddresses().getExternal().getHost(), getNode().getAddresses().getExternal().getPort()),
                        URL.valueOf(address.getExternal().getHost(), address.getExternal().getPort()));

                proxy.ping();
            }
            catch (CommunicationException e)
            {
                isAlive = false;
            }
        }
        return isAlive;
    }

    @Override
    public void initialize() throws OverlayException
    {
        ElectionState initialState = electionStateFactory.create();
        electionState.set(initialState);
    }

    @Override
    public ElectionState getState()
    {
        return this.electionState.get();
    }

    @Override
    public boolean isLeader()
    {
        return this.getNode().equals(electionState.get().getLeader());
    }

    @Override
    public NodeDetails getNode()
    {
        return getState().getNode();
    }

    @Override
    public NodeDetails getLeader()
    {
        return getState().getLeader();
    }
}
