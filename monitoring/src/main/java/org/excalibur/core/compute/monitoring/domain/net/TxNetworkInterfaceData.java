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

public class TxNetworkInterfaceData extends NetworkInterfaceData
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 1187856578487675226L;

    private final long collisions_;

    private final long carrier_;

    public TxNetworkInterfaceData(NetworkInterface networkInterface, long bytes, long dropped, long overruns, long errors, long packets,
            long collisions, long carrier)
    {
        super(networkInterface, bytes, dropped, overruns, errors, packets);
        this.collisions_ = collisions;
        this.carrier_ = carrier;
    }

    @Override
    public NetworkInterfaceDataType type()
    {
        return NetworkInterfaceDataType.TX;
    }

    /**
     * @return the collisions
     */
    public long collisions()
    {
        return collisions_;
    }

    /**
     * @return the carrier
     */
    public long carrier()
    {
        return carrier_;
    }
}
