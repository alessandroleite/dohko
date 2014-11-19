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
package org.excalibur.discovery.service.p2p;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.discovery.domain.ResourceDetails;
import org.excalibur.discovery.service.DiscoveryService;
import org.excalibur.jackson.databind.JsonJaxbObjectMapper;

import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.chord.StringKey;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class DiscoveryServiceImpl implements DiscoveryService
{
    private final Overlay      overlay_;
    private final ObjectMapper mapper;
    private final Lock         lock_;

    public DiscoveryServiceImpl(Overlay overlay)
    {
        this.overlay_ = checkNotNull(overlay);
        mapper = new JsonJaxbObjectMapper();
        lock_ = new ReentrantLock();
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> List<T> queryForResource(String name) throws Exception 
    {
        List<T> result = new ArrayList<T>();
        lock_.lock();
        
        try
        {
            Set<Serializable> resources = overlay_.retrieve(new StringKey(checkNotNull(name)));

            for (Serializable resource : resources)
            {
                ResourceDetails r = mapper.readValue(resource.toString().getBytes(), ResourceDetails.class);
                JAXBContextFactory factory = new JAXBContextFactory(r.getType());
                result.add((T) factory.unmarshal(r.getPayload()));
            }
        }
        finally
        {
            lock_.unlock();
        }
        
        return result;
    }

    @Override
    public <T> void registerResource(String name, T resource) throws Exception
    {
        registerResources(name, Collections.singleton(resource));
    }

    @Override
    public <T> void registerResources(String name, Iterable<T> resources) throws Exception
    {
        checkArgument(name != null && !isNullOrEmpty(name), "Resource's name was null or empty");
        checkArgument(resources != null, "The resource to register was null");
        
        lock_.lock();
        
        try
        {
            JAXBContextFactory<T> factory = null;
            for (T resource : resources)
            {
                if (factory == null)
                {
                    factory = new JAXBContextFactory<T>(resource.getClass());
                }

                String payload = factory.marshal(resource);
                String value = mapper.writeValueAsString(new ResourceDetails().setName(name).setPayload(payload).setType(resource.getClass()));
                this.overlay_.insert(new StringKey(name), value);
            }

        }
        finally
        {
            lock_.unlock();
        }
    }
    
    @Override
    public void registerResource(ResourceDetails resource) throws Exception
    {
        checkNotNull(resource);
        this.registerResources(resource.getName(), Collections.singleton(resource));
    }
    
    @Override
    public void unregisterResource(ResourceDetails resource) throws Exception
    {
        lock_.lock();
        
        try
        {
            String value = mapper.writeValueAsString(resource);
            this.overlay_.remove(new StringKey(resource.getName()), value);
            
        }finally
        {
            lock_.unlock();
        }
    }
}
