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

import java.io.Serializable;

import javax.xml.ws.Response;

import br.cic.unb.chord.communication.net.RemoteMethods;
import br.cic.unb.chord.communication.socket.SocketEndpoint;
import br.cic.unb.chord.communication.socket.SocketProxy;
import br.cic.unb.overlay.Overlay;

/**
 * <p>
 * This class represents a request for the invocation of a method on a {@link Overlay node}. <code>Request</code>s are sent by a {@link Proxy} to the
 * {@link Endpoint} of the node the {@link SocketProxy} represents.
 * </p>
 * <p>
 * Results of a method invocation are sent back to the {@link SocketProxy} by {@link SocketEndpoint} with help of a {@link Response} message. with
 * help of
 * </p>
 */
public final class Request extends Message
{

    private static final long serialVersionUID = -1295124240351172262L;

    /**
     * The type of this request. One of the method identifiers from {@link RemoteMethods}.
     */
    private RemoteMethods type;

    /**
     * The parameters for the request. Must match the parameters for the method identified by {@link #type} in types and order.
     */
    private Serializable[] parameters = null;

    /**
     * Identifier used to identify this request. This identifier must be the value of the {@link Response#getInReplyTo()} field of a {@link Response}
     * send for this request.
     */
    private String replyWith;

    /**
     * Creates a new instance of Request
     * 
     * @param type1
     *            The type of this request. One of the method identifiers from {@link RemoteMethods}.
     * @param replyWith1
     *            Identifier used to identify this request. This identifier must be the value of the {@link Response#getInReplyTo()} field of a
     *            {@link Response} send for this request.
     */
    public Request(RemoteMethods type, String replyWith)
    {
        super();
        this.type = type;
        this.replyWith = replyWith;
    }

    /**
     * Get the type of this request.
     * 
     * @return The type of this request. One of the method identifiers from {@link RemoteMethods}.
     */
    public RemoteMethods getRequestType()
    {
        return this.type;
    }

    /**
     * Set the parameters for this request.
     * 
     * @param parameters
     *            The parameters for the request. Must match the parameters for the method identified by {@link #type} in types and order.
     */
    public void setParameters(Serializable[] parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Get the parameters that shall be passed to the method that is requested by this.
     * 
     * @return The parameters for the request. Must match the parameters for the method identified by {@link #type} in types and order.
     */
    public Serializable[] getParameters()
    {
        return this.parameters;
    }

    /**
     * Get the value of the identifier for this request.
     * 
     * @return Identifier used to identify this request. This identifier must be the value of the {@link Response#getInReplyTo()} field of a
     *         {@link Response} send for this request.
     */
    public String getReplyWith()
    {
        return this.replyWith;
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
