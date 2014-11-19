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

import br.cic.unb.chord.communication.jxta.JxtaProxy;
import br.cic.unb.chord.communication.socket.SocketProxy;
import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.util.ReflectionUtil;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ProxyFactory
{

    private static final String DEFAULT_PROXY_TYPE = System.getProperty("br.cic.unb.chord.communication.proxy.default.type.class");

    /**
     * Factory method to create a proxy to connect to the given {@link URL}. The protocol of {@link URL} is used to determine the value of the
     * {@link ProtocolType} to create. The proxy type must be a known protocol type.
     * 
     * @param sourceUrl
     *            {@link URL} of the local node, that wants to establish the connection.
     * @param destinationUrl
     *            {@link URL} of the remote endpoint.
     * @param proxyType
     *            Proxy type of connection.
     * @return Proxy to make invocations on a {@link Node} remote node.
     * @throws CommunicationException
     */
    public static Node create(final URL sourceUrl, final URL destinationUrl, final ProtocolType proxyType) throws CommunicationException
    {
        checkNotNull(sourceUrl);
        checkNotNull(destinationUrl);
        checkArgument(!sourceUrl.equals(destinationUrl));

        switch (proxyType)
        {
        case SOCKET:
            return SocketProxy.create(sourceUrl, destinationUrl);
        case JXTA:
            return JxtaProxy.create(sourceUrl, destinationUrl);
        default:
            return create(sourceUrl, destinationUrl);
        }
    }

    /**
     * Factory method to create a proxy to connect to the given {@link URL}. The system properties
     * "br.cic.unb.chord.communication.proxy.default.type.class" is use to create the correct instance of connection.
     * 
     * @param sourceUrl
     *            {@link URL} of the local node, that wants to establish the connection.
     * @param destinationUrl
     *            {@link URL} of the remote endpoint.
     * @return Proxy to make invocations on a {@link Node} remote node. Proxy type of connection.
     * @throws CommunicationException
     */
    public static Node create(final URL sourceUrl, final URL destinationUrl) throws CommunicationException
    {

        checkNotNull(sourceUrl);
        checkNotNull(destinationUrl);
        checkArgument(!sourceUrl.equals(destinationUrl));

        if (DEFAULT_PROXY_TYPE == null)
            throw new NullPointerException("Default proxy type implementation not defined. Set the "
                    + "br.cic.unb.chord.communication.proxy.default.type.class properties "
                    + "or use the method: ProxyFactory.create(sourceUrl,destinationUrl,proxyType)");

        return (Node) ReflectionUtil.executeStaticMethod(DEFAULT_PROXY_TYPE, "create", sourceUrl, destinationUrl);
    }
}
