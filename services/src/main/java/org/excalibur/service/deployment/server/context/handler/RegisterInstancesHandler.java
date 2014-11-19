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
package org.excalibur.service.deployment.server.context.handler;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Maps.newHashMap;
import static org.excalibur.core.cloud.api.compute.ComputeServiceBuilder.builder;
import static org.excalibur.core.io.utils.IOUtils2.closeQuietly;
import static org.excalibur.core.util.SystemUtils2.getBooleanProperty;

import java.util.Date;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.excalibur.core.cloud.api.InstanceStateDetails;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.ProviderRepository;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.services.UserService;
import org.excalibur.service.manager.Configuration;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Maps;

public class RegisterInstancesHandler extends AbstractApplicationInitializedHandler
{

    @Override
    public void handlerApplicationInitializedEvent(Configuration configuration, ApplicationContext context, ServletContextEvent sce)
    {
        Instances instances = new Instances();

        InstanceService instanceService = context.getBean(InstanceService.class);
        boolean isLocal = getBooleanProperty("org.excalibur.environment.local", false);

        String hostname = System.getProperty("org.excalibur.instance.hostname");
        checkState(!isNullOrEmpty(hostname));

        if (!isLocal)
        {
            Map<String, VirtualMachine> instancesToUpdate = Maps.newHashMap();
            Map<String, ComputeService> services = newHashMap();

            try
            {
                for (VirtualMachine vm : instanceService.listRunningInstances(configuration.getUser()))
                {
                    ComputeService compute = createAndGetComputeServiceIfDoesNotExist(vm.getType().getProvider(), configuration, vm, context,
                            services);
                    VirtualMachine machine = null;

                    try
                    {
                        machine = compute.getInstanceWithName(vm.getName(), vm.getLocation().getName());
                    }
                    catch (Exception exception)
                    {
                        LOG.error("Error on requesting instance [{}] state on provider [{}], zone [{}]. Error message: [{}]", vm.getName(), vm
                                .getType().getProvider().getName(), configuration.getZone().getName(), exception.getMessage(), exception);
                    }

                    if (machine == null || vm.getState() != null && vm.getState().getState().equals(InstanceStateType.TERMINATED))
                    {
                        vm.setState(new InstanceStateDetails().setInstance(vm).setState(InstanceStateType.TERMINATED).setTime(new Date()));

                        instanceService.updateInstanceState(vm);
                    }
                    else
                    {
                        instancesToUpdate.put(machine.getName(), machine);
                    }
                }

                instanceService.insertOrUpdateInstances(instancesToUpdate.values());
                instances.addInstances(instanceService.listRunningInstances(configuration.getUser()));

                instances.addInstances(importAllInstancesOfProviders(configuration, services, context));
            }
            finally
            {
                closeQuietly(services.values());
            }
        }
    }
    
    protected ComputeService createAndGetComputeServiceIfDoesNotExist(ProviderSupport provider, Configuration configuration, VirtualMachine instance, 
            ApplicationContext context, Map<String, ComputeService> services)
    {
        ComputeService compute = services.get(provider.getName());
        
        if (compute == null)
        {
            UserProviderCredentials credentials = configuration.getCredentials();
            
            if (!instance.getType().getProvider().equals(credentials.getProvider()))
            {
                credentials = context.getBean(UserService.class).getUserProviderCredentials
                        (
                                configuration.getUser().getUsername(), instance.getType().getProvider().getName(), 
                                instance.getLocation().getName(), instance.getConfiguration().getKeyName()
                        );
            }
            
            compute = builder().credentials(credentials).provider(instance.getType().getProvider()).build();
            services.put(instance.getType().getProvider().getName(), compute);
        }
        
        return compute;
    }
    
    protected Instances importAllInstancesOfProviders(Configuration configuration, Map<String, ComputeService> services, ApplicationContext context)
    {
        Instances instances = new Instances();
        
        for (final Provider provider: context.getBean(ProviderRepository.class).getAllProviders())
        {
            ComputeService service = services.get(provider.getName());
            
            UserProviderCredentials credentials = configuration.getCredentials();
            
            if (service == null)
            {
                if (!provider.equals(credentials.getProvider()))
                {
                    credentials = context.getBean(UserService.class).getUserProviderCredentials
                    (
                            configuration.getUser().getUsername(),
                            provider.getName(), 
//                            configuration.getZone().getName(),
                            "us-central1-a",
                            configuration.getCredentials().getLoginCredentials().getCredentialName()
                    );
                }
                
                service = builder().credentials(credentials).provider(credentials.getProvider()).build();
                services.put(provider.getName(), service);
            }
            
            instances.addInstances(service.aggregateInstances().getInstances());
        }
        
        context.getBean(InstanceService.class).insertOrUpdateInstances(instances);
        return instances;
    }
}
