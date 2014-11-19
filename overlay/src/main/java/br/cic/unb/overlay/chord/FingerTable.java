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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;

/**
 * Stores references for remote nodes and provides methods for querying and manipulating this table.
 */
final class FingerTable
{
    /**
     * ID of local node.
     */
    private final ID localID;

    /**
     * Finger table data.
     */
    private final Node[] remoteNodes;

    /**
     * Reference on parent object.
     */
    private final References references;

    /**
     * Object LOG.
     */
    private final Logger logger;

    /**
     * Creates an initially empty finger table. The table size is determined by the given ID's length. A reference on the parent object of type
     * References is stored for being able to determine and disconnect unused references after removing them from the table.
     * 
     * @param localID
     *            ID of local node.
     * @param references
     *            Reference on parent object.
     * @throws IllegalArgumentException
     *             If either of the parameters is <code>null</code>.
     */
    FingerTable(ID localID, References references)
    {
        checkArgument(localID != null && references != null);
        this.logger = LoggerFactory.getLogger(FingerTable.class + "." + localID);

        this.references = references;
        this.localID = localID;
        this.remoteNodes = new Node[localID.getLength()];
    }

    /**
     * Sets one table entry to the given reference.
     * 
     * @param index
     *            Index of table entry.
     * @param proxy
     *            Reference to store.
     * @throws ArrayIndexOutOfBoundsException
     *             If given index is not contained in the finger table.
     * @throws NullPointerException
     *             If given reference is <code>null</code>.
     */
    private final void setEntry(int index, Node proxy)
    {

        if (index < 0 || index >= this.remoteNodes.length)
        {
            String message = String.format("The method: setEntry was invoked with an index out of array bounds; index=%s, length of array=%s", index, this.remoteNodes.length);
            this.logger.error(message);
            throw new ArrayIndexOutOfBoundsException(message);
        }

        checkNotNull(proxy, "Reference to proxy may not be null!");

        this.remoteNodes[index] = proxy;
        
        this.logger.debug("Entry {} set to {}", index, proxy.toString());
    }

    /**
     * Returns the reference stored at the given index.
     * 
     * @param index
     *            Index of entry to be returned.
     * @throws ArrayIndexOutOfBoundsException
     *             If given index is not contained in the finger table.
     * @return Reference stored at the given index.
     */
    private final Node getEntry(int index)
    {

        if (index < 0 || index >= this.remoteNodes.length)
        {
            String message = String.format("The method: getEntry was invoked with an index out of array bounds; index=%s, length of array=%s", index, this.remoteNodes.length);
            this.logger.error(message);
            throw new ArrayIndexOutOfBoundsException(message);
        }

        return this.remoteNodes[index];
    }

    /**
     * Sets the reference at the given index to <code>null</code> and triggers to disconnect that node, if no other reference to it is kept any more.
     * 
     * @param index
     *            Index of entry to be set to <code>null</code>.
     * @throws ArrayIndexOutOfBoundsException
     *             If given index is not contained in the finger table.
     */
    private final void unsetEntry(int index)
    {
        if (index < 0 || index >= this.remoteNodes.length)
        {
            String message = String.format("The method: unsetEntry was invoked with an index out of array bounds; index=%s, length of array=%s", index, this.remoteNodes.length);
            this.logger.error(message);
            throw new ArrayIndexOutOfBoundsException(message);
        }

        Node overwrittenNode = this.getEntry(index);

        this.remoteNodes[index] = null;

        if (overwrittenNode == null)
        {
            this.logger.debug("unsetEntry did not change anything, because entry was null before.");
        }
        else
        {
            this.logger.debug("Entry {} set to null. Previous value: {}", index, overwrittenNode.toString());
        }
    }

    /**
     * Adds the given reference to all finger table entries of which the start index is in the interval (local node ID, new node ID) and of which the
     * current entry is <code>null</code> or further away from the local node ID than the new node ID (ie. the new node ID is in the interval (local
     * node ID, currently stored node ID) ).
     * 
     * @param proxy
     *            Reference to be added to the finger table.
     * @throws NullPointerException
     *             If given reference is <code>null</code>.
     */
    final void addReference(Node proxy)
    {
        checkNotNull(proxy, "Reference to add may not be null!");

        for (int i = 0; i < this.remoteNodes.length; i++)
        {
            ID startOfInterval = this.localID.addPowerOfTwo(i);
            if (!startOfInterval.isInInterval(this.localID, proxy.getNodeID()))
            {
                break;
            }

            if (getEntry(i) == null)
            {
                setEntry(i, proxy);
            }
            else if (proxy.getNodeID().isInInterval(this.localID, getEntry(i).getNodeID()))
            {
//                final Node oldEntry = getEntry(i);
                
                setEntry(i, proxy);
                
//                if (oldEntry != null)
//                {
//                    this.references.disconnectIfUnreferenced(oldEntry);
//                }
            }
        }
    }

    /**
     * Returns a copy of the finger table entries.
     * 
     * @return Copy of finger table entries.
     */
    final Node[] getCopyOfReferences()
    {
        this.logger.debug("Returning copy of references.");

        Node[] copy = new Node[this.remoteNodes.length];
        System.arraycopy(this.remoteNodes, 0, copy, 0, this.remoteNodes.length);
        return copy;
    }

    /**
     * Returns a formatted string representation of this finger table.
     * 
     * @return String representation containing one line per reference, together with the annotation which table entries contain this reference.
     */
    @Override
    public final String toString()
    {
        StringBuilder result = new StringBuilder("Finger table:\n");

        int lastIndex = -1;
        ID lastNodeID = null;
        URL lastNodeURL = null;
        for (int i = 0; i < this.remoteNodes.length; i++)
        {
            Node next = this.remoteNodes[i];
            if (next == null)
            {
                // row ended or did not even start
                if ((lastIndex != -1) && (lastNodeID != null))
                {
                    // row ended
                    result.append("  " + lastNodeID + ", " + lastNodeURL + " "
                            + ((i - 1 - lastIndex > 0) ? "(" + lastIndex + "-" + (i - 1) + ")" : "(" + (i - 1) + ")") + "\n");
                    lastIndex = -1;
                    lastNodeID = null;
                    lastNodeURL = null;
                }
            }
            else if (lastNodeID == null)
            {
                // found first reference in a row
                lastIndex = i;
                lastNodeID = next.getNodeID();
                lastNodeURL = next.getNodeURL();
            }
            else if (!lastNodeID.equals(next.getNodeID()))
            {
                // found different reference in finger table
                result.append("  " + lastNodeID + ", " + lastNodeURL + " "
                        + ((i - 1 - lastIndex > 0) ? "(" + lastIndex + "-" + (i - 1) + ")" : "(" + (i - 1) + ")") + "\n");
                lastNodeID = next.getNodeID();
                lastNodeURL = next.getNodeURL();
                lastIndex = i;
            }
        }

        // display last row
        if (lastNodeID != null && lastIndex != -1)
        {
            // row ended
            result.append("  "
                    + lastNodeID
                    + ", "
                    + lastNodeURL
                    + " "
                    + ((this.remoteNodes.length - 1 - lastIndex > 0) ? "(" + lastIndex + "-" + (this.remoteNodes.length - 1) + ")" : "("
                            + (this.remoteNodes.length - 1) + ")") + "\n");
            lastNodeID = null;
        }

        return result.toString();
    }

    /**
     * Removes all occurrences of the given node from finger table.
     * 
     * @param node
     *            Reference to be removed from the finger table.
     * @throws NullPointerException
     *             If given reference is <code>null</code>.
     */
    final void removeReference(Node node)
    {
        checkNotNull(node, "node");

        // determine node reference with next larger ID than ID of node reference to remove
        Node referenceForReplacement = null;
        for (int i = this.localID.getLength() - 1; i >= 0; i--)
        {
            Node n = this.getEntry(i);
            if (node.equals(n))
            {
                break;
            }
            if (n != null)
            {
                referenceForReplacement = n;
            }
        }

        // remove reference(s)
        for (int i = 0; i < this.remoteNodes.length; i++)
        {
            if (node.equals(this.remoteNodes[i]))
            {
                if (referenceForReplacement == null)
                {
                    unsetEntry(i);
                }
                else
                {
                    setEntry(i, referenceForReplacement);
                }
            }
        }

        // try to add references of successor list to fill 'holes' in finger table
        List<Node> referencesOfSuccessorList = new ArrayList<Node>(this.references.getSuccessors());
        referencesOfSuccessorList.remove(node);
        for (Node referenceToAdd : referencesOfSuccessorList)
        {
            this.addReference(referenceToAdd);
        }
    }

    /**
     * Determines closest preceding node of given id.
     * 
     * @param key
     *            ID of which the closest preceding node shall be determined.
     * @throws NullPointerException
     *             If given key is null.
     * @return Reference to the node which most closely precedes the given ID. <code>null</code> if no node has been found.
     */
    final Node getClosestPrecedingNode(ID key)
    {
        checkNotNull(key, "key");

        for (int i = this.remoteNodes.length - 1; i >= 0; i--)
        {
            if (this.remoteNodes[i] != null && this.remoteNodes[i].getNodeID().isInInterval(this.localID, key))
            {
                this.logger.debug("Closest preceding node for ID {} is {}", key, this.remoteNodes[i].toString());
                return this.remoteNodes[i];
            }
        }
        this.logger.debug("There is no closest preceding node for ID {} -- returning null", key);

        return null;
    }

    /**
     * Determines if the given reference is stored somewhere in the finger table.
     * 
     * @param newReference
     *            Reference of which existence shall be determined.
     * @throws NullPointerException
     *             If reference to look for is <code>null</code>.
     * @return <code>true</code>, if the given reference exists in the finger table, or <code>false</code>, else.
     */
    final boolean containsReference(Node newReference)
    {
        checkNotNull(newReference, "newReference");

        for (int i = 0; i < this.remoteNodes.length; i++)
        {
            if (newReference.equals(this.remoteNodes[i]))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param i
     * @return The first (i+1) entries of finger table. If there are fewer then i+1 entries only these are returned.
     */
    final List<Node> getFirstFingerTableEntries(int i)
    {
        Set<Node> result = new HashSet<Node>();
        for (int j = 0; j < this.remoteNodes.length; j++)
        {
            if (this.getEntry(j) != null)
            {
                result.add(this.getEntry(j));
            }
            if (result.size() >= i)
            {
                break;
            }
        }
        return new ArrayList<Node>(result);
    }

    final Node[] getFingerTableEntries()
    {
        Map<ID, Node> entries = Maps.newHashMap();

        for (int i = 0; i < this.remoteNodes.length; i++)
        {
            if (this.remoteNodes[i] != null)
            {
                entries.put(this.remoteNodes[i].getNodeID(), this.remoteNodes[i]);
            }
        }
        return Lists.newArrayList(entries.values()).toArray(new Node[entries.size()]);
    }
}
