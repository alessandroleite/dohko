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
package br.cic.unb.chord.communication.net;

import java.io.Serializable;

import br.cic.unb.chord.communication.Endpoint;
import br.cic.unb.chord.communication.Proxy;
import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;

/**
 * This class represents information about a remote node that can be used to construct a {@link Proxy}. This class is sent over the network by
 * {@link Endpoint} instead of sending complete {@link Proxy}. The receiver has to construct the proxy instance from the information contained within
 * this class.
 */
public final class RemoteNodeInfo implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 495384055117147989L;

    /**
     * The {@link URL} of the node that this represents.
     */
    protected URL nodeURL;

    /**
     * The {@link ID} of the node that this represents.
     */
    protected ID nodeID;

    /**
     * Constructs an object containing information about a node.
     * 
     * @param nodeURL
     * @param nodeID
     */
    public RemoteNodeInfo(URL nodeURL, ID nodeID)
    {
        this.nodeURL = nodeURL;
        this.nodeID = nodeID;
    }

    /**
     * @return Returns the nodeID.
     */
    public ID getNodeID()
    {
        return this.nodeID;
    }

    /**
     * @param id
     *            The nodeID to set.
     */
    public void setNodeID(ID id)
    {
        this.nodeID = id;
    }

    /**
     * @return Returns the nodeURL.
     */
    public URL getNodeURL()
    {
        return this.nodeURL;
    }

    /**
     * @param url
     *            The nodeURL to set.
     */
    public void setNodeURL(URL url)
    {
        this.nodeURL = url;
    }

    @Override
    public String toString()
    {
        return String.format("%s[NodeURL -> %s, nodeID -> %s", this.getClass().getSimpleName(), this.getNodeURL(), this.getNodeID());
    }
}
