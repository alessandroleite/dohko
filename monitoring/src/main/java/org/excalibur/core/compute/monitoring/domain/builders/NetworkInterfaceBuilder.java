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
package org.excalibur.core.compute.monitoring.domain.builders;

import java.math.BigDecimal;

import lshw.types.Capabilities;
import lshw.types.Capability;
import lshw.types.Measured;

import org.excalibur.core.compute.monitoring.domain.net.NetworkInterface;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterfaceConfiguration;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterfaceInfo;

public class NetworkInterfaceBuilder
{
    private NetworkInterfaceInfo networkInterfaceInfo;
    private String id;
    private String description;
    private String product;
    private String vendor;
    private String busInfo;
    private String logicalName;
    private String version;
    private String serial;
    private BigDecimal size;
    private BigDecimal capacity;
    private BigDecimal width;
    private BigDecimal clock;
    private Capabilities capabilities;
    private NetworkInterfaceConfiguration configuration;
    private String type;
    private boolean primary;

    public NetworkInterface build()
    {

        NetworkInterface networkInterface = null;

        // if (os != null)
        // {
        // networkInterface = os.networkInterfaceDescription(id);
        // }

        if (networkInterface == null)
        {
            networkInterface = new NetworkInterface(this.id, this.description, this.product, this.vendor, this.busInfo, this.logicalName,
                    this.version, this.serial, new Measured(this.size), new Measured(this.capacity), new Measured(this.width), new Measured(
                            this.clock), this.capabilities, this.configuration);
        }

        if (this.networkInterfaceInfo != null)
        {
            networkInterface.setNetworkInfo(networkInterfaceInfo);
        }

        networkInterface.setPrimary(this.primary);

        if (!networkInterface.capabilities().getCapabilitiesMap().containsKey(type))
        {
            networkInterface.capabilities().add(new Capability(type, null));
        }

        return networkInterface;
    }

    public NetworkInterfaceBuilder networkInfo(NetworkInterfaceInfo networkInterfaceInfo)
    {
        this.networkInterfaceInfo = networkInterfaceInfo;
        return this;
    }

    public NetworkInterfaceBuilder id(String id)
    {
        this.id = id;
        return this;
    }

    public NetworkInterfaceBuilder description(String description)
    {
        this.description = description;
        return this;
    }

    public NetworkInterfaceBuilder productName(String name)
    {
        this.product = name;
        return this;
    }

    public NetworkInterfaceBuilder vendor(String vendor)
    {
        this.vendor = vendor;
        return this;
    }

    public NetworkInterfaceBuilder busInfo(String busInfo)
    {
        this.busInfo = busInfo;
        return this;
    }

    public NetworkInterfaceBuilder logicalName(String name)
    {
        this.logicalName = name;
        return this;
    }

    public NetworkInterfaceBuilder version(String version)
    {
        this.version = version;
        return this;
    }

    public NetworkInterfaceBuilder hardwareId(String serial)
    {
        this.serial = serial;
        return this;
    }

    public NetworkInterfaceBuilder size(BigDecimal size)
    {
        this.size = size;
        return this;
    }

    public NetworkInterfaceBuilder capacity(BigDecimal capacity)
    {
        this.capacity = capacity;
        return this;
    }

    public NetworkInterfaceBuilder width(BigDecimal width)
    {
        this.width = width;
        return this;
    }

    public NetworkInterfaceBuilder clock(BigDecimal clock)
    {
        this.clock = clock;
        return this;
    }

    public NetworkInterfaceBuilder capabilities(Capabilities capabilities)
    {
        this.capabilities = capabilities;
        return this;
    }

    public NetworkInterfaceBuilder configuration(NetworkInterfaceConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    public NetworkInterfaceBuilder isPrimary(boolean value)
    {
        this.primary = value;
        return this;
    }

    public NetworkInterfaceBuilder ofType(String type)
    {
        this.type = type;
        return this;
    }
}
