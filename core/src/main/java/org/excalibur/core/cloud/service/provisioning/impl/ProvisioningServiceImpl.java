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
package org.excalibur.core.cloud.service.provisioning.impl;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.service.provisioning.ProvisioningService;
import org.excalibur.core.compute.monitoring.domain.provisioning.listener.ProvisioningListener;
import org.excalibur.core.domain.repository.InstanceRepository;

public class ProvisioningServiceImpl implements ProvisioningService
{
    private InstanceRepository instanceRepository;
    
    @Override
    public Instances runInstances(InstanceTemplate request, ComputeService connector)
    {
        Instances instances = connector.createInstances(request);
        
        for (VirtualMachine instance: instances)
        {
            this.instanceRepository.insertInstance(instance);
        }
        
        return instances;
    }

    @Override
    public void runInstances(InstanceTemplate request, ComputeService connector, ProvisioningListener listener)
    {
    }
}
