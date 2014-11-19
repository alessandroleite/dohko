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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Entry;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.Proxy;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Stores all remote references of nodes the local node is connected to and provides methods for querying and manipulating these references. Makes use
 * of one finger table, one successor list, and one predecessor reference.
 */
final class References
{
    /**
     * Object LOG.
     */
    private final Logger logger;

    /**
     * This node's finger table.
     */
    private final FingerTable fingerTable;

    /**
     * This node's successor list
     */
    private final SuccessorList successorList;

    /**
     * This node's predecessor.
     */
    private Node predecessor;

    /**
     * This node's local ID.
     */
    private final ID localID;

    private final URL localURL;

    private final Entries entries;

    /**
     * Creates an References object which contains no references.
     * 
     * @param locID
     *            ID of local node. Must not be <code>null</code>.
     * @param numberOfEntriesInSuccessorList
     *            Length of successor list to be created. Must be greater or equal 1!
     * @param entries
     *            Reference on this nodes' entries which is passed to creation of the successor list. Must not be <code>null</code>.
     * @throws IllegalArgumentException
     *             If any parameters is <code>null</code> or if number of entries in successor list is less than 1.
     */
    References(ID locID, URL locURL, int numberOfEntriesInSuccessorList, Entries entries)
    {
        checkArgument(locID != null && locURL != null && entries != null);
        checkArgument(numberOfEntriesInSuccessorList >= 1, "Number of entries in successor list cannot be less than 1! "
                + numberOfEntriesInSuccessorList + " is not a valid value!");

        this.logger = LoggerFactory.getLogger(References.class.getName() + "." + locID);

        this.localID = locID;
        this.localURL = locURL;
        this.entries = entries;

        this.fingerTable = new FingerTable(locID, this);
        this.successorList = new SuccessorList(locID, numberOfEntriesInSuccessorList, this, entries);
    }

    /**
     * Determines the closest preceding node for the given ID based on finger table, successor list, and predecessor, but without testing the node's
     * liveliness.
     * 
     * @param key
     *            ID to find closest preceding node for.
     * @throws NullPointerException
     *             If ID is <code>null</code>.
     * @return Reference on closest preceding node.
     */
    final synchronized Node getClosestPrecedingNode(ID key)
    {
        checkNotNull(key, "ID may not be null!");

        Map<ID, Node> foundNodes = new HashMap<ID, Node>();
        // determine closest preceding reference of finger table
        Node closestNodeFT = this.fingerTable.getClosestPrecedingNode(key);
        if (closestNodeFT != null)
        {
            foundNodes.put(closestNodeFT.getNodeID(), closestNodeFT);
        }

        // determine closest preceding reference of successor list
        Node closestNodeSL = this.successorList.getClosestPrecedingNode(key);
        if (closestNodeSL != null)
        {
            foundNodes.put(closestNodeSL.getNodeID(), closestNodeSL);
        }

        // predecessor is appropriate only if it precedes the given id
        Node predecessorIfAppropriate = null;
        if (this.predecessor != null && key.isInInterval(this.predecessor.getNodeID(), this.localID))
        {
            predecessorIfAppropriate = this.predecessor;
            foundNodes.put(this.predecessor.getNodeID(), predecessor);
        }

        // with three references which may be null, there are eight (8) cases we
        // have to enumerate...
        Node closestNode = null;
        List<ID> orderedIDList = new ArrayList<ID>(foundNodes.keySet());
        orderedIDList.add(key);
        int sizeOfList = orderedIDList.size();
        // size of list must be greater than one to not only contain the key.
        // if (sizeOfList > 1) {

        /*
         * Sort list in ascending order
         */
        Collections.sort(orderedIDList);
        /*
         * The list item with one index lower than that of the key must be the id of the closest predecessor or the key.
         */
        int keyIndex = orderedIDList.indexOf(key);
        /*
         * As all ids are located on a ring if the key is the first item in the list we have to select the last item as predecessor with help of this
         * calculation.
         */
        int index = (sizeOfList + (keyIndex - 1)) % sizeOfList;
        /*
         * Get the references to the node from the map of collected nodes.
         */
        ID idOfclosestNode = orderedIDList.get(index);
        closestNode = foundNodes.get(idOfclosestNode);
        if (closestNode == null)
        {
            throw new NullPointerException("closestNode must not be null!");
        }

        if (logger.isDebugEnabled())
        {
            this.logger.debug("Closest preceding node of ID " + key + " at node " + this.localID.toString() + " is " + closestNode.getNodeID()
                + " with closestNodeFT=" + (closestNodeFT == null ? "null" : "" + closestNodeFT.getNodeID()) + " and closestNodeSL="
                + (closestNodeSL == null ? "null" : "" + closestNodeSL.getNodeID()) + " and predecessor (only if it precedes given ID)="
                + (predecessorIfAppropriate == null ? "null" : "" + predecessorIfAppropriate.getNodeID()));
        }

        return closestNode;
    }

    /**
     * Adds the given node reference to the finger table and successor list, if appropriate. The reference is NOT set as predecessor, even if is
     * closer to this node. Therefore use {@link #addReferenceAsPredecessor(Node)}.
     * 
     * @param newReference
     *            Reference to be added to the local data structures.
     * @throws NullPointerException
     *             If the given reference is <code>null</code>.
     */
    final synchronized boolean addReference(Node newReference)
    {
        boolean result = false;
        checkNotNull(newReference, "newReference");

        if (!newReference.getNodeID().equals(this.localID))
        {
            this.checkIfProxy(newReference);

            this.fingerTable.addReference(newReference);
            this.successorList.addSuccessor(newReference);

            result = true;
        }
        return result;
    }

    /**
     * Removes the given node reference from the finger table and the successor list. If the given reference is the current predecessor, the
     * predecessor reference will be <code>null</code> afterwards.
     * 
     * @param oldReference
     *            Reference to remove from ALL data structures.
     * @throws NullPointerException
     *             If reference to remove is <code>null</code>.
     */
    final synchronized void removeReference(Node oldReference)
    {
        checkNotNull(oldReference);

        this.fingerTable.removeReference(oldReference);
        this.successorList.removeReference(oldReference);

        if (oldReference.equals(this.predecessor))
        {
            this.predecessor = null;
        }

        this.logger.debug("Attempted to remove reference [{}] from all data structures including predecessor reference.", oldReference);

        disconnectIfUnreferenced(oldReference);
    }

    /**
     * Closes the connection to the given reference, if it is not kept in any data structure (ie. finger table, successor list, predecessor) any more.
     * 
     * @param removedReference
     *            Node to which the connection shall be closed, if there exists no reference any more.
     * @throws NullPointerException
     *             If the given reference is <code>null</code>.
     */
    void disconnectIfUnreferenced(Node removedReference)
    {
        if (!this.containsReference(removedReference))
        {
            this.logger.debug("Disconnecting unused reference on node [{}]" , removedReference);

            try
            {
                removedReference.finalize();
            }
            catch (Throwable e)
            {
                logger.error("Error on finalizing the reference: [{}]", removedReference);
            }
        }
    }

    /**
     * Determines this node's direct successor and returns it. If no successor is known, <code>null</code> is returned.
     * 
     * @return The local node's direct successor, or <code>null</code> if no successor is known.
     */
    final synchronized Node getSuccessor()
    {
        // direct successor is the first entry in my successor list
        return this.successorList.getDirectSuccessor();
    }

    /**
     * Returns a formatted string of the IDs of all references stored on this node. This includes references in the finger table and successor list as
     * well as the predecessor.
     * 
     * @return Formatted string of references.
     */
    public synchronized String toString()
    {
        StringBuilder result = new StringBuilder("Node: " + this.localID.toString() + ", " + this.localURL + "\n");
        result.append(this.fingerTable != null ? this.fingerTable.toString() : " null finger table ");

        result.append(this.successorList != null ? this.successorList.toString() : " null successor list! ");

        result.append("Predecessor: ");
        if (this.predecessor != null)
        {
            result.append(this.predecessor.getNodeID() + ", " + this.predecessor.getNodeURL());
        }
        else
        {
            result.append("null");
        }
        return result.toString();
    }

    /**
     * Returns the reference on this node's predecessor, if available. If no predecessor exists for this node, <code>null</code> is returned.
     * 
     * @return Reference on this node's predecessor, if available. If no predecessor exists for this node, <code>null</code> is returned.
     */
    final synchronized Node getPredecessor()
    {
        return this.predecessor;
    }

    /**
     * Sets the given reference as this node's predecessor. If the former value of this predecessor's node was <code>null</code> and if this reference
     * is not used any more (eg. in finger table or successor list), the connection to it is closed.
     * 
     * @param potentialPredecessor
     *            Reference on the node to be set as new predecessor; may not be null
     * @throws NullPointerException
     *             If potential predecessor is null.
     */
    final synchronized void setPredecessor(Node potentialPredecessor)
    {
        checkNotNull(potentialPredecessor, "Potential predecessor of method setPredecessor may not be null!");
        this.checkIfProxy(potentialPredecessor);

        if (!(potentialPredecessor.equals(this.predecessor)))
        {
            Node formerPredecessor = this.predecessor;
            this.predecessor = potentialPredecessor;

            if (formerPredecessor != null)
            {
                this.disconnectIfUnreferenced(formerPredecessor);

                /*
                 * The replicas, which are in the range between the old and the new predecessor, on the last successor of this node have to be removed
                 * if the successor list is full. => capacity of sl == length of sl.
                 */
                int sLSize = this.successorList.getSize();
                if (this.successorList.getCapacity() == sLSize)
                {
                    Node lastSuccessor = this.successorList.getReferences().get(sLSize - 1);
                    try
                    {
                        lastSuccessor.removeReplicas(this.predecessor.getNodeID(), new HashSet<Entry>());
                    }
                    catch (CommunicationException e)
                    {
                        logger.warn("Could not remove replicas on last predecessor", e);
                    }
                }
                this.logger.debug("Old predecessor " + formerPredecessor + " was replaced by " + potentialPredecessor);
            }
            else
            {
                this.logger.info("Predecessor reference set to " + potentialPredecessor + "; was null before.");
                Set<Entry> entriesToRep = this.entries.getEntriesInInterval(this.predecessor.getNodeID(), this.localID);
                List<Node> successors = this.successorList.getReferences();
                for (Node successor : successors)
                {
                    try
                    {
                        successor.insertReplicas(entriesToRep);
                    }
                    catch (CommunicationException e)
                    {
                        this.logger.warn("Could not replicate to successor " + successor.getNodeID(), e);
                    }
                }
            }
        }
    }

    /**
     * Returns an unmodifiable list of this node's successors.
     * 
     * @return Unmodifiable successor list.
     */
    final synchronized List<Node> getSuccessors()
    {
        return this.successorList.getReferences();
    }

    final synchronized Node[] getFingerTable()
    {
        return this.fingerTable.getFingerTableEntries();
    }

    /**
     * Determines if the given reference exists in any one the data structure, i.e. finger table, successor list, or predecessor reference.
     * 
     * @param newReference
     *            Reference to look up.
     * @throws NullPointerException
     *             If given reference is <code>null</code>.
     * @return <code>true</code> if the reference is contained and <code>false</code> if not.
     */
    final synchronized boolean containsReference(Node newReference)
    {
        checkNotNull(newReference);

        return (this.fingerTable.containsReference(newReference) || this.successorList.containsReference(newReference) || newReference
                .equals(this.predecessor));
    }

    /**
     * Returns a formatted string of this node's finger table.
     * 
     * @return String representation of finger table.
     */
    final String printFingerTable()
    {
        return this.fingerTable.toString();
    }

    /**
     * Returns a formatted string of this node's successor list.
     * 
     * @return String representation of successor list.
     */
    final String printSuccessorList()
    {
        return this.successorList.toString();
    }

    /**
     * Adds the given node reference to the finger table and successor list AND sets it as new predecessor reference, if appropriate. Appropriateness
     * is given if either the former predecessor reference was <code>null</code> or the new reference's ID is located between the old predecessor ID
     * and this node's ID. Even if not appropriate as predecessor, the reference is added to finger table and successor list.
     * 
     * @param potentialPredecessor
     *            Reference which should be this node's predecessor.
     * @throws NullPointerException
     *             If the given reference is <code>null</code>.
     */
    void addReferenceAsPredecessor(Node potentialPredecessor)
    {
        if (!potentialPredecessor.getNodeID().equals(this.localID))
        {
            this.checkIfProxy(potentialPredecessor);
            
            checkNotNull(potentialPredecessor, "Reference to potential predecessor may not be null!");

            // if the local node did not have a predecessor reference before
            // or if the potential predecessor is closer to this local node,
            // replace the predecessor reference
            if (this.predecessor == null || potentialPredecessor.getNodeID().isInInterval(this.predecessor.getNodeID(), this.localID))
            {
                this.logger.info("Setting a new predecessor reference: New reference is {}; the old reference was {}", potentialPredecessor,
                        (this.predecessor == null ? "null" : this.predecessor));
                // replace predecessor reference
                this.setPredecessor(potentialPredecessor);
            }
            // add reference
            this.addReference(potentialPredecessor);
        }
    }

    /**
     * Determines the first i entries in the finger table.
     * 
     * @param i
     * @return The first (i+1) entries of finger table. If there are fewer then i+1 entries only these are returned.
     */
    public List<Node> getFirstFingerTableEntries(int i)
    {
        return this.fingerTable.getFirstFingerTableEntries(i);
    }

    /**
     * This data structure is supposed to work with remote references therefore it must be instances of Proxy. This method is used in every method
     * that adds a new reference to this to check that it is an instance of Proxy. ->
     * 
     * @param node
     * @throws RuntimeException
     */
    private void checkIfProxy(Node node)
    {
        if (!(node instanceof Proxy))
        {
            throw new RuntimeException("Trying to use local node " + node + " with references.");
        }
    }
}
