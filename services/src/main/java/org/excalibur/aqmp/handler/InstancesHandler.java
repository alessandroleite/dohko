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
package org.excalibur.aqmp.handler;

import java.io.IOException;

import javax.annotation.Resource;

import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.deployment.domain.engine.ScriptStatementProcessor;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.exec.HostAndPort;
import org.excalibur.core.exec.RemoteTask;
import org.excalibur.core.exec.RemoteTasks;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.service.ApplicationExecutionService;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.services.UserService;
import org.excalibur.core.util.AnyThrow;
import org.excalibur.service.manager.NodeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

@Component("newInstancesHandler")
public class InstancesHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(InstancesHandler.class.getName());

    @Autowired
    private InstanceService instanceService_;

    @Autowired
    private UserService userService_;

    @Autowired
    private ApplicationExecutionService applicationExecutionService_;

    @Autowired
    private RegionRepository regionRepository_;

    @Resource(name = "instanceConfigurationAmqpTemplate")
    private RabbitTemplate instanceConfigurationAmqpTemplate_;

    public void handleNewInstances(Instances instances) throws IOException
    {
        NodeManagerFactory.getManagerReference();
        LOG.debug("Received [{}] new instance(s) to configure...", instances.size());

        try
        {
            try
            {
                this.instanceService_.insertOrUpdateInstances(instances);
            }
            catch (Throwable exception)
            {
                LOG.error("Error on inserting the new instance(s). Error message [{}]", exception.getMessage(), exception);
            }

            for (final VirtualMachine instance : instances)
            {
                if (instance.getConfiguration().getCredentials() == null)
                {
                    LOG.debug("Instance [{}] does not have a credential reference. Owner [{}], keyname [{}] ", 
                            instance.getName(), 
                            instance.getOwner().getUsername(), 
                            instance.getConfiguration().getKeyName());

                    UserProviderCredentials credentials = this.userService_.getUserProviderCredentials(instance.getOwner().getUsername(), instance
                            .getType().getProvider().getName(), instance.getLocation().getName(), instance.getConfiguration().getKeyName());

                    instance.getConfiguration().setCredentials(credentials);

                    LOG.debug("Assigned one credential for instance [{}]. Owner [{}], keyname [{}] ",
                            instance.getName(), 
                            instance.getOwner().getUsername(), 
                            instance.getConfiguration().getKeyName());
                }

                if (!isNullOrEmpty(instance.getConfiguration().getPublicIpAddress()))
                {
                    if (instance.getLocation() == null)
                    {
                        instance.setLocation(new Zone().setName(instance.getPlacement().getZone()));
                    }

                    if (instance.getLocation().getRegion() == null)
                    {
                        instance.setLocation(this.regionRepository_.findZoneByName(instance.getLocation().getName()));
                    }

                    instance.getType().setRegion(instance.getLocation().getRegion());
                    // KeyPair keyPair = instance.getConfiguration().getKeyPairs().getPrivateKey();

                    User owner = instance.getOwner();
                    userService_.insertUserKey(owner, instance.getConfiguration().getKeyPairs());

                    owner = this.userService_.findUserByUsername(owner.getUsername());
                    instance.setOwner(checkNotNull(owner));

                    UserKey key = owner.getKey(instance.getConfiguration().getKeyName());
                    checkNotNull(key);

                    // KeyPairs keyPairs = new KeyPairs().setPrivateKey(new
                    // KeyPair().setKeyName(key.getName()).setKeyMaterial(key.getPrivateKeyMaterial()));
                    // keyPairs.setPublicKey(keyPairs.getPrivateKey().clone().setKeyMaterial(key.getPublicKeyMaterial()));

                    KeyPairs keyPairs = key.getKeyPairs();

                    RemoteTasks tasks = new RemoteTasks();

                    for (final ApplicationExecDescription application : applicationExecutionService_.listPendentExecutionsForInstance(instance))
                    {
                        application.setUser(owner);
                        HostAndPort hostAndPort = new HostAndPort().setHost(instance.getConfiguration().getPublicIpAddress()).setPort(22)
                                .setProvider(instance.getType().getProvider().getName());

                        ScriptStatementProcessor.assignVariableValues(instance, application.getApplication());
                        RemoteTask task = new RemoteTask()
                                .setApplication(application)
                                .setHostAndPort(hostAndPort)
                                .setKeyPairs(keyPairs)
                                .setOwner(owner)
                                .setUsername(instance.getConfiguration().getPlatformUserName())
                                .setZone(instance.getLocation());
                        
                        LOG.debug("Created task [{}] for instance [{}], host and port [{}], owner [{}], keyname [{}]",
                                task.getApplication().getName(),
                                instance.getName(),
                                task.getHostAndPort().toString(),
                                task.getOwner().getUsername(),
                                task.getKeyPairs().getName());

                        tasks.add(task);
                    }

                    this.instanceConfigurationAmqpTemplate_.convertAndSend(tasks);
                }
                else
                {
                    LOG.debug("Instance [{}] does not have an external address [{}]",
                            instance.getName(), 
                            Strings.nullToEmpty(instance.getConfiguration().getPublicIpAddress()));
                }
            }
        }
        catch (Exception exception)
        {
            LOG.error("Error on handling the new instances [{}]. Error message [{}]", instances.size(), exception.getMessage(), exception);
            AnyThrow.throwUncheked(exception);
        }
    }

    public void handleNewInstances(VirtualMachine instance) throws IOException
    {
        this.handleNewInstances(new Instances(instance));
    }
}
