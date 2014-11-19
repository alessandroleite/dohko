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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.data.ID;

import com.google.common.base.Preconditions;

/**
 * Stores references on the next nodes in the Chord ring and provides methods for querying and manipulating this list.
 */
final class SuccessorList
{
    /**
     * List storing the successor references in correct order.
     */
    private final List<Node> successors;

    /**
     * Local node ID - initialized in constructor.
     */
    private final ID localID;

    /**
     * Maximum number of references - initialized in constructor.
     */
    private final int capacity;

    /**
     * Reference on parent object of type References.
     */
    private final References references;

    /**
     * Reference on Entries.
     */
    private final Entries entries;

    /**
     * Object LOG.
     */
    private final Logger logger;

    /**
     * Creates an empty list of successors.
     * 
     * @param localID
     *            This node's ID; is used for comparison operations of other node references.
     * @param numberOfEntries
     *            Number of entries to be stored in this successor list.
     * @param parent
     *            Reference on this objects parent.
     * @param entries
     *            Reference on the entry repository for replication purposes.
     */
    SuccessorList(ID localID, int numberOfEntries, References parent, Entries entries)
    {
        this.logger = LoggerFactory.getLogger(SuccessorList.class.getName() + "." + localID);

        Preconditions.checkState(localID != null && parent != null && entries != null);
        Preconditions.checkState(numberOfEntries >= 1, "SuccessorList has to be at least of length 1! " + numberOfEntries + "is not a valid value!");

        this.localID = localID;
        this.capacity = numberOfEntries;
        this.successors = new CopyOnWriteArrayList<Node>();
        this.references = parent;
        this.entries = entries;

    }

    /**
     * Adds a successor references, preserving ordering of list elements.
     * 
     * @param nodeToAdd
     *            Reference to be added.
     * @throws NullPointerException
     *             If node to add is <code>null</code>.
     */
    final void addSuccessor(Node nodeToAdd)
    {
        Preconditions.checkNotNull(nodeToAdd, "Node to add may not be null!");

        if (this.localID.equals(nodeToAdd.getNodeID()))
        {
            return;
        }

        if (this.successors.contains(nodeToAdd))
        {
            this.logger.debug("Reference to new node {} is not added to successor list, because it is already contained.", nodeToAdd.toString());
            return;
        }

        // if new ID is between last ID in list and this node's own ID _AND_
        // successor list already has maximum allowed list, the new reference IS
        // NOT added!
        if (this.successors.size() >= this.capacity
                && nodeToAdd.getNodeID().isInInterval(this.successors.get(this.successors.size() - 1).getNodeID(), this.localID))
        {
            if (logger.isDebugEnabled())
            {
                this.logger.debug("Reference to new node {} is not added to successor list, because the list is already full and the " +
                                  "new reference is further away from the local node than all other successors.", nodeToAdd.toString());
            }
            return;
        }

        // insert node to successors

        // insert before an existing element?
        boolean inserted = false;

        for (int i = 0; i < this.successors.size() && !inserted; i++)
        {
            if (nodeToAdd.getNodeID().isInInterval(this.localID, this.successors.get(i).getNodeID()))
            {
                this.successors.add(i, nodeToAdd);
                inserted = true;
            }
        }

        // insert at end if list not long enough
        if (!inserted)
        {
            this.successors.add(nodeToAdd);
            inserted = true;
        }

        // determine ID range of entries this node is responsible for
        // and replicate them on new node
        ID fromID;
        Node predecessor = this.references.getPredecessor();
        if (predecessor != null)
        {
            // common case: have a predecessor
            fromID = predecessor.getNodeID();
        }
        else
        {
            // have no predecessor
            // do I have any preceding node?
            Node precedingNode = this.references.getClosestPrecedingNode(this.localID);
            if (precedingNode != null)
            {
                // use ID of preceding node
                fromID = precedingNode.getNodeID();
            }
            else
            {
                // use own ID (leads to replicating the whole ring); should not
                // happen
                fromID = this.localID;
            }
        }

        // replicate entries from determined ID up to local ID
        ID toID = this.localID;
        Set<Entry> entriesToReplicate = this.entries.getEntriesInInterval(fromID, toID);
        if (!entriesToReplicate.isEmpty())
        {
            try
            {
                nodeToAdd.insertReplicas(entriesToReplicate);
                this.logger.debug("Inserted replicas on node {}", nodeToAdd);
            }
            catch (CommunicationException e)
            {
                this.logger.warn("Entries could not be replicated to node {}!" + nodeToAdd, e);
            }
        }

        // remove last element from this.successors, if maximum exceeded

        if (this.successors.size() > this.capacity)
        {
            Node nodeToDelete = this.successors.get(this.successors.size() - 1);
            this.successors.remove(nodeToDelete);

            // determine ID range of entries this node is responsible
            // for and remove replicates of them from discarded successor
            //
            // Could be replaced by a new method:
            // nodeToDelete.removeReplicas(fromID, toID);
            // Set<Entry> replicatedEntries = this.entries.getEntriesInInterval(
            // fromID, toID);
            try
            {
                // remove all replicas!
                nodeToDelete.removeReplicas(this.localID, new HashSet<Entry>());
                this.logger.debug("Removed replicas from node {}", nodeToDelete);
            }
            catch (CommunicationException e)
            {
                this.logger.warn("Could not remove the replicas from node {}!", nodeToDelete, e);
            }

            this.logger.debug("If no other reference to node {} exists any more, it is disconnected.");
            
            // 06/05/2014
            this.references.disconnectIfUnreferenced(nodeToDelete);
        }

    }

    /**
     * Removes a successor reference without leaving a gap in the list
     * 
     * @param nodeToDelete
     *            Reference to be removed.
     * @throws NullPointerException
     *             If reference to remove is <code>null</code>.
     */
    final void removeReference(Node nodeToDelete)
    {
        if (nodeToDelete == null)
        {
            NullPointerException e = new NullPointerException("Reference to remove may not be null!");
            this.logger.error("Null pointer", e);
            throw e;
        }
        this.successors.remove(nodeToDelete);

        // try to add references of finger table to fill 'hole' in successor list
        List<Node> referencesOfFingerTable = this.references.getFirstFingerTableEntries(this.capacity);
        referencesOfFingerTable.remove(nodeToDelete);
        for (Node referenceToAdd : referencesOfFingerTable)
        {
            this.addSuccessor(referenceToAdd);
        }
    }

    /**
     * Returns an unmodifiable copy of the ordered successor list, starting with the closest following node.
     * 
     * @return Unmodifiable copy of successor list.
     */
    final List<Node> getReferences()
    {
        return Collections.unmodifiableList(this.successors);
    }

    /**
     * Returns a string representation of this successor list.
     * 
     * @return String representation of list.
     */
    public final String toString()
    {
        StringBuilder result = new StringBuilder("Successor List:\n");

        for (Node next : this.successors)
        {
            result.append("  " + next.getNodeID().toString() + ", " + next.getNodeURL() + "\n");
        }
        return result.toString();
    }

    /**
     * Returns closest preceding node of given ID.
     * 
     * @param idToLookup
     *            ID of which closest preceding node is sought-after.
     * @throws NullPointerException
     *             If ID to look up is <code>null</code>.
     * @return Reference on closest preceding node of given ID.
     */
    final Node getClosestPrecedingNode(ID idToLookup)
    {

        if (idToLookup == null)
        {
            NullPointerException e = new NullPointerException("ID to look up may not be null!");
            this.logger.error("Null pointer", e);
            throw e;
        }

        for (int i = this.successors.size() - 1; i >= 0; i--)
        {
            Node nextNode = this.successors.get(i);
            if (nextNode.getNodeID().isInInterval(this.localID, idToLookup))
            {
                return nextNode;
            }
        }

        return null;
    }

    /**
     * Determines if the given reference is contained in this successor list.
     * 
     * @param nodeToLookup
     *            Reference to look up.
     * @throws NullPointerException
     *             If node to look up is <code>null</code>.
     * @return <code>true</code>, if reference is contained, and <code>false</code>, else.
     */
    final boolean containsReference(Node nodeToLookup)
    {
        Preconditions.checkNotNull(nodeToLookup, "The node to look up was null");
        return this.successors.contains(nodeToLookup);
    }

    /**
     * Returns the reference on the direct successor; may be <code>null</code>, if the list is empty.
     * 
     * @return Direct successor (or <code>null</code> if list is empty).
     */
    final Node getDirectSuccessor()
    {
        if (this.successors.isEmpty())
        {
            return null;
        }
        return this.successors.get(0);
    }

    /**
     * @return the capacity
     */
    final int getCapacity()
    {
        return capacity;
    }

    final int getSize()
    {
        return this.successors.size();
    }

}
