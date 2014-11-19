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
package org.excalibur.core.exec;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.exec.DefaultExecutor;
import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.domain.ApplicationExecutionResult;
import org.excalibur.core.execution.domain.FailureAction;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;

public class RemoteScriptStatementExecutor extends DefaultExecutor implements Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger(RemoteScriptStatementExecutor.class.getName());
    
    private final SshClient sshClient_;
    
    private final VirtualMachine remoteNode_;

    private final AtomicBoolean connected_ = new AtomicBoolean(false);
    
    private final ListeningExecutorService executionHandlerThread_;

    public RemoteScriptStatementExecutor(SshClient sshClient, VirtualMachine remoteNode, ListeningExecutorService executionHandlerService)
    {
        this.sshClient_ = checkNotNull(sshClient);
        this.remoteNode_ = checkNotNull(remoteNode);
        this.executionHandlerThread_ = checkNotNull(executionHandlerService);
        
//        executionHandlerThread_ = DynamicExecutors.newListeningDynamicScalingThreadPool("execution-handler-thread-%d");
    }

    public RemoteScriptStatementExecutor(VirtualMachine node, LoginCredentials loginCredentials, ListeningExecutorService executionHandlerService)
    {
        this(SshClientFactory.defaultSshClientFactory().create(HostAndPort.fromParts(node.getConfiguration().getPublicIpAddress(), 22),
                loginCredentials), node, executionHandlerService);
    }

    @Override
    public void close() throws IOException
    {
        if (this.connected_.compareAndSet(true, false))
        {
            this.sshClient_.disconnect();
        }
    }
    
    public void execute(final ApplicationExecDescription application, final ApplicationExecutionHandler executionHandler)
    {
        if (connected_.compareAndSet(false, true))
        {
            LOG.debug("Connecting to host: [{}], user: [{}]", sshClient_.getHostAddress(), sshClient_.getUsername());
            
//            try
//            {
                this.sshClient_.connect();
//            }
//            catch (Exception ex)
//            {
//                LOG.error("Connection failed! Error message: [{}]", ex.getMessage(), ex);
//                connected.set(false);
//            }
        }
        
        
        LOG.debug("Connected to host: [{}], user: [{}]", sshClient_.getHostAddress(), sshClient_.getUsername());
        
        for (int i = 0; i < application.getNumberOfExecutions(); i++)
        {
            final ApplicationExecutionResult result = new ApplicationExecutionResult()
                    .setApplication(application)
                    .setStartedIn(new Date())
                    .setInstance(this.remoteNode_);
            
            if (executionHandler != null)
            {
                executionHandlerThread_.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        executionHandler.onStarted(result);
                    }
                });
            }
            
            ExecutableResponse executableResponse = null;
            
            try
            {
                LOG.debug("Executing application/script: [{}] on host: [{}]", application.getName(), sshClient_.getHostAddress());
                executableResponse = sshClient_.execute(application.getApplication().getStatement());
                result.setError(executableResponse.getError())
                        .setExitCode(executableResponse.getExitStatus())
                        .setOutput(executableResponse.getOutput())
                        .setFinishedIn(new Date());
                
                LOG.debug("Executed the application/script: [{}] on host: [{}] with exit code: [{}]", 
                        application.getName(), 
                        sshClient_.getHostAddress(),
                        executableResponse.getExitStatus());
            }
            catch (Exception exception)
            {
                result.setError(result.getError() + " " + exception.getMessage());
                LOG.error("Error on executing the application: [{}] on host: [{}]. Error message: [{}]", 
                        application.getName(),
                        sshClient_.getHostAddress(), 
                        exception.getMessage(), 
                        exception);
            }
            
            executionHandlerThread_.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    executionHandler.onFinished(result);
                }
            });
            
            if (executableResponse != null && executableResponse.getExitStatus() != 0 && FailureAction.ABORT.equals(application.getFailureAction()))
            {
                break;
            }
        }
    }
}
