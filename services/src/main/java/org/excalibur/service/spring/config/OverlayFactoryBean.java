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
package org.excalibur.service.spring.config;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.excalibur.discovery.ws.client.DiscoveryUtils.buildClustersNameFor;

import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.discovery.domain.ResourceDetails;
import org.excalibur.jackson.databind.JsonJaxbObjectMapper;
import org.excalibur.overlay.hierarchical.HierarchicalOverlay;
import org.springframework.beans.factory.FactoryBean;

import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.chord.util.Constants;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayBuilder;
import br.cic.unb.overlay.chord.StringKey;

public class OverlayFactoryBean implements FactoryBean<Overlay>
{
    public static final String INTERNAL_ADDRESS_PROPERTY_NAME = "org.excalibur.server.host.internal";
    
    private String host;
    private Integer port;

    private String bootstrapAddress;
    private Integer bootstrapPort;

    private ProviderDetails providerInfo_;

    private boolean bootstrap;

    @Override
    public Overlay getObject() throws Exception
    {
        Overlay overlay;

        System.setProperty(Constants.PROPERTY_WHERE_TO_FIND_LOCAL_NODE_ADDRESS, host);
        System.setProperty(Constants.PROPERTY_WHERE_TO_FIND_LOCAL_NODE_PORT, port.toString());
        
        String internalAddress = SystemUtils2.getProperty(INTERNAL_ADDRESS_PROPERTY_NAME, host);

        if (bootstrapAddress != null && bootstrapPort != null)
        {
            System.setProperty(Constants.PROPERTY_WHERE_TO_BOOTSTRAP_ADDRESS, bootstrapAddress);
            System.setProperty(Constants.PROPERTY_WHERE_TO_FIND_BOOTSTRAP_PORT, bootstrapPort.toString());
        }

        if (bootstrap)
        {
            overlay = new OverlayBuilder().localAddress(host, port).bootstrap(bootstrapAddress, bootstrapPort).buildAndJoin();
            
            NodeDetails thisNode = new NodeDetails().setProvider(providerInfo_);
            thisNode.getAddresses().setInternal(Peer.valueOf(overlay.getID(), URL.valueOf(internalAddress, port)));

            JsonJaxbObjectMapper mapper = new JsonJaxbObjectMapper();

            String cluster = buildClustersNameFor(providerInfo_);

            ResourceDetails resource = new ResourceDetails();
            resource.setName(cluster)
                    .setType(thisNode.getClass())
                    .setPayload(new JAXBContextFactory<NodeDetails>(thisNode.getClass()).marshal(thisNode));
            
            overlay.insert(new StringKey(cluster), mapper.writeValueAsString(resource));
            
            resource.setName("/providers")
                    .setPayload(new JAXBContextFactory<NodeDetails>(providerInfo_.getClass()).marshal(providerInfo_))
                    .setType(providerInfo_.getClass());
            
            overlay.insert(new StringKey("/providers"), mapper.writeValueAsString(resource));
        }
        else
        {
            checkState(!isNullOrEmpty(bootstrapAddress));
            checkState(bootstrapPort != null);

            URL localURL = URL.valueOf(host, port);
            URL bootstrapURL = URL.valueOf(bootstrapAddress, bootstrapPort);

            checkState(!localURL.equals(bootstrapURL));

            overlay = new HierarchicalOverlay(providerInfo_, internalAddress);
//            overlay.join(localURL, bootstrapURL);
        }

        return overlay;
    }

    @Override
    public Class<?> getObjectType()
    {
        return Overlay.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(Integer port)
    {
        this.port = port;
    }

    /**
     * @param bootstrapAddress
     *            the bootstrapAddress to set
     */
    public void setBootstrapAddress(String bootstrapAddress)
    {
        this.bootstrapAddress = bootstrapAddress;
    }

    /**
     * @param bootstrapPort
     *            the bootstrapPort to set
     */
    public void setBootstrapPort(Integer bootstrapPort)
    {
        this.bootstrapPort = bootstrapPort;
    }

    public void setProviderInfo(ProviderDetails providerInfo)
    {
        this.providerInfo_ = providerInfo;
    }

    public void setBootstrap(boolean bootstrap)
    {
        this.bootstrap = bootstrap;
    }
}
