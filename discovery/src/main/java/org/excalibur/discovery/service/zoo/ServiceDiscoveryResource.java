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
package org.excalibur.discovery.service.zoo;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.excalibur.discovery.domain.ServiceDetails;

import com.google.common.io.Closeables;

public class ServiceDiscoveryResource<T> implements Closeable
{
    private final ServiceDiscovery<T> discovery;
    private final AtomicBoolean       started = new AtomicBoolean(false);

    private ServiceDiscoveryResource(CuratorFramework client, String basePath, Class<T> payloadClass) throws Exception
    {
        this.discovery = ServiceDiscoveryBuilder.builder(payloadClass).basePath(basePath).client(client).build();
    }

    public List<T> getServices(String name) throws Exception
    {
        List<T> services = new ArrayList<T>();
        
        for (ServiceInstance<T> instance : discovery.queryForInstances(name))
        {
            services.add(instance.getPayload());
        }
        
        return Collections.unmodifiableList(services);
    }

    public <E> List<ServiceDetails> getServicesOfType(Class<E> type)
    {
        return null;
    }
    
    public void start() throws Exception
    {
        if (started.compareAndSet(false, true))
        {
            discovery.start();
        }
    }

    @Override
    public void close() throws IOException
    {
        if (started.compareAndSet(true, false))
        {
            Closeables.close(this.discovery, true);
        }
    }
}
