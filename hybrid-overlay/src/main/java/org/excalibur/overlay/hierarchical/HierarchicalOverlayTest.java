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
package org.excalibur.overlay.hierarchical;

import org.excalibur.core.util.SystemUtils2;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.discovery.ws.client.DiscoveryUtils;

import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;

public class HierarchicalOverlayTest
{

    public static void main(String[] args) throws OverlayException
    {
        final URL bootstrapURL = URL.valueOf(System.getProperty("boostrap.address"), SystemUtils2.getIntegerProperty("boostrap.port", 9090));
        Integer port = SystemUtils2.getIntegerProperty("node.port", 9090);
        
        String address = System.getProperty("node.address");
        String name = String.format("%s-%s", System.getProperty("org.excalibur.provider.name"), System.getProperty("org.excalibur.provider.region.name"));
        
        ProviderDetails provider = DiscoveryUtils.getProvider(new Peer().setHost(bootstrapURL.getHost()), name);
        
        Overlay overlay = new HierarchicalOverlay(provider, SystemUtils2.getProperty("org.excalibur.server.host.internal", address));
        overlay.join(URL.valueOf(address, port), bootstrapURL);
    }
}
