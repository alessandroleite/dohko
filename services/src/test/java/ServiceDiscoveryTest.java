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
import java.util.Collection;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.codehaus.jackson.map.ObjectMapper;
import org.excalibur.discovery.domain.ServiceDetails;


public class ServiceDiscoveryTest
{
    public static void main(String[] args) throws Exception
    {
        CuratorFramework client = CuratorFrameworkFactory
                .newClient("ec2-54-211-43-119.compute-1.amazonaws.com:2181,ec2-54-198-130-168.compute-1.amazonaws.com,127.0.0.1:2181", 
                        new ExponentialBackoffRetry(1000, 3));
        client.start();
        
        ServiceDetails deployment = new ServiceDetails()
                  .setId("deployment")
                  .setName("deployment")
                  .setEndpoint("http://localhost:8080/excalibur/deployment/v1/")
                  .setVersion("v1.0")
                  .setProvider("aws")
                  .setPort(8080);
        
        ServiceDetails workflow = new ServiceDetails()
                 .setId("workflow")
                 .setName("workflow")
                 .setEndpoint("http://localhost:8080/excalibur/workflow/v1/")
                 .setVersion("1")
                 .setProvider("aws")
                 .setPort(8080);
        
        ObjectMapper mapper = new ObjectMapper();
//        String data = mapper.writeValueAsString(service);
        
//        client.create().forPath("/services");
//        client.create().withMode(CreateMode.EPHEMERAL).forPath("/services/deployment", data.getBytes());
        
//        mapper.readValue(client.getData().forPath("/services/deployment"), ServiceDetails.class);
        
        ServiceDiscovery<ServiceDetails> discovery = ServiceDiscoveryBuilder.builder(ServiceDetails.class)
                .client(client)
                .basePath("/services")
                .build();
        
        InstanceServer server = new InstanceServer(client, "/services", deployment);
        server.start();
        
        new InstanceServer(client, "/services", workflow).start();
        new InstanceServer(client, "/services", deployment).start();
        
//        ServiceInstance<ServiceDetails> instance = ServiceInstance.<ServiceDetails>builder()
//                .address(service.getEndpoint())
//                .id(service.getId())
//                .name(service.getName())
//                .payload(service)
//                .serviceType(ServiceType.DYNAMIC).build();
//        
//        discovery.registerService(instance);
        
        Collection<ServiceInstance<ServiceDetails>> services = discovery.queryForInstances("deployment");
        
//        ServiceProvider<ServiceDetails> provider = discovery.serviceProviderBuilder()
//                .serviceName("workflow")
////                .providerStrategy(new ProviderStrategy<ServiceDetails>()
////                {
////                    @Override
////                    public ServiceInstance<ServiceDetails> getInstance(InstanceProvider<ServiceDetails> instanceProvider) throws Exception
////                    {
////                        List<ServiceInstance<ServiceDetails>> instances = instanceProvider.getInstances();
////                        System.err.println(instances.size());
////                        
////                        return null;
////                    }
////                })
//                .build();
//        provider.start();
//        ServiceInstance<ServiceDetails> instance2 = provider.getInstance();
        
        for (ServiceInstance<ServiceDetails> serviceInstance : services)
        {
            System.out.println(serviceInstance);
        }
        
        server.close();
    }
}
