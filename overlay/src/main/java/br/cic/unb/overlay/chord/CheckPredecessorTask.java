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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Node;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Checks if the predecessor of the local node is still alive.
 */
final class CheckPredecessorTask implements Runnable
{
    /**
     * Object LOG.
     */
    private static final Logger logger = LoggerFactory.getLogger(CheckPredecessorTask.class);

    /**
     * Reference on routing table.
     */
    private final References references;

    /**
     * Creates a new instance, but without starting a thread running it.
     * 
     * @param references
     *            Reference to the routing table.
     * @throws NullPointerException
     *             If the routing table is <code>null</code>.
     */
    CheckPredecessorTask(References references)
    {
        this.references = checkNotNull(references);
    }

    @Override
    public void run()
    {
        try
        {
            Node predecessor = this.references.getPredecessor();
            if (predecessor != null)
            {
                try
                {
                    predecessor.ping();
                }
                catch (CommunicationException e)
                {
                    logger.debug("Checking predecessor was NOT successful due to a communication failure! Removing predecessor reference.", e);
                    this.references.removeReference(predecessor);
                    return;
                }
            }
        }
        catch (Exception e)
        {
            logger.warn("Unexpected Exception caught in CheckpredecessorTask!", e);
        }
    }
}
