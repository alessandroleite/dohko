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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.PeerInfo;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.chord.EntryInsertedEvent;
import br.cic.unb.overlay.chord.EntryInsertedListener;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Provides methods which remote nodes can invoke.
 */
public abstract class Node
{
    /**
     * This is the id of this node. It has to be set by every implementation of this class!
     */
    private final AtomicReference<ID> nodeID = new AtomicReference<ID>();
    
    /**
     * The listeners interested in the entries of this node.
     */
    protected final ConcurrentMap<ID, EntryInsertedListener> thisNodeListeners = Maps.newConcurrentMap();

    /**
     * This is the url of this node. It has to be set by every implementation of this class!
     */
    private final URL nodeURL;
    
    /**
     * The start time in milliseconds.
     */
    private final long startTime;
    
    public Node(ID id, URL url)
    {
        this(url);
        nodeID.set(checkNotNull(id, "nodeID"));
    }

    public Node(URL nodeURL)
    {
        this.nodeURL = checkNotNull(nodeURL, "nodeURL");
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Returns the ID of a node.
     * 
     * @return ID of a node.
     */
    public final ID getNodeID()
    {
        return this.nodeID.get();
    }

    /**
     * @return
     */
    public final URL getNodeURL()
    {
        return this.nodeURL;
    }
    
    /**
     * Returns the uptime in milliseconds.
     * @return The uptime in milliseconds.
     */
    public final long getUptime()
    {
        return System.currentTimeMillis() - this.startTime;
    }

    /**
     * Returns the node which is responsible for the given key.
     * 
     * @param key
     *            Key for which the successor is searched for.
     * @return Responsible node.
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract Node findSuccessor(ID key) throws CommunicationException;

    /**
     * Requests this node's predecessor in result[0] and successor list in result[1..length-1]. This method is invoked by another node which thinks it
     * is this node's predecessor.
     * 
     * @param potentialPredecessor
     * @return A list containing the predecessor at first position of the list and the successors in the rest of the list.
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract List<Node> notify(Node potentialPredecessor) throws CommunicationException;

    /**
     * Requests this node's predecessor, successor list and entries.
     * 
     * @param potentialPredecessor
     *            Remote node which invokes this method
     * @return References to predecessor and successors and the entries this node will be responsible for.
     * @throws CommunicationException
     */
    public abstract RefsAndEntries notifyAndCopyEntries(Node potentialPredecessor) throws CommunicationException;

    /**
     * Requests a sign of live. This method is invoked by another node which thinks it is this node's successor.
     * 
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract PeerInfo ping() throws CommunicationException;

    /**
     * Stores the given object under the given ID.
     * 
     * @param entryToInsert
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract void insertEntry(Entry entryToInsert) throws CommunicationException;

    /**
     * Inserts replicates of the given entries.
     * 
     * @param entries
     *            The entries that are replicated.
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     * 
     */
    public abstract void insertReplicas(Set<Entry> entries) throws CommunicationException;

    /**
     * Removes the given object from the list stored under the given ID.
     * 
     * @param entryToRemove
     *            The entry to remove from the dht.
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract void removeEntry(Entry entryToRemove) throws CommunicationException;

    /**
     * Removes replicates of the given entries.
     * 
     * @param sendingNode
     *            ID of sending node; if entriesToRemove is empty, all replicas with ID smaller than the sending node's ID are removed
     * @param replicasToRemove
     *            Replicas to remove; if empty, all replicas with ID smaller than the sending node's ID are removed
     * 
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract void removeReplicas(ID sendingNode, Set<Entry> replicasToRemove) throws CommunicationException;

    /**
     * Returns all entries stored under the given ID.
     * 
     * @param id
     * @return A {@link Set} of entries associated with <code>id</code>.
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract Set<Entry> retrieveEntries(ID id) throws CommunicationException;

    /**
     * Informs a node that its predecessor leaves the network.
     * 
     * @param predecessor
     * @throws CommunicationException
     *             Thrown if a communication failure occurs.
     */
    public abstract void leavesNetwork(Node predecessor) throws CommunicationException;

    /**
     * Informs a node that an {@link Entry} was added.
     * 
     * @param event the event with the {@link Entry} added.
     * @throws CommunicationException if a communication failure occurs.
     */
    public abstract void notifyEntryListeners(final EntryInsertedEvent event) throws CommunicationException;
    
    public abstract void onEntryAdded(final EntryInsertedEvent event);
    
    /**
     * @param listenerNode
     * @throws CommunicationException
     */
    public abstract void notifyOnEntryAdded(Node listenerNode) throws CommunicationException;
    

    /**
     * @param nodeID
     *            the nodeID to set
     */
    protected final void setNodeID(ID nodeID)
    {
        if (!this.nodeID.compareAndSet(null, checkNotNull(nodeID)))
        {
            checkArgument(getNodeID().equals(nodeID), "Tried to change the ID of this node from: " + this.getNodeID() + " to: " + nodeID);
        }
    }

    public void registerEntryListener(ID id, EntryInsertedListener listener)
    {
        checkNotNull(id);
        checkNotNull(listener);
        
        thisNodeListeners.putIfAbsent(id, listener);
    }
    
    
    @Override
    public final boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Node))
        {
            return false;
        }
        
        Node other = (Node) obj;
        return Objects.equal(this.getNodeID(), other.getNodeID());
    }

    @Override
    public final int hashCode()
    {
        return this.nodeID.hashCode();
    }

    @Override
    public String toString()
    {
        String id = null;
        if (this.nodeID != null)
        {
            id = this.nodeID.toString();
        }
        
        String url = "null";
        
        if (this.nodeURL != null)
        {
            url = this.nodeURL.toString();
        }
        return "Node[type=" + this.getClass().getSimpleName() + ", id=" + id + ", url=" + url + "]";
    }
    
    @Override
    public void finalize() throws Throwable
    {
        super.finalize();
    }
}
