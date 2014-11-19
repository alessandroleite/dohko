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
package org.excalibur.discovery.resource;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static org.excalibur.discovery.service.DiscoveryService.CLUSTERS_PATH;
import static org.excalibur.discovery.service.DiscoveryService.CLUSTER_MEMBERS_NAME_PATH;
import static org.excalibur.discovery.service.DiscoveryService.PROVIDERS_PATH;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.discovery.domain.ResourceDetails;
import org.excalibur.discovery.service.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

@Path(ResourceDiscovery.OVERLAY_PATH)
public class ResourceDiscovery
{
    public static final String OVERLAY_PATH = "/discovery";

    @Context
    private UriInfo uriInfo;

    @Context
    private Request request;

    @Autowired
    private DiscoveryService discoveryService;

    @GET
    @Path("providers")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response providers() throws Exception
    {
        return Response.ok().entity(discoveryService.queryForResource(PROVIDERS_PATH)).build();
    }

    @GET
    @Path("providers/{providerName}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getProvider(@PathParam("providerName") String name) throws Exception
    {
        return Response.ok().entity(findProviderByName(name)).build();
    }
    
    @GET
    @Path("providers/{provider}/members")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response members(@Nonnull @PathParam("provider") String name) throws Exception
    {
        if (isNullOrEmpty(name))
        {
            return Response.status(Status.BAD_REQUEST).build();
        }
        
        final ProviderDetails provider = findProviderByName(name);
        final String path = "/providers/%s/clusters/%s/members";
        List<NodeDetails> members = newArrayList();
        
        List<NodeDetails> clusters = discoveryService.queryForResource(String.format(CLUSTERS_PATH, provider.getName()));
        
        if (!clusters.isEmpty())
        {
            for (NodeDetails node: clusters)
            {
                List<NodeDetails> nodes = discoveryService.queryForResource(String.format(path, name, node.getId()));
                members.addAll(nodes);
            }
        }
        
        return Response.ok().entity(members).build();
    }
    
    @GET
    @Path("providers/{provider}/clusters")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response clusters(@Nonnull @PathParam("provider") String name) throws Exception
    {
        final ProviderDetails provider = findProviderByName(name);
        
        if (provider != null)
        {
            List<NodeDetails> clusters = discoveryService.queryForResource(String.format(CLUSTERS_PATH, provider.getName()));
            
            if (!clusters.isEmpty())
            {
                Collection<NodeDetails> transform = Collections2.transform(clusters, new Function<NodeDetails, NodeDetails>()
                {
                    @Override
                    @Nullable
                    public NodeDetails apply(@Nullable NodeDetails input)
                    {
                        return input != null ? input.setProvider(provider) : input;
                    }
                });
                return Response.ok().entity(transform).build();
            }
        }
        return Response.ok().build();
    }
    
    @GET
    @Path("providers/{provider}/clusters/{cluster}/members")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response clusterMembers(@PathParam("provider") String provider, @Nonnull @PathParam("cluster") String cluster) throws Exception
    {
        String path = uriInfo.getPath();
        List<NodeDetails> members = discoveryService.queryForResource(path.substring(path.indexOf("/")));
        return Response.ok().entity(members).build();
    }
    
    @PUT
    @Path("providers/add")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response addProvider(ResourceDetails resource) throws Exception
    {
       return this.add(resource);
    }
    
    @PUT
    @Path("providers/{provider}/clusters/add")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response add(@Nonnull ResourceDetails resource) throws Exception
    {
        this.discoveryService.registerResource(resource);
        return Response.ok().build();
    }
    
    @PUT
    @Path("providers/{provider}/clusters/{cluster}/members/add")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response addClusterMember(@Nonnull NodeDetails member) throws Exception
    {
        String fullName = this.uriInfo.getPath();
        String name = fullName.substring(fullName.indexOf("/"), fullName.lastIndexOf("/"));
        
        discoveryService.registerResource(name, member);
        return Response.ok().build();
    }
        
    @GET
    @Path("clusters")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response clusters() throws Exception
    {
        List<NodeDetails> nodes = newArrayList();
        List<ProviderDetails> providers = discoveryService.queryForResource(PROVIDERS_PATH);

        for (ProviderDetails provider : providers)
        {
            List<NodeDetails> clusters = discoveryService.queryForResource(String.format(CLUSTERS_PATH, provider.getName()));

            for (NodeDetails peer : clusters)
            {
                if (peer.getProvider() == null)
                {
                    peer.setProvider(provider);
                }
                
//                String resourceName = String.format(CLUSTER_MEMBERS_NAME_PATH, provider.getName(), peer.getId());
//                List<NodeDetails> members = discoveryService.queryForResource(resourceName);
//                peer.addChildren(members);
                
                nodes.add(peer);
            }
        }
        return Response.ok().entity(nodes).build();
    }

    @GET
    @Path("members/{providerId}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response listNodes(@PathParam("providerId") @Nonnull String clusterId) throws Exception
    {
        checkState(!isNullOrEmpty(clusterId));
        List<NodeDetails> members = newArrayList();

        List<NodeDetails> clusters = discoveryService.queryForResource(String.format(CLUSTERS_PATH, clusterId));

        for (NodeDetails member : clusters)
        {
            List<NodeDetails> nodes = discoveryService.queryForResource(String.format(CLUSTER_MEMBERS_NAME_PATH, clusterId, member.getId()));
            member.addChildren(nodes);
            members.add(member);
        }

        return Response.ok().entity(members).build();
    }

    @GET
    @Path("clusters/{provider}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Deprecated
    public Response listClusters(@PathParam("provider") @Nonnull final String providerName) throws Exception
    {
        return clusters(providerName);
    }

    @POST
    @Path("cluster/members")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response listClusterMembers(@Nonnull String clusterName) throws Exception
    {
        checkState(!isNullOrEmpty(clusterName), "Cluster's name might not be null or empty");
        
        List<NodeDetails> members = discoveryService.queryForResource(clusterName);
        return Response.ok().entity(members).build();
    }

    @GET
    @Path("cluster/members/{providerId}/{clusterId}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response listClusterMembers(@PathParam("providerId") @Nonnull String provider, @PathParam("clusterId") @Nonnull String clusterName)
            throws Exception
    {
        checkState(!isNullOrEmpty(provider), "Provider's name might not be null or empty");
        checkState(!isNullOrEmpty(clusterName), "Cluster's name might not be null or empty");

        List<NodeDetails> members = discoveryService.queryForResource(String.format(CLUSTER_MEMBERS_NAME_PATH, provider, clusterName));

        return Response.ok().entity(members).build();
    }

    @PUT
    @Path("cluster/members")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response registerResource(@Nonnull ResourceDetails resource) throws Exception
    {
        discoveryService.registerResource(resource);
        return Response.ok().build();
    }

    @POST
    @Path("cluster/members/remove")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response removeResource(@Nonnull ResourceDetails resource) throws Exception
    {
        this.discoveryService.unregisterResource(resource);
        return Response.ok().build();
    }

    @GET
    @Path("resource/{name}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response queryResourceByName(@PathParam("name") @Nonnull String name) throws Exception
    {
        return Response.ok().entity(this.discoveryService.queryForResource(name)).build();
    }

    private ProviderDetails findProviderByName(final String name) throws Exception
    {
        List<ProviderDetails> providers = discoveryService.queryForResource(String.format(PROVIDERS_PATH, name));

        for (ProviderDetails provider : providers)
        {
            if (provider != null && provider.getName().equals(name))
            {
                return provider;
            }
        }

        return null;
    }
}
