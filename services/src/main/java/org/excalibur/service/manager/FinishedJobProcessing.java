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
package org.excalibur.service.manager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static org.excalibur.core.cloud.api.compute.ComputeServiceBuilder.builder;

import java.util.Map;
import java.util.concurrent.Callable;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinishedJobProcessing implements Callable<Void>
{
    private final static Logger LOG = LoggerFactory.getLogger(FinishedJobProcessing.class.getName());
    
    private final ApplicationDescriptor job_;
    private final Instances nodes_;
    private final UserProviderCredentials credentials_;

    private final UserService userService_;

    public FinishedJobProcessing(ApplicationDescriptor job, Instances nodes, UserProviderCredentials credentials, UserService userService)
    {
        this.job_ = checkNotNull(job);
        this.nodes_ = checkNotNull(nodes);
        this.credentials_ = checkNotNull(credentials);
        this.userService_ = userService;
    }

    @Override
    public Void call() throws Exception
    {
        Map<ProviderSupport, Instances> vmByProviders = newHashMap();

        for (VirtualMachine vm : nodes_)
        {
            Instances instances = vmByProviders.get(vm.getType().getProvider().getName());

            if (instances == null)
            {
                vmByProviders.put(vm.getType().getProvider(), instances = new Instances());
            }

            instances.addInstance(vm);
        }

        for (ProviderSupport provider : vmByProviders.keySet())
        {
            VirtualMachine machine = vmByProviders.get(provider.getName()).first().get();
            UserProviderCredentials credential = this.credentials_;

            if (!provider.equals(machine.getType().getProvider()))
            {
                credential = this.userService_.findUserProviderCredentials(new User().setId(this.credentials_.getUserId()), provider);
                
                LoginCredentials loginCredentials = credential.getLoginCredentials()
                        .toBuilder()
                        .credentialName(machine.getConfiguration().getKeyName())
                        .build();

                credential.setLoginCredentials(loginCredentials)
                          .setRegion(machine.getLocation().getRegion());

            }

            try (ComputeService service = builder().credentials(credentials_).provider(provider).build())
            {
                switch (job_.getOnFinished())
                {
                case TERMINATE:
                    try
                    {
                        service.terminateInstances(vmByProviders.get(provider));
                    }
                    catch (Exception exception)
                    {
                        LOG.error("Error on terminating the instances", exception.getMessage(), exception);
                    }
                    break;
                case SHUTDOWN:
                    try
                    {
                        service.stop(vmByProviders.get(provider));
                    }
                    catch (Exception exception)
                    {
                        LOG.error("Error on shutdown the instances", exception.getMessage(), exception);
                    }
                    break;
                case NONE:
                    break;
                }
            }
        }

        return null;
    }
}
