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

public final class NetworkInterfaceInfo implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6038078425806488864L;

    /**
     * The default gateway
     */
    private final String defaultGateway;

    /**
     * The address (IP) of the primary DNS.
     */
    private final String primaryDns;

    /**
     * The address (IP) of the second DNS.
     */
    private final String secondDns;

    /**
     * The domain name.
     */
    private final String domainName;

    /**
     * The host name.
     */
    private final String hostName;

    private final String ip6;

    private final String netmask;

    private final String broadcastAddress;

    private final String ip;

    private final String destination;

    public NetworkInterfaceInfo(String defaultGateway, String primaryDns, String secondDns, String domainName, String hostName, String ip6,
            String netmask, String broadcastAddress, String ip, String destination)
    {

        this.defaultGateway = defaultGateway;
        this.primaryDns = primaryDns;
        this.secondDns = secondDns;
        this.domainName = domainName;
        this.hostName = hostName;
        this.ip6 = ip6;
        this.netmask = netmask;
        this.broadcastAddress = broadcastAddress;
        this.ip = ip;
        this.destination = destination;
    }

    /**
     * @return the defaultGateway
     */
    public String defaultGateway()
    {
        return defaultGateway;
    }

    /**
     * @return the primaryDns
     */
    public String primaryDns()
    {
        return primaryDns;
    }

    /**
     * @return the secondDns
     */
    public String secondDns()
    {
        return secondDns;
    }

    /**
     * @return the domainName
     */
    public String domainName()
    {
        return domainName;
    }

    /**
     * @return the hostName
     */
    public String hostName()
    {
        return hostName;
    }

    /**
     * @return the ip6
     */
    public String ip6()
    {
        return ip6;
    }

    /**
     * @return the netmask
     */
    public String netmask()
    {
        return netmask;
    }

    /**
     * @return the broadcastAddress
     */
    public String broadcastAddress()
    {
        return broadcastAddress;
    }

    /**
     * @return the ip
     */
    public String ip()
    {
        return ip;
    }

    /**
     * @return the destination
     */
    public String destination()
    {
        return destination;
    }
}
