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
package org.excalibur.discovery.ws.client;

import static javax.ws.rs.core.MediaType.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;

import java.io.Serializable;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.discovery.domain.ResourceDetails;
import org.excalibur.discovery.service.DiscoveryService;
import org.excalibur.discovery.ws.ext.ObjectMapperProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cic.unb.chord.data.Peer;
import br.cic.unb.overlay.OverlayException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public final class DiscoveryUtils
{
    private final static Logger LOG = LoggerFactory.getLogger(DiscoveryUtils.class.getName());
    
    private final static JAXBContextFactory<NodeDetails> FACTORY;
    private final static ObjectMapper RESOURCE_MAPPER;

    static
    {
        try
        {
            FACTORY = new JAXBContextFactory<NodeDetails>(NodeDetails.class);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }

        RESOURCE_MAPPER = new ObjectMapper();
        RESOURCE_MAPPER.registerModule(new JaxbAnnotationModule());
    }

    private DiscoveryUtils()
    {
        throw new UnsupportedOperationException();
    }

    public static String buildClustersNameFor(ProviderDetails provider)
    {
        return String.format(DiscoveryService.CLUSTERS_PATH, provider.getName());
    }

    public static String buildClusterNameFor(ProviderDetails provider, NodeDetails leader)
    {
        return String.format(DiscoveryService.CLUSTER_MEMBERS_NAME_PATH, provider.getName(), leader.getId());
    }

    public static Serializable serialize(String name, NodeDetails node) throws OverlayException
    {
        checkNotNull(node);
        checkState(!isNullOrEmpty(name));

        try
        {
            ResourceDetails resource = new ResourceDetails();
            resource.setType(node.getClass()).setPayload(FACTORY.marshal(node)).setName(name);

            return RESOURCE_MAPPER.writeValueAsString(resource);

        }
        catch (JAXBException | JsonProcessingException e)
        {
            throw new OverlayException(e.getMessage(), e);
        }
    }

    public static ProviderDetails getProvider(Peer address, String name) throws OverlayException
    {
        ProviderDetails provider = buildWebTarget(address).path("/providers").path(name).request().accept(APPLICATION_XML_TYPE)
                .get(ProviderDetails.class);

        return provider;
    }

    public static NavigableSet<NodeDetails> getMembers(List<ResourceDetails> resources) throws OverlayException
    {
        NavigableSet<NodeDetails> members = newTreeSet();

        for (ResourceDetails resource : resources)
        {
            try
            {
                members.add(FACTORY.unmarshal(resource.getPayload()));
            }
            catch (JAXBException e)
            {
                throw new OverlayException(e.getMessage(), e);
            }
        }

        return members;
    }

    public static <E> NavigableSet<E> convertValue(List<?> members, Class<E> type)
    {
        NavigableSet<E> nodes = new TreeSet<E>();

        if (members != null)
        {
            for (Object member : members)
            {
                nodes.add(RESOURCE_MAPPER.convertValue(member, type));
            }
        }

        return nodes;
    }

    public static NavigableSet<NodeDetails> getSuperPeers(Peer peer, String provider) throws OverlayException
    {
       return getSuperPeers(peer.getHost(), provider);
    }
    
    public static NavigableSet<NodeDetails> getSuperPeers(String host, String provider)
    {
        checkNotNull(provider);
        WebTarget target = buildWebTarget(host);

        List<?> superpeers = target.path("clusters").path(provider).request(APPLICATION_JSON_TYPE).get(List.class);

        return DiscoveryUtils.convertValue(superpeers, NodeDetails.class);
    }

    public static NavigableSet<NodeDetails> getMembers(NodeDetails cluster, String clusterName)
    {
        WebTarget target = buildWebTarget(cluster.getAddresses().getExternal());
        Response response = target.path("cluster/members").request(APPLICATION_JSON_TYPE).post(Entity.text(clusterName));
        
        List<?> members = response.readEntity(List.class);
        
        return DiscoveryUtils.convertValue(members, NodeDetails.class);
    }
    
    public static NavigableSet<NodeDetails> listClusterMembers(String host, String provider, String clusterName)
    {
        WebTarget target = buildWebTarget(host);
        Response response = target.path("cluster/members/").path(provider).path(clusterName).request(APPLICATION_JSON_TYPE).get();
        List<?> members = response.readEntity(List.class);

        return DiscoveryUtils.convertValue(members, NodeDetails.class);
    }

    public static void registerResource(String name, Peer leader, NodeDetails resource) throws OverlayException
    {
        WebTarget target = buildWebTarget(leader);

        try
        {
            ResourceDetails resourceInstance = new ResourceDetails();
            resourceInstance.setName(name).setType(resource.getClass()).setPayload(FACTORY.marshal(resource));

            target.path("cluster/members").request(APPLICATION_JSON_TYPE)
                    .put(Entity.entity(resourceInstance, APPLICATION_JSON_TYPE));
        }
        catch (JAXBException e)
        {
            throw new OverlayException(e.getMessage(), e);
        }
    }
    
    public static void registerProvider(Peer peer, ProviderDetails providerInfo) throws OverlayException
    {
        WebTarget target = buildWebTarget(peer);
        ResourceDetails resource = new ResourceDetails();

        try
        {
            resource.setName("/providers").setPayload(FACTORY.marshal(providerInfo)).setType(providerInfo.getClass());
            target.path("providers/add").request(APPLICATION_JSON_TYPE).put(Entity.entity(resource, APPLICATION_JSON_TYPE));
        }
        catch (JAXBException e)
        {
            throw new OverlayException(e.getMessage(), e);
        }
    }

    public static void removeResource(Peer master, NodeDetails resource) throws OverlayException
    {
        WebTarget target = buildWebTarget(master);

        try
        {
            ResourceDetails details = new ResourceDetails()
                    .setName(
                            String.format(DiscoveryService.CLUSTER_MEMBERS_NAME_PATH, resource.getProvider().getName(), resource.getParent().getId()))
                    .setType(resource.getClass()).setPayload(FACTORY.marshal(resource));

            target.path("cluster/members/remove").request().post(Entity.entity(details, APPLICATION_JSON_TYPE));
        }
        catch (JAXBException e)
        {
            throw new OverlayException(e.getMessage(), e);
        }
    }

    public static ResourceDetails buildResourceDetailsForClusterMember(ProviderDetails provider, NodeDetails manager, NodeDetails member)
    {
        return buildResourceDetailsFor(DiscoveryUtils.buildClusterNameFor(provider, manager), member);
    }
    
    public static ResourceDetails buildResourceDetailsFor(String name, NodeDetails node)
    {
        ResourceDetails resource = null;
        
        try
        {
            resource = new ResourceDetails()
                    .setName(name)
                    .setType(node.getClass())
                    .setPayload(FACTORY.marshal(node));
        }
        catch (JAXBException exception)
        {
            LOG.error(exception.getMessage(), exception);
        }
        
        return resource;
    }

    /**
     * @param server
     * @return
     */
    private static WebTarget buildWebTarget(String host)
    {
        Client client = ClientBuilder.newClient().register(ObjectMapperProvider.class).register(JacksonFeature.class);

        WebTarget target = client.target(String.format("http://%s:%s/discovery", host,
                System.getProperty("org.excalibur.server.port", "8080")));

        return target;
    }
    
    private static WebTarget buildWebTarget(Peer peer)
    {
        return buildWebTarget(peer.getHost());
    }
}
