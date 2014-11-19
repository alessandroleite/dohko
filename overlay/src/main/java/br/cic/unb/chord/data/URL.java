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
package br.cic.unb.chord.data;

import java.io.Serializable;
import java.net.MalformedURLException;

import br.cic.unb.chord.communication.ProtocolType;

/**
 * Represents the address of a node.
 */
public final class URL implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7735850063827644101L;

    /**
     * String representation of URL
     */
    private transient String urlString;

    /**
     * The protocol of this URL.
     */
    private final String protocol;

    /**
     * The host of this URL.
     */
    private final String host;

    /**
     * The port of this URL.
     */
    private final int port;

    /**
     * The path for this URL.
     */
    private final String path;

    /**
     * Constant for URL parsing.
     */
    private final static String DCOLON = ":";

    /**
     * Constant for URL parsing.
     */
    private final static String SLASH = "/";

    /**
     * Constant for URL parsing.
     */
    private final static String DCOLON_SLASHES = DCOLON + SLASH + SLASH;

    /**
     * Create an instance of URL from <code>urlString</code>.
     * 
     * @param urlString
     *            The string to create an URL from.
     * @throws MalformedURLException
     *             This can occur if <code>urlString</code> does not match the pattern <code>protocol://host[:port]/path</code>, an unknown protocol
     *             is specified, or port is negative.
     * 
     */
    public URL(String urlString) throws MalformedURLException
    {
        this.urlString = urlString;

        int indexOfColonAndTwoSlashes = urlString.indexOf(DCOLON_SLASHES);
        if (indexOfColonAndTwoSlashes < 0)
        {
            throw new MalformedURLException("Invalid URL");
        }

        this.protocol = urlString.substring(0, indexOfColonAndTwoSlashes);
        urlString = urlString.substring(indexOfColonAndTwoSlashes + 3);

        int endOfHost = urlString.indexOf(DCOLON);
        if (endOfHost >= 0)
        {
            this.host = urlString.substring(0, endOfHost);
            urlString = urlString.substring(endOfHost + 1);
            int endOfPort = urlString.indexOf(SLASH);

            if (endOfPort < 0)
            {
                throw new MalformedURLException("Invalid URL!");
            }

            int tmp_port = Integer.parseInt(urlString.substring(0, endOfPort));

            if ((tmp_port <= 0) || (tmp_port >= 65536))
            {
                throw new MalformedURLException("Invalid URL! A port number must be between 0 and 65535!");
            }

            this.port = tmp_port;
            urlString = urlString.substring(endOfPort + 1);

        }
        else
        {
            endOfHost = urlString.indexOf(SLASH);

            if (endOfHost < 0)
            {
                throw new MalformedURLException("Invalid URL");
            }

            this.host = urlString.substring(0, endOfHost);
            urlString = urlString.substring(endOfHost + 1);

            ProtocolType type = ProtocolType.valueOf(this.protocol.toUpperCase());
            String defaultPort = System.getProperty("br.cic.unb.chord.communication.proxy.default." + this.getProtocol().toLowerCase().trim()
                    + ".port");

            if (defaultPort == null || defaultPort.trim().isEmpty())
            {
                this.port = type.getDefaultProxyPort();
            }
            else
            {
                this.port = Integer.parseInt(defaultPort);
            }
        }

        this.path = urlString;

        boolean protocolIsKnown = false;

        for (int i = 0; i < ProtocolType.values().length && !protocolIsKnown; i++)
        {
            if (this.protocol.equalsIgnoreCase(ProtocolType.values()[i].name()))
            {
                protocolIsKnown = true;
            }
        }
        if (!protocolIsKnown)
        {
            throw new MalformedURLException("Invalid protocol! " + this.protocol);
        }
    }

    /**
     * Factory method of {@link URL}
     * 
     * @param protocol
     *            The protocol of {@link URL} do be instantiated.
     * @param host
     *            The host name.
     * @param port
     *            The port number.
     * @return A instance of {@link URL}
     */
    public static URL valueOf(ProtocolType protocol, String host, Integer port)
    {
        try
        {
            return new URL(protocol.name().toLowerCase() + "://" + host + ":" + port + "/");
        }
        catch (MalformedURLException exception)
        {
            throw new IllegalArgumentException(exception);
        }
    }

    public static URL valueOf(String host, Integer port)
    {
        return URL.valueOf(ProtocolType.SOCKET, host, port);
    }

    /**
     * Get the protocol of this URL.
     * 
     * @return The protocol of this URL.
     */
    public final String getProtocol()
    {
        return this.protocol;
    }

    /**
     * Get the host name contained in this URL.
     * 
     * @return Host name contained in this URL.
     */
    public final String getHost()
    {
        return this.host;
    }

    /**
     * Get the path contained in this URL.
     * 
     * @return The path contained in this URL.
     */
    public final String getPath()
    {
        return this.path;
    }

    /**
     * Get the port contained in this URL.
     * 
     * @return The port of this URL. Has value <code>NO_PORT</code> if no port has been specified for this URL.
     */
    public final int getPort()
    {
        return this.port;
    }

    /**
     * Overwritten from {@link java.lang.Object}.
     * 
     * @return Hash code of this URL.
     */
    public final int hashCode()
    {
        int hash = 17;
        hash += 37 * this.protocol.hashCode();
        hash += 37 * this.host.hashCode();
        hash += 37 * this.path.hashCode();
        hash += 37 * this.port;
        return hash;
    }

    /**
     * Overwritten from {@link java.lang.Object}.
     * 
     * @param obj
     * @return <code>true</code> if provided <code>obj</code> is an instance of <code>URL</code> and has the same attributes as this <code>URL</code>.
     */
    public final boolean equals(Object obj)
    {
        if (obj instanceof URL)
        {
            URL url = (URL) obj;

            if (!url.getProtocol().equalsIgnoreCase(this.protocol))
            {
                return false;
            }
            if (!url.getHost().equalsIgnoreCase(this.host))
            {
                return false;
            }
            if (!(url.getPort() == this.port))
            {
                return false;
            }
            if (!url.getPath().equals(this.path))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Overwritten from {@link java.lang.Object}.
     * 
     * @return String representation of this URL.
     */
    public final String toString()
    {
        if (this.urlString == null)
        {
            StringBuilder builder = new StringBuilder().append(this.protocol).append(DCOLON_SLASHES).append(this.host).append(DCOLON)
                    .append(this.port).append(SLASH).append(this.path);

            this.urlString = builder.toString().toLowerCase();
        }
        return this.urlString;
    }
}
