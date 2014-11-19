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
package org.excalibur.core.compute.monitoring.domain.net;

import java.io.Serializable;
import java.util.Map;

import lshw.types.Configurations;

import org.excalibur.core.compute.monitoring.utils.Booleans;
import org.excalibur.core.compute.monitoring.utils.Doubles2;

public final class NetworkInterfaceConfiguration implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3697694628229922770L;

    /**
     * Flag that indicates if the interface supports auto-negotiation.
     */
    private final boolean autoNegotiation;

    /**
     * Flag that indicates if the interface has broadcast support.
     */
    private final boolean broadcast;

    /**
     * The driver's name.
     */
    private final String driver;

    /**
     * The version of the driver.
     */
    private final String driverVersion;

    /**
     * 
     */
    private final String duplex;

    /**
     * 
     */
    private final String firmware;

    /**
     * 
     */
    private final String ip;
    /**
     * 
     */
    private final double latency;

    /**
     * 
     */
    private final boolean link;
    /**
     * 
     */
    private final boolean multicast;

    /**
     * 
     */
    private final String port;

    /**
     * 
     */
    private final String speed;

    public NetworkInterfaceConfiguration(boolean autoNegotiation, boolean broadcast, String driver, String driverVersion, String duplex,
            String firmware, String ip, double latency, boolean link, boolean multicast, String port, String speed)
    {

        this.autoNegotiation = autoNegotiation;
        this.broadcast = broadcast;
        this.driver = driver;
        this.driverVersion = driverVersion;
        this.duplex = duplex;
        this.firmware = firmware;
        this.ip = ip;
        this.latency = latency;
        this.link = link;
        this.multicast = multicast;
        this.port = port;
        this.speed = speed;
    }

    /**
     * Creates an instance of the {@link NetworkInterfaceConfiguration} using the data of a given {@link Configurations}'s instance.
     * 
     * @param configurations
     *            The data of the {@link NetworkInterfaceConfiguration}.
     * @return An instance of the {@link NetworkInterfaceConfiguration} with the data of the given {@link Configurations}.
     */
    public static NetworkInterfaceConfiguration valueOf(Configurations configurations)
    {
        Map<String, String> configurationsMap = configurations.getConfigurationsMap();
        
        return new NetworkInterfaceConfiguration(Booleans.valueOf(configurationsMap.get("autonegotiation")), 
                Booleans.valueOf(configurationsMap.get("broadcast")), 
                configurationsMap.get("driver"), 
                configurationsMap.get("driverversion"), 
                configurationsMap.get("duplex"),
                configurationsMap.get("firmware"), 
                configurationsMap.get("ip"), 
                Doubles2.valueOf(configurationsMap.get("latency")),
                Booleans.valueOf(configurationsMap.get("link")), 
                Booleans.valueOf(configurationsMap.get("multicast")), 
                configurationsMap.get("port"),
                configurationsMap.get("speed"));
    }

    @Override
    public NetworkInterfaceConfiguration clone()
    {
        try
        {
            return (NetworkInterfaceConfiguration) super.clone();
        }
        catch (CloneNotSupportedException exception)
        {
            return new NetworkInterfaceConfiguration(this.autoNegotiation, broadcast, driver, driverVersion, duplex, firmware, ip, latency, link,
                    multicast, port, speed);
        }
    }

    /**
     * @return the autoNegotiation
     */
    public boolean autoNegotiation()
    {
        return autoNegotiation;
    }

    /**
     * @return the broadcast
     */
    public boolean broadcast()
    {
        return broadcast;
    }

    /**
     * @return the driver
     */
    public String driver()
    {
        return driver;
    }

    /**
     * @return the driverVersion
     */
    public String driverVersion()
    {
        return driverVersion;
    }

    /**
     * @return the duplex
     */
    public String duplex()
    {
        return duplex;
    }

    /**
     * @return the firmware
     */
    public String firmware()
    {
        return firmware;
    }

    /**
     * @return the ip
     */
    public String ip()
    {
        return ip;
    }

    /**
     * @return the latency
     */
    public double latency()
    {
        return latency;
    }

    /**
     * @return the link
     */
    public boolean link()
    {
        return link;
    }

    /**
     * @return the multicast
     */
    public boolean multicast()
    {
        return multicast;
    }

    /**
     * @return the port
     */
    public String port()
    {
        return port;
    }

    /**
     * @return the pair
     */
    public String speed()
    {
        return speed;
    }
}
