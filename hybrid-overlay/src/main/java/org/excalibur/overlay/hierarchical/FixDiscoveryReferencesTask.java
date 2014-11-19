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

import java.util.Collections;
import java.util.List;

import org.excalibur.core.util.SystemUtils2;
import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.discovery.domain.ResourceDetails;
import org.excalibur.discovery.service.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import br.cic.unb.chord.communication.CommunicationException;
import br.cic.unb.chord.communication.Node;
import br.cic.unb.chord.communication.Proxy;
import br.cic.unb.chord.data.URL;

import static org.excalibur.discovery.ws.client.DiscoveryUtils.buildClusterNameFor;
import static org.excalibur.discovery.ws.client.DiscoveryUtils.buildClustersNameFor;
import static org.excalibur.discovery.ws.client.DiscoveryUtils.buildResourceDetailsFor;


public class FixDiscoveryReferencesTask implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(FixDiscoveryReferencesTask.class.getName());

    @Autowired
    private DiscoveryService discoveryService;

    @Override
    public void run()
    {
        heartbeat(null);
    }
    
    public void heartbeat(String name)
    {
        try
        {
            URL thisNode = URL.valueOf(System.getProperty("org.excalibur.server.host"), SystemUtils2.getIntegerProperty("node.port", 9090));

            for (ProviderDetails provider : providers())
            {
                for (NodeDetails manager : clusters(provider))
                {
                    manager.setProvider(provider);

                    pingAndRemoveIfDead(thisNode, buildClustersNameFor(provider), manager);

                    for (NodeDetails member : members(provider, manager))
                    {
                        member.setParent(manager).setProvider(provider);
                        pingAndRemoveIfDead(thisNode, buildClusterNameFor(provider, manager), member);
                    }
                }
            }
        }
        catch (Exception exception)
        {
            LOG.error("Unexpected exception on {}!", this.getClass().getSimpleName(), exception);
        }
    }

    private List<ProviderDetails> providers()
    {
        return resources(DiscoveryService.PROVIDERS_PATH);
    }

    private List<NodeDetails> clusters(ProviderDetails provider)
    {
        return resources(buildClustersNameFor(provider));
    }

    private List<NodeDetails> members(ProviderDetails provider, NodeDetails manager)
    {
        return resources(buildClusterNameFor(provider, manager));
    }

    private <T> List<T> resources(String name)
    {
        List<T> resources;
        try
        {
            resources = discoveryService.queryForResource(name);
        }
        catch (Exception e)
        {
            resources = Collections.emptyList();
        }

        return resources;
    }

    private void pingAndRemoveIfDead(URL sourceURL, String name, NodeDetails node)
    {
        URL destinationUrl = node.getAddresses().getExternal().toURL();

        if (!sourceURL.equals(destinationUrl))
        {
            Node remote = null;
            try
            {
                remote = Proxy.createConnection(sourceURL, destinationUrl);
                remote.ping();
            }
            catch (CommunicationException e)
            {
                LOG.info("Node {} did not respond to ping!", remote);

                try
                {
                    ResourceDetails resource = buildResourceDetailsFor(name, node);

                    discoveryService.unregisterResource(resource);
                }
                catch (Exception exception)
                {
                    LOG.error("Error on removing the dead resource {}!", node, exception);
                }
            }
            finally
            {
                if (remote != null)
                {
                    try
                    {
                        remote.finalize();
                    }
                    catch (Throwable e)
                    {
                        LOG.error("Error on finalizing the remote node {}!", remote, e);
                    }
                }
            }
        }
    }
}
