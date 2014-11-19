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

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.exec.ApplicationExecutionHandler;
import org.excalibur.core.exec.RemoteScriptStatementExecutor;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.domain.ApplicationExecutionResult;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.service.ApplicationExecutionService;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.core.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.net.HostAndPort.*;
import static java.util.Collections.*;
import static org.excalibur.core.util.concurrent.DynamicExecutors.newListeningDynamicScalingThreadPool;

public class ConfigureInstanceTask implements Callable<List<ApplicationExecutionResult>>
{
    private static final Logger LOG = LoggerFactory.getLogger(ConfigureInstanceTask.class.getName());

    private final VirtualMachine instance_;

    private final ApplicationExecutionService applicationExecutionService_;

    public ConfigureInstanceTask(VirtualMachine instance, ApplicationExecutionService applicationExecutionService)
    {
        this.instance_ = checkNotNull(instance);
        this.applicationExecutionService_ = checkNotNull(applicationExecutionService);
    }

    @Override
    public List<ApplicationExecutionResult> call() throws Exception
    {
        List<ApplicationExecutionResult> results = newArrayList();
        
        final ListeningExecutorService executionHandlerService = newListeningDynamicScalingThreadPool("configure-instance-task-result-handler", 
                Runtime.getRuntime().availableProcessors());

        HostAndPort hostAndPort = fromParts(instance_.getConfiguration().getPublicIpAddress(), 22);

        UserKey userKey = instance_.getOwner().getKey(instance_.getConfiguration().getKeyName());
        checkNotNull(userKey);

        File sshKey = SystemUtils2.writeUserkey(userKey);

        LoginCredentials loginCredentials = new LoginCredentials.Builder()
                .authenticateAsSudo(true)
                .privateKey(sshKey)
                .user(instance_.getConfiguration().getPlatformUserName())
                .build();
        
        LOG.info("Instance [{}] state [{}]", instance_.getName(), instance_.getState().getId());

        try (RemoteScriptStatementExecutor executor = new RemoteScriptStatementExecutor(instance_, loginCredentials, executionHandlerService))
        {
            LOG.info("Connected to instance [{}] host and port [{}]", instance_.getName(), hostAndPort);

            for (final ApplicationExecDescription application : applicationExecutionService_.listPendentExecutionsForInstance(instance_))
            {
                final ScriptStatement statement = application.getApplication();
                
                executor.execute(application, new ApplicationExecutionHandler()
                {
                    @Override
                    public void onStarted(ApplicationExecutionResult application)
                    {
                        LOG.info("Starting the application [{}] on instance [{}]", statement.getName(), instance_.getName());
                        applicationExecutionService_.insertStartExecution(application);
                    }
                    
                    @Override
                    public void onFinished(ApplicationExecutionResult result)
                    {
                        LOG.info("Finished the execution of application [{}] finished on instance [{}] with exit code [{}], error message [{}]",
                                statement.getName(), instance_.getName(), result.getExitCode(), result.getError());

                        applicationExecutionService_.insertFinishedExecution(result);

                    }
                });
            }
        }
        catch (Exception exception)
        {
            LOG.error("Error in executing the instance task configuration [{}]", exception.getMessage(), exception);
        }
        
        ThreadUtils.awaitTerminationAndShutdownAndIgnoreInterruption(executionHandlerService, 30, TimeUnit.SECONDS);

        return unmodifiableList(results);
    }

   
}
