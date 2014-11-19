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
package br.cic.unb.chord.communication.net.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class GetPublicHostname
{
    public static void main(String[] args) throws Throwable
    {
        NetworkInterface iface = null;

        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();)
        {

            iface = ifaces.nextElement();
            System.out.println("Interface:" + iface.getDisplayName());

            InetAddress ia = null;

            for (Enumeration<InetAddress> ips = iface.getInetAddresses(); ips.hasMoreElements();)
            {
                ia = ips.nextElement();
                System.out.println(ia.getCanonicalHostName() + " " + ia.getHostAddress());
            }
        }
    }
}
