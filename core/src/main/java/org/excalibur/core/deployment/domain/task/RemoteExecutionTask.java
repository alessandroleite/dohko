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

import com.google.common.net.HostAndPort;

import org.excalibur.core.exec.ExecutableResponse;
import org.excalibur.core.execution.logger.LogEntryImpl;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshClientFactory;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.impl.AbstractTaskTypeSupport;
import org.excalibur.core.task.impl.TaskResultBuilder;
import org.excalibur.core.task.remote.RemoteExecutionContext;
import org.excalibur.core.util.Exceptions;
import org.excalibur.core.workflow.domain.TaskDescription;

public class RemoteExecutionTask extends AbstractTaskTypeSupport<String>
{
    public RemoteExecutionTask(TaskDescription task)
    {
        super(task);
    }

    @Override
    protected TaskResult<String> doExecute(TaskContext context)
    {
        TaskResultBuilder<String> result = new TaskResultBuilder<String>();
        result.setTask(this);
        
        RemoteExecutionContext remoteContext = RemoteExecutionContext.class.cast(context.getExecutionContext());

        HostAndPort hostAndPort = HostAndPort.fromParts(remoteContext.getRemoteHost().getConfiguration().getPublicDnsName(), 22);

        SshClient client = SshClientFactory.defaultSshClientFactory().create(hostAndPort, remoteContext.getLoginCredentials());
        client.connect();
        
        try
        {
            result.setStartTimeInMillis(System.currentTimeMillis());
            
            ExecutableResponse response = client.execute(this.getDescription().getExecutable());
            
            result.setFinishTimeInMillis(System.currentTimeMillis())
                  .setState(response.getExitStatus() == 0 ? TaskState.SUCCESS : TaskState.FAILED)
                  .setTaskResult(response.getOutput())
                  .setState(response.getError());
        }
        catch (Exception exception)
        {
            result.setState(TaskState.ERROR, exception);
            context.getLogger().addLogEntry(new LogEntryImpl(Exceptions.toString(exception)));
        }
        finally
        {
            client.disconnect();
        }

        return result.build();
    }
}
