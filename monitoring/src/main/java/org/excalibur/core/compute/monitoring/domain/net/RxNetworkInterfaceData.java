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


public class RxNetworkInterfaceData extends NetworkInterfaceData
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2812754096538622969L;
   

    private final long frame;

    public RxNetworkInterfaceData(NetworkInterface networkInterface, long bytes, long dropped, long overruns, long errors, long packets, long frame)
    {
        super(networkInterface, bytes, dropped, overruns, errors, packets);
        this.frame = frame;
    }

    @Override
    public NetworkInterfaceDataType type()
    {
        return NetworkInterfaceDataType.RX;
    }

    /**
     * @return the frame
     */
    public long frame()
    {
        return frame;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (frame ^ (frame >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!super.equals(obj))
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        RxNetworkInterfaceData other = (RxNetworkInterfaceData) obj;

        if (frame != other.frame)
        {
            return false;
        }
        return true;
    }
}
