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
package org.excalibur.service;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static java.lang.System.*;
import static org.excalibur.core.io.utils.IOUtils2.*;

import java.lang.instrument.Instrumentation;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.compute.ComputeServiceBuilder;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.UserProviderCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Agent
{
    private static final Logger LOG = LoggerFactory.getLogger(Agent.class.getName());
    
    public static void premain(String agentArgs, Instrumentation inst)
    {
        String region = getenv("NODE_REGION_NAME");
        region = isNullOrEmpty(region) ? getProperty("org.excalibur.provider.region.name") : region;
        
        String regionEndpoint = getenv("NODE_REGION_ENDPOINT");
        regionEndpoint = isNullOrEmpty(regionEndpoint) ? getProperty("org.excalibur.provider.region.endpoint") : regionEndpoint;
        
        String zone = getenv("NODE_ZONE_NAME");
        zone = isNullOrEmpty(zone) ? getProperty("org.excalibur.provider.region.zone.name") : zone;
        
        String nodeName = getenv("NODE_NAME");
        nodeName = isNullOrEmpty(nodeName) ? getProperty("org.excalibur.instance.hostname") : nodeName;
        
        String provider = getenv("NODE_PROVIDER_NAME");
        provider = isNullOrEmpty(provider) ? getProperty("org.excalibur.provider.name") : provider;
        
        String providerClassName = getenv("NODE_PROVIDER_CLASS_NAME");
        providerClassName = isNullOrEmpty(provider) ? getProperty("org.excalibur.provider.driver.class.name") : providerClassName;
        
        String projectName = getenv("NODE_PROJECT_NAME");
        projectName = isNullOrEmpty(projectName) ? getProperty("org.excalibur.user.project.name") : projectName;
        
        String credentialName = getenv("NODE_KEY_NAME");
        credentialName = isNullOrEmpty(credentialName) ? getProperty("org.excalibur.user.keyname") : credentialName;
        
        String credential = getenv("NODE_ACCESS_CREDENTIAL");
        credential = isNullOrEmpty(credential) ? getProperty("org.excalibur.user.provider.access.credential") : credential;
        
        String identity = getenv("NODE_ACCESS_IDENTITY");
        identity = isNullOrEmpty(identity) ? getProperty("org.excalibur.user.provider.identity") : identity;
        
        UserProviderCredentials credentials = new UserProviderCredentials();
        LoginCredentials loginCredentials = LoginCredentials.builder()
                .credential(credential)
                .identity(identity)
                .credentialName(credentialName)
                .build();
        
        credentials.setLoginCredentials(loginCredentials)
                   .setProject(projectName)
                   .setProvider(new ProviderSupport().setName(provider).setServiceClass(providerClassName))
                   .setRegion(new Region().setName(region).setEndpoint(regionEndpoint).addZone(new Zone().setName(zone)));
        
        ComputeService service = ComputeServiceBuilder.builder().credentials(credentials).provider(credentials.getProvider()).build();
        
        try
        {
            final VirtualMachine node = service.getInstanceWithName(nodeName, zone);

            checkArgument(node != null, "Node [%s] does not exist on provider [%s] on region/zone [%s/%s]", nodeName, provider, region, zone);
            
            Tag bootstrapTag = new Tag().setName("is-bootstrap").setValue(String.valueOf(true));
            
            boolean isBootstrap = node.getTags().contains(bootstrapTag);
            
            setProperty("org.excalibur.server.host", node.getConfiguration().getPublicIpAddress());
            setProperty("org.excalibur.server.host.internal", node.getConfiguration().getPrivateIpAddress());
            setProperty("org.excalibur.overlay.is.bootstrap", String.valueOf(isBootstrap));
            setProperty("org.excalibur.rabbit.host", node.getConfiguration().getPrivateIpAddress());
            
            LOG.info("Node [{}] has internal/external address [{}/{}] on provider [{}] on region/zone [%s/%s]", 
                    node.getConfiguration().getPublicIpAddress(),
                    node.getConfiguration().getPrivateIpAddress(),
                    provider, 
                    region,
                    zone);

            if (!isBootstrap)
            {
                Instances instances = service.listInstancesWithTags(new Tag().setName("is-bootstrap").setValue(String.valueOf(true)));
                
                if (!instances.isEmpty())
                {
                    VirtualMachine bootstrap = instances.first().get();
                    setProperty("org.excalibur.overlay.bootstrap.address", bootstrap.getConfiguration().getPublicIpAddress());
                    //TODO check if it is alive
                }
            }
        }
        finally
        {
            closeQuietly(service);
        }
    }
}
