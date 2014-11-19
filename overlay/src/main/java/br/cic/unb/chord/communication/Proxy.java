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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;

import static com.google.common.base.Preconditions.*;

/**
 * This class is used to represent other {@link br.cic.unb.chord.service.Overlay nodes} at a {@link br.cic.unb.chord.service.Overlay node}, so that
 * these nodes are able to connect to the node. A Proxy should establish a connection to the {@link Endpoint} of the node that is represented by this
 * proxy. So all protocol specific implementation for connections between nodes must be realized in an pair of {@link Endpoint} and {@link Proxy}.
 * 
 * This class has to be extended by all Proxies that are used to provide a connection to a remote node via the {@link Node} interface.
 */
public abstract class Proxy extends Node
{
    /**
     * The LOG for instances of this class.
     */
    private final static Logger logger = LoggerFactory.getLogger(Proxy.class.getName());

    /**
     * @param url
     */
    protected Proxy(ID nodeID, URL nodeURL)
    {
        super(nodeID, nodeURL);
        logger.debug("Proxy with url {} initiliased.", nodeURL);
    }
    
    protected Proxy(URL nodeURL)
    {
        super(nodeURL);
        logger.debug("Proxy with url {} initialised" + nodeURL);
    }

    /**
     * Factory method to create a proxy to connect to the given {@link URL}. The protocol of url is used to determine the type of the proxy to create.
     * The protocol of url must be a known protocol, as specified in {@link ProtocolType}
     * 
     * @param sourceUrl
     *            {@link URL} of the local node, that wants to establish the connection.
     * @param destinationUrl
     *            {@link URL} of the remote endpoint.
     * @return Proxy to make invocations on a {@link Node} remote node.
     * @throws CommunicationException
     * @see ProtocolType
     * @see Proxy
     */
    public static Node createConnection(URL sourceUrl, URL destinationUrl) throws CommunicationException
    {
        checkNotNull(sourceUrl,"sourceUrl");
        checkNotNull(destinationUrl,"destinationUrl");

        if (sourceUrl.equals(destinationUrl))
        {
            logger.error("URLs are equal: this url = {}, the other url = {}", sourceUrl.toString(), destinationUrl.toString());
            throw new CommunicationException("The source node and the target must not be the same!");
        }
        
        return ProxyFactory.create(sourceUrl, destinationUrl, ProtocolType.valueOfFromProtocol(sourceUrl.getProtocol()));
    }
}
