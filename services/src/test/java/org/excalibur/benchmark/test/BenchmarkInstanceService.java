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
package org.excalibur.benchmark.test;

import java.util.List;

import javax.annotation.Nonnull;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.services.InstanceService;
import org.excalibur.service.deployment.service.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkInstanceService
{
    @Autowired
    private InstanceService instanceService;
    
    @Autowired
    private DeploymentService deploymentService;
    
    @Autowired
    private RegionRepository regionRepository;
    
    public void deploy(@Nonnull Iterable<InstanceType> types, @Nonnull User user)
    {
        Deployment deployment = new Deployment().withUsername(user.getUsername());
        
        for (InstanceType type: types)
        {
            VirtualMachineImage image = instanceService.listAvailableImagesForInstanceType(type.getName(), type.getRegion().getId()).get(0);
            
            List<Zone> zones = regionRepository.listZoneOfRegion(type.getRegion().getId());
            
            Node node = new Node()
                    .setCount(1)
                    .setCredential(new Credential().setName(user.getUsername()))
                    .setProvider(new Provider().setImageId(image.getName()).setInstanceType(type.getName()).setName(type.getProvider().getName()))
                    .setRegion(type.getRegion().getName())
                    .setZone(zones.get(0).getName());
            
            deployment.withNode(node);
        }
        
        deploymentService.create(deployment);
    }
}
