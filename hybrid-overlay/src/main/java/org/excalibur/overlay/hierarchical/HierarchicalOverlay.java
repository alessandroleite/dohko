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
package org.excalibur.overlay.hierarchical;


import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.excalibur.core.cloud.api.domain.Endpoint;
import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.discovery.ws.client.DiscoveryUtils;
import org.excalibur.overlay.hierarchical.election.ElectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.Key;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayBuilder;
import br.cic.unb.overlay.OverlayException;
import br.cic.unb.overlay.OverlayFuture;
import br.cic.unb.overlay.OverlayOperationCallback;
import br.cic.unb.overlay.OverlayRetrievalFuture;
import br.cic.unb.overlay.chord.EntryInsertedListener;
import br.cic.unb.overlay.chord.HashFunction;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static org.excalibur.discovery.ws.client.DiscoveryUtils.*;

import static org.excalibur.discovery.ws.client.DiscoveryClientBuilder.*;
import static org.excalibur.core.util.SystemUtils2.*;


public class HierarchicalOverlay implements Overlay
{
    private static final Logger LOG = LoggerFactory.getLogger(HierarchicalOverlay.class.getName());

    private final AtomicReference<Overlay>       delegate_ = new AtomicReference<Overlay>();
    private final AtomicReference<ElectionState> electionState_ = new AtomicReference<ElectionState>();

    private final AtomicBoolean                  joined_ = new AtomicBoolean();
    private final ProviderDetails                provider_;
    private final String                         internalAddress_;

    private Peer                                 bootstrap_;
    private NodeDetails                          thisNode_;

    public HierarchicalOverlay(ProviderDetails provider, String internalAddress)
    {
        this.provider_ = checkNotNull(provider);
        this.internalAddress_ = internalAddress;
        checkArgument(!isNullOrEmpty(this.internalAddress_), "Internal address may not be null!");
    }

    @Override
    public URL getURL()
    {
        return delegate_.get().getURL();
    }

    @Override
    public ID getID()
    {
        return delegate_.get().getID();
    }

    @Override
    public long getUptime()
    {
        return this.delegate_.get().getUptime();
    }

    @Override
    public void create(URL localURL) throws OverlayException
    {
        checkNotNull(localURL);
        this.create(localURL, HashFunction.getHashFunction().createID(localURL.toString().getBytes()));
    }

    @Override
    public void create(URL localURL, ID localID) throws OverlayException
    {
        checkNotNull(localURL);
        checkNotNull(localID);

        createOverlay(localURL);
    }

    @Override
    public void join(URL localURL, URL bootstrapURL) throws OverlayException
    {
        checkNotNull(localURL);
        checkNotNull(bootstrapURL);

        this.join(localURL, HashFunction.getHashFunction().createID(localURL.toString().getBytes()), bootstrapURL);
    }

    @Override
    public void join(URL localURL, ID localID, URL bootstrapURL) throws OverlayException
    {
        checkNotNull(localURL);
        checkNotNull(localID);
        checkNotNull(bootstrapURL);

        if (joined_.compareAndSet(false, true))
        {
            bootstrap_ = Peer.valueOf(HashFunction.getHashFunction().createID(bootstrapURL.toString().getBytes()), bootstrapURL);
            Overlay overlay = createOverlay(localURL);
            thisNode_ = new NodeDetails().setProvider(provider_);
            
            thisNode_.getAddresses().setExternal(Peer.valueOf(localID, localURL))
                                   .setInternal(Peer.valueOf(localID, URL.valueOf(this.internalAddress_, localURL.getPort())));

            NavigableSet<NodeDetails> peers = clusters(getDiscoveryEndpoint()).clustersOfProvider(provider_);
            NodeDetails superPeer = null;

            if (peers.isEmpty())
            {
                LOG.info("There is not a super-peer for provider {}", provider_.getName());
                superPeer = thisNode_;
                electionState_.set(new ElectionState(superPeer, superPeer, new Date()));

                overlay.join(localURL, bootstrapURL);

                String cluster = buildClustersNameFor(provider_);
                String name = buildClusterNameFor(provider_, superPeer);

                DiscoveryUtils.registerProvider(bootstrap_, provider_);
                DiscoveryUtils.registerResource(cluster, bootstrap_, thisNode_);
                DiscoveryUtils.registerResource(name, bootstrap_, thisNode_);
            }
            else
            {
                superPeer = getFirstDifferentOf(thisNode_, peers);

                if (superPeer != null)
                {
                    String clusterName = buildClusterNameFor(provider_, superPeer);
                    NavigableSet<NodeDetails> members = clusters(superPeer.getProvider().getEndpoint())
                    		.membersOfCluster(provider_.getName(), superPeer.getId());
                    
                    members.remove(superPeer);
                    
                    thisNode_.setParent(superPeer);
                    joinCluster(members, clusterName, thisNode_);
                }
            }
        }
    }

    protected void joinCluster(NavigableSet<NodeDetails> members, String cluster, NodeDetails node) throws OverlayException
    {
        NodeDetails bootstrap = getFirstDifferentOf(node, members);

        if (bootstrap != null)
        {
            LOG.info("Node [{}] will join the internal overlay through the node [{}]", node, bootstrap);
            getOverlay().join(node.getAddresses().getInternal().toURL(), bootstrap.getAddresses().getInternal().toURL());
        }
        
        else
        {
            LOG.info("First member [{}] of overlay [{}]", bootstrap, cluster);
            getOverlay().create(node.getAddresses().getInternal().toURL());
        }
        
        clusters(node.getParent().getProvider().getEndpoint()).addMember(provider_, node.getParent().getId(), node);
    }

    protected NodeDetails getFirstDifferentOf(NodeDetails node, NavigableSet<NodeDetails> nodes)
    {
        NodeDetails result = null;

        for (Iterator<NodeDetails> iter = nodes.iterator(); iter.hasNext();)
        {
            result = iter.next();

            if (!node.equals(result))
            {
                break;
            }
            else
            {
                result = null;
            }
        }

        return result;
    }

    @Override
    public void leave() throws OverlayException
    {
        if (joined_.compareAndSet(true, false))
        {
            DiscoveryUtils.removeResource(bootstrap_, thisNode_);
            delegate_.get().leave();
        }
    }

    @Override
    public void insert(Key key, Serializable object) throws OverlayException
    {
        delegate_.get().insert(key, object);
    }

    @Override
    public Set<Serializable> retrieve(Key key) throws OverlayException
    {
        return delegate_.get().retrieve(key);
    }

    @Override
    public void remove(Key key, Serializable object) throws OverlayException
    {
        delegate_.get().remove(key, object);
    }

    @Override
    public Peer find(ID id)
    {
        return this.getOverlay().find(id);
    }

    @Override
    public void registerEntryListener(EntryInsertedListener listener)
    {
    }

    @Override
    public void retrieve(Key key, OverlayOperationCallback callback)
    {
        delegate_.get().retrieve(key, callback);
    }

    @Override
    public void insert(Key key, Serializable entry, OverlayOperationCallback callback)
    {
        delegate_.get().insert(key, entry, callback);
    }

    @Override
    public void remove(Key key, Serializable entry, OverlayOperationCallback callback)
    {
        delegate_.get().remove(key, entry, callback);
    }

    @Override
    public OverlayRetrievalFuture retrieveAsync(Key key)
    {
        return delegate_.get().retrieveAsync(key);
    }

    @Override
    public OverlayFuture insertAsync(Key key, Serializable entry)
    {
        return delegate_.get().insertAsync(key, entry);
    }

    @Override
    public OverlayFuture removeAsync(Key key, Serializable entry)
    {
        return delegate_.get().removeAsync(key, entry);
    }

    @Override
    public Peer[] getNeighbors()
    {
        return delegate_.get().getNeighbors();
    }

    @Override
    public boolean isConnected()
    {
        return delegate_.get().isConnected();
    }
    
    @Override
    public Peer getBootStrap()
    {
        return delegate_.get().getBootStrap();
    }

    public NodeDetails getLeader()
    {
        return this.electionState_.get().getLeader();
    }

    private Overlay createOverlay(URL url)
    {
        Overlay overlay = OverlayBuilder.newBuilder().localAddress(url.getHost(), url.getPort()).build();

        if (!this.delegate_.compareAndSet(null, overlay))
        {
            overlay = this.delegate_.get();
        }

        return overlay;
    }
    
    protected Endpoint getDiscoveryEndpoint()
    {
        return Endpoint.valueOf(System.getProperty("org.excalibur.discovery.address", System.getProperty("org.excalibur.overlay.bootstrap.address")),
                getIntegerProperty("org.excalibur.discovery.port", getIntegerProperty("org.excalibur.server.port", 8080)));
    }

    protected Overlay getOverlay()
    {
        return this.delegate_.get();
    }
}
