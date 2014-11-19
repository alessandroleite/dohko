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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.data.ID;

/**
 * Looks up the node for a certain ID and stores the reference of the responsible node in the local finger table.
 */
final class FixFingerTask implements Runnable
{
    /**
     * Object LOG.
     */
    private final Logger log;

    /**
     * Instance of random generator for randomly picking another finger to fix.
     */
    private final Random random = new Random();

    /**
     * Parent object for invoking findSuccessor.
     */
    private final NodeImpl parent;

    /**
     * Copy of the local node's ID for determining which ID to look up.
     */
    private final ID localID;

    /**
     * Reference on routing table.
     */
    private final References references;

    /**
     * Creates a new instance, but without starting a thread running it.
     * 
     * @param parent
     *            Parent object for invoking findSuccessor.
     * @param localID
     *            Copy of the local node's ID for determining which ID to look up.
     * @param references
     *            Reference on routing table.
     * @throws NullPointerException
     *             If either of the parameters has value <code>null</code>.
     */
    FixFingerTask(NodeImpl parent, ID localID, References references)
    {
        checkNotNull(parent);
        checkNotNull(localID);
        checkNotNull(references);

        this.log = LoggerFactory.getLogger(FixFingerTask.class.getSimpleName() + "." + localID);

        this.parent = parent;
        this.localID = localID;
        this.references = references;
    }

    @Override
    public void run()
    {
        try
        {
            int nextFingerToFix = this.random.nextInt(this.localID.getLength());

            // look up reference
            ID lookForID = this.localID.addPowerOfTwo(nextFingerToFix);

            Node newReference = this.parent.findSuccessor(lookForID);
            newReference.ping();

            // add new reference to finger table, if not yet included
            if (newReference != null && !this.references.containsReference(newReference))
            {
                this.log.debug("Adding new reference. URL: {} ID: {}", newReference.getNodeURL().getPath(), newReference.getNodeID().toString());

                this.references.addReference(newReference);
            }
        }
        catch (Exception e)
        {
            this.log.warn("Unexpected Exception caught in {}!", this.getClass().getSimpleName(), e);
        }
    }
}
