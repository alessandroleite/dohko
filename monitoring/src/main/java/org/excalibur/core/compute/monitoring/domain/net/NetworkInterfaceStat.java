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

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.io.Serializable;

public final class NetworkInterfaceStat implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7628692769918787851L;

    private final NetworkInterface networkInterface;

    private final TxNetworkInterfaceData txData;

    private final RxNetworkInterfaceData rxData;

    public NetworkInterfaceStat(NetworkInterface networkInterface, TxNetworkInterfaceData txData, RxNetworkInterfaceData rxData)
    {

        this.networkInterface = requireNonNull(networkInterface);
        this.txData = requireNonNull(txData);
        this.rxData = requireNonNull(rxData);

        checkArgument(networkInterface == txData.networkInterface() && networkInterface == rxData.networkInterface());
    }

    public NetworkInterfaceStat value()
    {
        return this;
    }

    /**
     * @return the networkInterface
     */
    public NetworkInterface networkInterface()
    {
        return networkInterface;
    }

    /**
     * @return the txData
     */
    public TxNetworkInterfaceData txData()
    {
        return txData;
    }

    /**
     * @return the rxData
     */
    public RxNetworkInterfaceData rxData()
    {
        return rxData;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((networkInterface == null) ? 0 : networkInterface.hashCode());
        result = prime * result + ((rxData == null) ? 0 : rxData.hashCode());
        result = prime * result + ((txData == null) ? 0 : txData.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        NetworkInterfaceStat other = (NetworkInterfaceStat) obj;
        if (networkInterface == null)
        {
            if (other.networkInterface != null)
            {
                return false;
            }
        }
        else if (!networkInterface.equals(other.networkInterface))
        {
            return false;
        }

        if (rxData == null)
        {
            if (other.rxData != null)
            {
                return false;
            }
        }
        else if (!rxData.equals(other.rxData))
        {
            return false;
        }

        if (txData == null)
        {
            if (other.txData != null)
            {
                return false;
            }
        }
        else if (!txData.equals(other.txData))
        {
            return false;
        }

        return true;
    }
}
