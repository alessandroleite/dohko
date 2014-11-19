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

import static com.google.common.base.Preconditions.checkNotNull;
import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.impl.AbstractTaskTypeSupport;
import org.excalibur.core.task.impl.TaskResultBuilder;
import org.excalibur.core.util.SecurityUtils2;
import org.excalibur.core.workflow.domain.TaskDescription;

public class ImportKeyPairTask extends AbstractTaskTypeSupport<KeyPair>
{
    public ImportKeyPairTask(TaskDescription task)
    {
        super(task);
    }

    @Override
    protected TaskResult<KeyPair> doExecute(TaskContext context)
    {
        TaskResultBuilder<KeyPair> result = new TaskResultBuilder<KeyPair>().setStartTimeInMillis(System.currentTimeMillis()).setState(TaskState.EXECUTING);
        
        String provider = null;
        String region = null;
        String keyName = null;
        
        UserProviderCredentials userProviderCredentials = context.getUserRepository()
                .findLoginCredentialsOfUserForProvider(getDescription().getActivity().getWorkflow().getUser().getId(), provider);
        
        userProviderCredentials.setRegion(context.getRegionRepository().findByName(region));
        checkNotNull(userProviderCredentials.getRegion());
        
        LoginCredentials loginCredentials = userProviderCredentials.getLoginCredentials().toBuilder().credentialName(keyName).build();
        
        userProviderCredentials.setLoginCredentials(loginCredentials);
        
        ComputeService compute = (ComputeService) new Mirror()
                .on(userProviderCredentials.getProvider().getServiceClass())
                .invoke()
                .constructor()
                .withArgs(userProviderCredentials);
        
        try
        {
            UserKey key = SecurityUtils2.generateUserKey();
            
            KeyPair keyPair = new KeyPair().setKeyName(keyName);
            compute.importKeyPair(keyPair.setKeyMaterial(key.getPrivateKeyMaterial()));
            result.setState(TaskState.SUCCESS);
            
            result.setTaskResult(keyPair);
        }
        catch (Exception e)
        {
            result.setState(TaskState.ERROR, e);
        }
        
        return result.setFinishTimeInMillis(System.currentTimeMillis()).build();
    }
}
