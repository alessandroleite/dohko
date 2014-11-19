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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.excalibur.core.compute.monitoring.domain.CpuSocketState;
import org.excalibur.core.compute.monitoring.domain.Machine;
import org.excalibur.core.compute.monitoring.domain.MemoryState;
import org.excalibur.core.compute.monitoring.domain.builders.HardwareBuilder;
import org.excalibur.core.compute.monitoring.monitors.resources.CpuMonitor;
import org.excalibur.core.compute.monitoring.monitors.resources.MemoryMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.RefsAndEntries;
import br.cic.unb.chord.communication.net.RemoteNodeInfo;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.PeerInfo;
import br.cic.unb.chord.data.URL;

import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implements all operations which can be invoked remotely by other nodes.
 */
public final class NodeImpl extends Node
{
    /**
     * The name of the LOG is the name of this class with the nodeID appended. The length of the nodeID depends on the number of
     * bytes that are displayed when the ID is shown in Hex-Representation.
     */
    private final Logger LOG;
    
    /**
     * Endpoint for incoming communication.
     */
    private final Endpoint nodeEndpoint;

    /**
     * Reference on local node.
     */
    private final Chord impl;

    /**
     * Routing table (including finger table, successor list, and predecessor reference)
     */
    private final References references;

    /**
     * Repository for locally stored entries.
     */
    private final Entries entries;

    /**
     * Executor that executes insertion and removal of entries on successors of this node.
     */
    private Executor asyncExecutor;
    
    /**
     * Executor that notifies the registered thisNodeListeners of this node.
     */
    private Executor asyncNotifyExecutor;
    
    /**
	 * The
	 */
    private final Lock notifyLock;
    
    private final Machine machine;

    /**
     * This node's master.
     */
    private Node master;

    private boolean IS_REPLICA_ENABLE = Boolean.valueOf(System.getProperty("br.cic.unb.chord.service.dht.replica.enable", "true"));

    /**
     * Creates that part of the local node which answers remote requests by other nodes. Sole constructor, is invoked by {@link Chord} only.
     * 
     * @param impl
     *            Reference on Chord instance which created this object.
     * @param nodeID
     *            This node's Chord ID.
     * @param nodeURL
     *            URL, on which this node accepts connections.
     * @param references
     *            Routing table of this node.
     * @param entries
     *            Repository for entries of this node.
     * @throws IllegalArgumentException
     *             If any of the parameter has value <code>null</code>.
     */
    NodeImpl(Chord impl, ID nodeID, URL nodeURL, References references, Entries entries)
    {
        super(nodeID, nodeURL);
        this.LOG = LoggerFactory.getLogger(NodeImpl.class.getName() + "." + nodeID.toString());
        
        checkNotNull(references);
        checkNotNull(entries);
        checkNotNull(impl);

        this.impl = impl;
        
        this.asyncExecutor = impl.getAsyncExecutor();
        this.asyncNotifyExecutor = impl.getAsyncNotifyExecutor();
        
        this.references = references;
        this.entries = entries;
        this.notifyLock = new ReentrantLock(true);

        this.nodeEndpoint = Endpoint.createEndpoint(this, nodeURL);

        try
        {
            this.nodeEndpoint.listen();
        }
        catch (CommunicationException exception)
        {
            // raise it?
            LOG.error("Error on starting listen for incoming messages from other nodes. Message: {}", exception.getMessage(), exception);
        }
        
        Machine machine = null;
        
        try
        {
            machine = HardwareBuilder.machine();
        }
        catch (Throwable throwable)
        {
        }
        
        this.machine = machine;
    }

    /**
     * Makes this endpoint accept entries by other nodes. Is invoked by Chord only.
     */
    final void acceptEntries()
    {
        this.nodeEndpoint.acceptEntries();
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public final void disconnect()
    {
        this.nodeEndpoint.disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Node findSuccessor(ID key)
    {
        return this.impl.findSuccessor(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Node> notify(Node potentialPredecessor)
    {
        this.notifyLock.lock();
        try
        {
            List<Node> result = new LinkedList<Node>();

            // add reference on predecessor as well as on successors to result
            if (this.references.getPredecessor() != null)
            {
                result.add(this.references.getPredecessor());
            }
            else
            {
                result.add(potentialPredecessor);
            }
            
            result.addAll(this.references.getSuccessors());

            // add potential predecessor to successor list, finger table, and
            // set it as predecessor if there isn't a predecessor available.
            this.references.addReferenceAsPredecessor(potentialPredecessor);
            
            return result;
        }
        finally
        {
            this.notifyLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final RefsAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException
    {
        this.notifyLock.lock();
        try
        {
            // copy all entries which lie between the local node ID and the ID
            // of the potential predecessor, including those equal to potential predecessor
            Set<Entry> copiedEntries = this.entries.getEntriesInInterval(this.getNodeID(), potentialPredecessor.getNodeID());

            return new RefsAndEntries(this.notify(potentialPredecessor), copiedEntries);
        }
        finally
        {
            this.notifyLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PeerInfo ping()
    {
        PeerInfo data = null;
        
        if (machine != null && machine.cpus().length > 0)
        {
            CpuMonitor cpuMonitor = new CpuMonitor();
            CpuSocketState cpuState = cpuMonitor.probe(machine.cpus()[0]);
            
            MemoryState[] memoryState = new MemoryMonitor().probe();
            machine.ram().setState(memoryState[0]);
            machine.swap().setState(memoryState[1]);
            
            data = new PeerInfo(Peer.valueOf(this.getNodeID(), this.getNodeURL()), cpuState, memoryState, cpuMonitor.getUptime());
        }
        
        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void insertEntry(Entry newEntry) throws CommunicationException
    {
        LOG.debug("Inserting entry with id {} at node {}",  newEntry.getId(), this.getNodeID());

        if (this.getMaster() != null)
        {
            this.getMaster().insertEntry(newEntry);
            return;
        }

        if ((this.references.getPredecessor() != null) && !newEntry.getId().isInInterval(this.references.getPredecessor().getNodeID(), this.getNodeID()))
        {
            this.references.getPredecessor().insertEntry(newEntry);
            return;
        }

        // add entry to local repository
        this.entries.add(newEntry);
        
        EntryInsertedEvent event = new EntryInsertedEvent(new RemoteNodeInfo(this.getNodeURL(), this.getNodeID()), newEntry);
        this.notifyEntryListeners(event);

        if (IS_REPLICA_ENABLE)
        {
            // create a set containing this entry for insertion of replicates at all nodes in successor list.
            final Set<Entry> newEntriesToReplicate = Sets.newHashSet(newEntry);

            // invoke insertReplicates method on all nodes in successor list
            for (final Node successor : this.references.getSuccessors())
            {
                this.asyncExecutor.execute(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            successor.insertReplicas(newEntriesToReplicate);
                        }
                        catch (CommunicationException e)
                        {
                            LOG.warn("Error on replicating the newest entries. Error message: [{}] ", e.getMessage(), e);
                        }
                    }
                });
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void insertReplicas(Set<Entry> replicatesToInsert)
    {
        this.entries.addAll(replicatesToInsert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeEntry(Entry entryToRemove) throws CommunicationException
    {
        this.LOG.debug("Removing entry with id {} ant node {}", entryToRemove.getId(), this.getNodeID());

        if (this.references.getPredecessor() != null
                && !entryToRemove.getId().isInInterval(this.references.getPredecessor().getNodeID(), this.getNodeID()))
        {
            this.references.getPredecessor().removeEntry(entryToRemove);
            return;
        }

        this.entries.remove(entryToRemove);

        // create set containing this entry for removal of replicates at all nodes in successor list
        final Set<Entry> entriesToRemove = new HashSet<Entry>();
        
        entriesToRemove.add(entryToRemove);

        // invoke removeReplicates method on all nodes in successor list
        List<Node> successors = this.references.getSuccessors();
        final ID id = this.getNodeID();
        
        for (final Node successor : successors)
        {
            this.asyncExecutor.execute(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        successor.removeReplicas(id, entriesToRemove);
                    }
                    catch (CommunicationException e)
                    {
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeReplicas(ID sendingNodeID, Set<Entry> replicasToRemove)
    {
        if (replicasToRemove.size() == 0)
        {
            LOG.debug("Removing replicas. Current no. of entries: {}", this.entries.getNumberOfStoredEntries());
            Set<Entry> allReplicasToRemove = this.entries.getEntriesInInterval(this.getNodeID(), sendingNodeID);
            
            LOG.debug("Replicas to remove {}", allReplicasToRemove);
            LOG.debug("Size of replicas to remove {}", allReplicasToRemove.size());
            
            this.entries.removeAll(allReplicasToRemove);
        }
        else
        {
            this.entries.removeAll(replicasToRemove);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<Entry> retrieveEntries(ID id) throws CommunicationException
    {
        if (this.references.getPredecessor() != null && !id.isInInterval(this.references.getPredecessor().getNodeID(), this.getNodeID()))
        {
            return this.references.getPredecessor().retrieveEntries(id);
        }
        return this.entries.getEntries(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void leavesNetwork(Node predecessor)
    {
        this.references.removeReference(this.references.getPredecessor());
    }

    /**
     * Return the reference of this node's master, if available. If no master exists for this node <code>null</code> is returned.
     * 
     * @return Reference of this node's master, if available. If no master exists for this node <code>null</code> is returned.
     */
    final synchronized Node getMaster()
    {
        return this.master;
    }

    /**
     * Sets the given reference as this node's master.
     * 
     * @param potentialMasterNode
     *            Reference on the node to be set as new Master; may not be <code>null</code>
     * @throws NullPointerException
     *             If potential potentialMaster is <code>null</code>
     */
    final synchronized void setMaster(final Node potentialMaster)
    {
        checkNotNull(potentialMaster);
        
        if (potentialMaster.getNodeID().equals(this.getNodeID()))
        {
            return;
        }

        this.master = potentialMaster;
    }

    /**
     * 
     * @return
     */
    final Executor getAsyncExecutor()
    {
        return this.asyncExecutor;
    }
    
    /**
     * Notifies the register thisNodeListeners about an entry inserted on this node.
     * @param event
     */
    public void notifyEntryListeners(final EntryInsertedEvent event)
    {
        for (final EntryInsertedListener listener: thisNodeListeners.values())
        {
            this.asyncNotifyExecutor.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        listener.onEntryInserted(event);
                    }
                    catch (Throwable t)
                    {
                        LOG.debug("Exception raised by an entry listener: {}", t.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void notifyOnEntryAdded(final Node listenerNode) throws CommunicationException
    {
        this.thisNodeListeners.put(listenerNode.getNodeID(), new EntryInsertedListener()
        {
            @Override
            public void onEntryInserted(final EntryInsertedEvent event)
            {
                try
                {
                    listenerNode.notifyEntryListeners(event);
                }
                catch (Throwable e)
                {
                    thisNodeListeners.remove(listenerNode);
                    LOG.error(e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void onEntryAdded(EntryInsertedEvent event)
    {
        this.impl.onEntryInserted(event);
    }
}
