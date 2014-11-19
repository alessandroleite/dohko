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

public enum ProtocolType
{
    SOCKET, LOCAL, RMI, JXTA;

    /**
     * Array containing default ports for all known protocols. The port for each protocol can be referenced with help of the constants for the
     * protocol e.g. <code>SOCKET_PROTOCOL</code>.
     */
    private final static int[] DEFAULT_PORTS = new int[] { 4242, -1, 4242 };

    /**
     * Return the default port of this {@link ProtocolType}
     */
    public int getDefaultProxyPort()
    {
        return DEFAULT_PORTS[this.ordinal()];
    }

    /**
     * @param protocol
     * @return
     */
    public static ProtocolType valueOfFromProtocol(final String protocol)
    {
        if (protocol == null)
        {
            throw new NullPointerException("Protocol may not be null!");
        }
        for (ProtocolType type : ProtocolType.values())
        {
            if (type.name().equalsIgnoreCase(protocol))
            {
                return type;
            }
        }
        throw new RuntimeException("Unknown protocol " + protocol);
    }
}
