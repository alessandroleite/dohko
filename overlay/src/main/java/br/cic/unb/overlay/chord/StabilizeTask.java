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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.RefsAndEntries;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Invokes notify method on successor.
 */
final class StabilizeTask implements Runnable
{

    protected final static Logger LOG = LoggerFactory.getLogger(StabilizeTask.class);

    /**
     * Parent object for performing stabilization.
     */
    private NodeImpl parent;

    /**
     * Reference on routing table.
     */
    private References references;

    private Entries entries;

    /**
     * Creates a new instance, but without starting a thread running it.
     * 
     * @param parent
     *            Parent object for performing stabilization.
     * @param references
     *            Reference on routing table.
     * @throws NullPointerException
     *             If either of the parameters is <code>null</code>.
     */
    StabilizeTask(NodeImpl parent, References references, Entries entries)
    {
        checkNotNull(parent);
        checkNotNull(references);
        checkNotNull(entries);
        
        this.parent = parent;
        this.references = references;
        this.entries = entries;
    }

    @Override
    public void run()
    {
        try
        {
//            Node successor = this.references.getSuccessor();
            List<Node> successors = Lists.newArrayList(this.references.getSuccessors());
            
            for (Node successor: successors)
            {
                if (successor != null)
                {
                    List<Node> mySuccessorsPredecessorAndSuccessorList;
                    
                    try
                    {
                        mySuccessorsPredecessorAndSuccessorList = successor.notify(this.parent);
                    }
                    catch (CommunicationException e)
                    {
                        this.references.removeReference(successor);
                        continue;
                    }
                    
                    if ((mySuccessorsPredecessorAndSuccessorList.size() > 0) && (mySuccessorsPredecessorAndSuccessorList.get(0) != null))
                    {

                        if (!this.parent.getNodeID().equals(mySuccessorsPredecessorAndSuccessorList.get(0).getNodeID()))
                        {
                            RefsAndEntries refsAndEntries = successor.notifyAndCopyEntries(this.parent);
                            mySuccessorsPredecessorAndSuccessorList = refsAndEntries.getRefs();
                            this.entries.addAll(refsAndEntries.getEntries());
                        }
                    }

                    for (Node newReference : mySuccessorsPredecessorAndSuccessorList)
                    {
                        try
                        {
                            newReference.ping();
                            this.references.addReference(newReference);
                        }
                        catch (CommunicationException e)
                        {
                            LOG.debug("Invalid successor reference {}", newReference);
                        }
                    }
                }
            }
            
        }
        catch (Exception e)
        {
            LOG.warn("Unexpected exception in {}!", this.getClass().getSimpleName(), e);
        }
    }
}
