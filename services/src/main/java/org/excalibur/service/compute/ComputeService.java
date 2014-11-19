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
package org.excalibur.service.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.discovery.domain.ServiceDetails;
import org.excalibur.discovery.domain.ServiceType;
import org.excalibur.discovery.service.DiscoveryService;

public class ComputeService
{
    private final DiscoveryService discovery;
    
    public ComputeService(DiscoveryService discoveryService)
    {
        this.discovery = checkNotNull(discoveryService);
    }

    public void registerServices(List<ServiceDetails> services) throws Exception
    {
        for (ServiceDetails service : services)
        {
            if (service != null)
            {
                discovery.registerResource("/services/" + service.getType().name(), service);
            }
        }
    }
    
    public List<ServiceDetails> listServices() throws Exception
    {
        discovery.queryForResource("/services/" + ServiceType.COMPUTE.name());
        return null;
    }
    

    public List<InstanceType> listInstanceTypes() throws Exception
    {
        List<InstanceType> instanceTypes = discovery.queryForResource("/resources/" + ServiceType.COMPUTE.name() + "/instanceTypes"  );
        return instanceTypes;
    }
    
    public void registerInstanceTypes(Iterable<InstanceType> instanceTypes) throws Exception
    {
        String name = "/resources/" + ServiceType.COMPUTE.name() + "/instanceTypes";
        
        for(InstanceType instanceType: instanceTypes)
        {
            this.discovery.registerResource(name, instanceType);
        }
    }
    
    public void registerNodes(Iterable<VirtualMachine> nodes) throws Exception
    {
    }
}
