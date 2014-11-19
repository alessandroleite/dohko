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

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.excalibur.discovery.domain.NodeDetails;
import org.excalibur.discovery.domain.ProviderDetails;
import org.excalibur.jackson.databind.JsonJaxbObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javax.ws.rs.core.MediaType.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClustersClient extends RestClient
{
    private static final Logger LOG = LoggerFactory.getLogger(ClustersClient.class.getName());
    
    private final static ObjectMapper RESOURCE_MAPPER;
    

    static
    {
        RESOURCE_MAPPER = new JsonJaxbObjectMapper();
    }
    
    
    public ClustersClient(String uri, Integer port)
    {
        super(uri, port);
    }

    public List<NodeDetails> all()
    {
        return null;
    }
    
    public NavigableSet<NodeDetails> clustersOfProvider(ProviderDetails provider)
    {
        return this.clustersOfProvider(provider.getName());
    }

    public NavigableSet<NodeDetails> clustersOfProvider(String name)
    {
        return get(target().path("providers").path(name).path("clusters").request());
    }
    
    public NavigableSet<NodeDetails> membersOfCluster(String provider, String name)
    {
        return get(target().path("providers").path(provider).path("clusters").path(name).path("members").request());
    }
    
    protected NavigableSet<NodeDetails> get(Builder builder)
    {
        List<?> list = builder.accept(MediaType.APPLICATION_JSON_TYPE).get(List.class);
        return convertValue(list, NodeDetails.class);
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

    public void addMember(ProviderDetails provider, String cluster, NodeDetails member)
    {
        Response response = target().path("providers").path(provider.getName()).path("clusters").path(cluster).path("members/add").request()
                .put(Entity.entity(member, APPLICATION_JSON_TYPE));
        
        StatusType status = response.getStatusInfo();
        LOG.debug("Included the mamber [{}]. Status info: [{}]", member, status);
    }
}
