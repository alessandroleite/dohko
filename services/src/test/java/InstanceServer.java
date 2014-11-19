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
import java.io.Closeable;
import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.excalibur.discovery.domain.ServiceDetails;

import com.google.common.io.Closeables;

public class InstanceServer implements Closeable
{
    private final ServiceInstance<ServiceDetails> thisInstance;
    private final ServiceDiscovery<ServiceDetails> serviceDiscovery;

    public InstanceServer(CuratorFramework client, String path, ServiceDetails details) throws Exception
    {
        UriSpec uriSpec = new UriSpec("{scheme}://localhost:{port}");

        thisInstance = ServiceInstance.<ServiceDetails> builder()
                .name(details.getName())
                .payload(details)
                .port(details.getPort())
                .uriSpec(uriSpec)
                .build();
        
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetails.class)
                .client(client)
                .basePath(path)
                .thisInstance(thisInstance)
                .build();
    }
    
    public ServiceInstance<ServiceDetails> getThisInstance()
    {
        return thisInstance;
    }
    
    public void start() throws Exception
    {
        this.serviceDiscovery.start();
    }

    @Override
    public void close() throws IOException
    {
        Closeables.close(this.serviceDiscovery, true);
    }
}
