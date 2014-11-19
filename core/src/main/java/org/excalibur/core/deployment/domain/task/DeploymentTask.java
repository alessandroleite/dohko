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
package org.excalibur.core.deployment.domain.task;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.Placement;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.compute.ComputeServiceBuilder;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.utils.DeploymentUtils;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.execution.logger.LogEntryImpl;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.impl.AbstractTaskTypeSupport;
import org.excalibur.core.task.impl.TaskResultBuilder;
import org.excalibur.core.util.Exceptions;
import org.excalibur.core.workflow.domain.TaskDescription;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.excalibur.core.task.TaskState.ERROR;
import static org.excalibur.core.task.TaskState.SUCCESS;

public class DeploymentTask extends AbstractTaskTypeSupport<Instances>
{
    public DeploymentTask(TaskDescription task)
    {
        super(task);
    }

    @Override
    protected TaskResult<Instances> doExecute(TaskContext context)
    {
        TaskResultBuilder<Instances> builder = new TaskResultBuilder<Instances>();
        builder.setTask(this).setStartTimeInMillis(System.currentTimeMillis());
        
        try
        {
            Node node = DeploymentUtils.unmarshal(this.getDescription().getExecutable());
            User user = getDescription().getActivity().getWorkflow().getUser();
            
            UserProviderCredentials userProviderCredentials = context.getUserRepository()
                    .findLoginCredentialsOfUserForProvider(user.getId(), node.getProvider().getName());
            
            userProviderCredentials.setRegion(context.getRegionRepository().findByName(node.getRegion()));
            checkNotNull(userProviderCredentials.getRegion());
            
            UserKey key = context.getUserRepository().findUserKeyByName(node.getCredential().getName());
            checkNotNull(key);
            
            key.setPrivateKeyMaterial(key.getPrivateKeyMaterial());
            key.setPublicKeyMaterial(key.getPublicKeyMaterial());
            
            LoginCredentials loginCredentials = userProviderCredentials.getLoginCredentials().toBuilder()
                    .credentialName(node.getCredential().getName())
                    .authenticateAsSudo(true)
                    .build();
            
            
            VirtualMachine deployer = context.getExecutionContext().getExecutionEnvironment().getLocation();
            String deployedBy = deployer.getType().getProvider().getName() + "-" + deployer.getLocation().getName() + "-" + deployer.getName();
            
            Tags tags = node.getTags();
            tags.add(new Tag().setName("username").setValue(user.getUsername()), 
                     new Tag().setName("deployedby").setValue(deployedBy),
                     new Tag().setName("is-bootstrap").setValue(System.getProperty("org.excalibur.overlay.is.bootstrap", "false")));
            
            userProviderCredentials.setLoginCredentials(loginCredentials);
            
            ComputeService compute = ComputeServiceBuilder.builder()
                    .credentials(userProviderCredentials)
                    .provider(userProviderCredentials.getProvider())
                    .build();
            
//            (ComputeService) new Mirror()
//                    .on(userProviderCredentials.getProvider().getServiceClass())
//                    .invoke()
//                    .constructor()
//                    .withArgs(userProviderCredentials);
            
            InstanceTemplate request = new InstanceTemplate()
                    .setGroup(new Placement().setGroupName(node.getGroup()).setZone(node.getZone()))
                    .setImageId(node.getProvider().getImageId())
                    .setInstanceName(node.getName())
                    .setInstanceType(node.getProvider().getInstanceType())
                    .setKeyName(loginCredentials.getCredentialName())
                    .setMinCount(1)
                    .setMaxCount(node.getCount())
                    .setRegion(new Region().setName(node.getRegion()))
                    .setLoginCredentials
                    (
                            loginCredentials.toBuilder()
                                            .privateKey(key.getPrivateKeyMaterial())
                                            .publicKey(key.getPublicKeyMaterial())
                                            .user(user.getUsername())
                                            .build()
                    )
                    .setTags(tags)
                    .setUserData(node.getUserData());
            
            Instances instances = compute.createInstances(request);
            builder.setTaskResult(instances).setState(SUCCESS);
        }
        catch (Throwable exception)
        {
            builder.setState(ERROR, exception).setTaskResult(new Instances());
            context.getLogger().addLogEntry(new LogEntryImpl(Exceptions.toString(exception)));
        }
        
        return builder.setFinishTimeInMillis(System.currentTimeMillis()).build();
    }
}
