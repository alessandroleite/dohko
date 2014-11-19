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

import javax.annotation.Resource;

import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.deployment.domain.engine.ScriptStatementProcessor;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.exec.HostAndPort;
import org.excalibur.core.exec.RemoteTask;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.service.ApplicationExecutionService;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.services.UserService;
import org.excalibur.service.manager.NodeManagerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("monitorApplicationDescription")
public class MonitorApplicationDescription
{
    @Autowired
    private ApplicationExecutionService applicationExecutionService_;
    
    @Autowired
    private InstanceService instanceService_;
    
    @Autowired
    private UserService userService_;
    
    @Resource(name = "instanceConfigurationAmqpTemplate")
    private RabbitTemplate instanceConfigurationAmqpTemplate_;
    
    public void monitorApplicationDescriptionSubmission(String arguments)
    {
        NodeManagerFactory.getManagerReference();
        
        for (ApplicationExecDescription des: applicationExecutionService_.listPendentExecutions())
        {
            VirtualMachine resource = this.instanceService_.getInstanceByName(des.getResource());
            User user = this.userService_.findUserById(des.getUser().getId());
            UserKey key = user.getKey(resource.getConfiguration().getKeyName());
            
            KeyPairs keyPairs = new KeyPairs().setPrivateKey(new KeyPair().setKeyName(key.getName()).setKeyMaterial(key.getPrivateKeyMaterial()));
            keyPairs.setPublicKey(keyPairs.getPrivateKey().clone().setKeyMaterial(key.getPublicKeyMaterial()));
            
            HostAndPort hostAndPort = new HostAndPort()
                    .setHost(resource.getConfiguration().getPublicIpAddress())
                    .setPort(22)
                    .setProvider(resource.getType().getProvider().getName());

            ScriptStatementProcessor.assignVariableValues(resource, des.getApplication());
            
            RemoteTask task = new RemoteTask()
                    .setApplication(des)
                    .setHostAndPort(hostAndPort)
                    .setKeyPairs(keyPairs)
                    .setUsername(resource.getConfiguration().getPlatformUserName());
    
            instanceConfigurationAmqpTemplate_.convertAndSend(task);
        }
    }
}
