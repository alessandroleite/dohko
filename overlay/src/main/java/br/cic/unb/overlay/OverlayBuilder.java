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
package br.cic.unb.overlay;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.util.Constants;
import br.cic.unb.chord.util.ReflectionUtil;

import static br.cic.unb.chord.util.Constants.DEFAULT_PORT_NUMBER;
import static br.cic.unb.chord.util.Constants.PROPERTY_WHERE_TO_BOOTSTRAP_ADDRESS;
import static br.cic.unb.chord.util.Constants.PROPERTY_WHERE_TO_FIND_BOOTSTRAP_PORT;
import static br.cic.unb.chord.util.Constants.PROPERTY_WHERE_TO_FIND_LOCAL_NODE_ADDRESS;
import static br.cic.unb.chord.util.Constants.PROPERTY_WHERE_TO_FIND_LOCAL_NODE_PORT;
import static br.cic.unb.chord.util.Constants.PROPERTY_WHERE_TO_FIND_PROPERTY_FILE;
import static br.cic.unb.chord.util.Constants.PROPERTY_WITH_DEFAULT_PORT_NUMBER;
import static br.cic.unb.chord.util.Constants.STANDARD_PROPERTY_FILE;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class OverlayBuilder
{
    private String localAddress;
    private Integer localPortNumber;
    private URL localURL;

    private String bootstrapAddress;
    private Integer bootstrapPortNumber;
    private URL bootstrapURL;

    private String configFile;

    public static OverlayBuilder newBuilder()
    {
        return new OverlayBuilder();
    }

    public OverlayBuilder localAddress(String localNodeAddress)
    {
        this.localAddress = localNodeAddress;
        return this;
    }

    /**
     * Assigns the IP as the address to be used by the overlay.
     * 
     * @return The same instance of the builder.
     * @throws UnknownHostException
     *             if the local host name could not be resolved into an address.
     */
    public OverlayBuilder localHostAddress() throws UnknownHostException
    {
        localAddress(InetAddress.getLocalHost().getHostAddress());
        return this;
    }

    public OverlayBuilder localAddress(String localNodeAddress, Integer portNumber)
    {
        this.localAddress = localNodeAddress;
        this.localPortNumber = portNumber;

        return this;
    }

    public OverlayBuilder localPortNumber(Integer portNumber)
    {
        this.localPortNumber = portNumber;
        return this;
    }

    public OverlayBuilder bootstrap(String bootstrapAddress, Integer bootstrapPort)
    {
        this.bootstrapAddress = bootstrapAddress;
        this.bootstrapPortNumber = bootstrapPort;
        return this;
    }

    public final Overlay build()
    {
        configure();
        return (Overlay) ReflectionUtil.newInstance(System.getProperty(Constants.OVERLAY_SYSTEM_PROPERTY_CLASS_NAME));
    }

    public final Overlay buildAndJoin() throws OverlayException
    {
        Overlay chord = build();

        if (this.bootstrapURL != null)
        {
            chord.join(this.localURL, this.bootstrapURL);
        }
        else
        {
            chord.create(this.localURL);
        }

        return chord;
    }

    public final Overlay build(Peer bootstrap)
    {
        checkNotNull(bootstrap);
        return bootstrap(bootstrap.getHost(), bootstrap.getPort()).build();
    }

    private void configure()
    {
        if (isNullOrEmpty(this.configFile))
        {
            this.configFile = System.getProperty(PROPERTY_WHERE_TO_FIND_PROPERTY_FILE, STANDARD_PROPERTY_FILE);
        }

        try
        {
            Properties props = System.getProperties();
            props.load(ClassLoader.getSystemResourceAsStream(this.configFile));

            System.setProperties(props);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Property file was not found: " + this.configFile + "! It must be located in the CLASSPATH and "
                    + "either be named 'chord.properties' or its name be specified by -Dapp.config.properties.file='filename'", e);
        }
        catch (NullPointerException e)
        {
            throw new NullPointerException("Property file was not found: " + this.configFile + "! It must be located in the CLASSPATH and "
                    + "either be named 'chord.properties' or its name be specified by -Dapp.config.properties.file='filename'");
        }

        if (isNullOrEmpty(this.localAddress))
        {
            this.localAddress = System.getProperty(PROPERTY_WHERE_TO_FIND_LOCAL_NODE_ADDRESS);
            checkNotNull(this.localAddress, "Node's address is null!");
        }

        if (this.localPortNumber == null)
        {
            String port = System.getProperty(PROPERTY_WHERE_TO_FIND_LOCAL_NODE_PORT,
                    System.getProperty(PROPERTY_WITH_DEFAULT_PORT_NUMBER, DEFAULT_PORT_NUMBER.toString()));
            this.localPortNumber = Integer.parseInt(port);
        }

        if (isNullOrEmpty(bootstrapAddress))
        {
            this.bootstrapAddress = System.getProperty(PROPERTY_WHERE_TO_BOOTSTRAP_ADDRESS);
        }

        if (!isNullOrEmpty(this.bootstrapAddress) && this.bootstrapPortNumber == null)
        {
            this.bootstrapPortNumber = Integer.parseInt(System.getProperty(PROPERTY_WHERE_TO_FIND_BOOTSTRAP_PORT, DEFAULT_PORT_NUMBER.toString()));
        }

        if (bootstrapAddress != null)
        {
            this.bootstrapURL = URL.valueOf(this.bootstrapAddress, this.bootstrapPortNumber);
        }

        this.localURL = URL.valueOf(this.localAddress, this.localPortNumber);
    }
}
