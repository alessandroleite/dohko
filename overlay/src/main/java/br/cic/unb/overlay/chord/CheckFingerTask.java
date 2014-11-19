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

import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Node;

class CheckFingerTask implements Runnable
{
    /**
     * Object LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CheckFingerTask.class);

    /**
     * Reference on routing table.
     */
    private final References references;

    public CheckFingerTask(References references)
    {
        this.references = checkNotNull(references);
    }

    @Override
    public void run()
    {
        try
        {
            for (Node node : references.getFingerTable())
            {
                if (node != null)
                {
                    try
                    {
                        node.ping();
                    }
                    catch (CommunicationException exception)
                    {
                        LOG.debug("Invalid finger table reference {}", node);
                        references.removeReference(node);
                    }
                }
            }
        }
        catch (Exception exception)
        {
            LOG.debug("Unexpected exception!", exception);
        }
    }
}
