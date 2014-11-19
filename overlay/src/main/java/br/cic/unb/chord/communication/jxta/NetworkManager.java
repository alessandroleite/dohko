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
package br.cic.unb.chord.communication.jxta;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.chord.HashFunction;

final class NetworkManager
{

    private static final Map<ID, net.jxta.platform.NetworkManager> NETWORK_MANAGER = new HashMap<ID, net.jxta.platform.NetworkManager>();

    private static final NetworkManager INSTANCE = new NetworkManager();

    private NetworkManager()
    {
    }

    public static final NetworkManager getInstance()
    {
        return INSTANCE;
    }

    public net.jxta.platform.NetworkManager getNetworkManager(URL url)
    {
        ID id = HashFunction.getHashFunction().createID(url.toString().getBytes());
        if (NETWORK_MANAGER.containsKey(id))
            return NETWORK_MANAGER.get(id);
        else
        {
            net.jxta.platform.NetworkManager manager;
            try
            {
                manager = new net.jxta.platform.NetworkManager(net.jxta.platform.NetworkManager.ConfigMode.ADHOC, id.toHexString(), new File(
                        new File(".cache"), id.toHexString()).toURI());
                NETWORK_MANAGER.put(id, manager);
                manager.startNetwork();
            }
            catch (Exception exception)
            {
                throw new RuntimeException(exception);
            }
            return manager;
        }
    }
}
