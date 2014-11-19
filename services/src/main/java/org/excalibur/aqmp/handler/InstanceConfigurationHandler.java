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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.deployment.domain.engine.ScriptStatementProcessor;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.ProviderRepository;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.exec.ApplicationExecutionHandler;
import org.excalibur.core.exec.OnlineChannel;
import org.excalibur.core.exec.RemoteScriptStatementExecutor;
import org.excalibur.core.exec.RemoteTask;
import org.excalibur.core.exec.RemoteTasks;
import org.excalibur.core.execution.domain.ApplicationExecutionResult;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.service.ApplicationExecutionService;
import org.excalibur.core.io.utils.IOUtils2;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.services.UserService;
import org.excalibur.core.ssh.SshClient;
import org.excalibur.core.ssh.SshClientFactory;
import org.excalibur.core.util.AnyThrow;
import org.excalibur.core.util.Strings2;
import org.excalibur.service.manager.NodeManagerFactory;
import org.excalibur.service.status.heartbeat.HeartbeatSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;

import static org.excalibur.core.cipher.TripleDESUtils.*;
import static org.excalibur.core.util.concurrent.DynamicExecutors.*;
import static org.excalibur.core.util.ThreadUtils.*;

@Component("instanceConfigurationHandler")
public class InstanceConfigurationHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(InstanceConfigurationHandler.class.getName());
    
    @Autowired
    private InstanceService instanceService_;

    @Autowired
    private UserService userService_;
    
    @Autowired
    private ApplicationExecutionService applicationExecutionService_;
    
    @Autowired
    private RegionRepository regionRepository_;
    
    @Autowired
    private ProviderRepository providerRepository_;
    
    public void handleInstanceConfigurationTasks(RemoteTask task)
    {
        this.handleInstanceConfigurationTasks(new RemoteTasks().add(task));
    }

    public void handleInstanceConfigurationTasks(RemoteTasks tasks)
    {
        NodeManagerFactory.getManagerReference();
        final ListeningExecutorService executionHandlerService = newListeningDynamicScalingThreadPool("instances-remote-task-result-handler", tasks.size());
        
        VirtualMachine remoteHost = instanceService_.getInstanceByPublicIp(tasks.first().getHostAndPort().getHost());
        
        if (remoteHost == null)
        {
            LOG.debug("Remote host was null. Ignoring [{}] task(s)", tasks.size());
            return;
        }
        
        UserProviderCredentials credentials = null;
        LoginCredentials loginCredentials = null;
        File sshKey = null;
        
        final AtomicInteger count = new AtomicInteger();
        
        for (final RemoteTask task : tasks)
        {
            try
            {
                LOG.debug("Configuring the execution of task [{}] on host [{}] with username [{}]", 
                        task.getApplication().getName(), 
                        task.getHostAndPort().getHost(),
                        task.getUsername());
                
                ProviderSupport provider = (ProviderSupport) this.providerRepository_.findByExactlyProviderName(task.getHostAndPort().getProvider());
                Zone zone = this.regionRepository_.findZoneByName(task.getZone().getName());
                
                if (remoteHost == null)
                {
                    LOG.debug("Reference for host [{}] was null!", task.getHostAndPort().getHost());
                    
                    VmConfiguration configuration = new VmConfiguration()
                            .setKeyName(task.getKeyPairs().getPrivateKey().getKeyName())
                            .setKeyPairs(new KeyPairs().setPrivateKey(task.getKeyPairs().getPrivateKey()))
                            .setPlatformUserName(task.getUsername())
                            .setPublicIpAddress(task.getHostAndPort().getHost())
                            .setPublicDnsName(task.getHostAndPort().getHost());
                    
                    remoteHost = new VirtualMachine()
                           .setConfiguration(configuration)
                           .setType(new InstanceType().setProvider(provider));
                }
                
                if (credentials == null)
                {
                
                credentials = this.userService_.findUserProviderCredentials(task.getOwner(), provider);
                credentials.setLoginCredentials(credentials.getLoginCredentials().toBuilder()
                              .credentialName(task.getKeyPairs().getName())
                              .authenticateAsSudo(true)
                              .build())
                           .setRegion(zone.getRegion());
                
                remoteHost.getConfiguration().setCredentials(credentials);
                
                sshKey = File.createTempFile(String.format("%s_key_", task.getUsername()), ".key");
                Files.write(decrypt(task.getKeyPairs().getPrivateKey().getKeyMaterial()).getBytes(), sshKey);
                
                loginCredentials = new LoginCredentials.Builder()
                        .authenticateAsSudo(true)
                        .privateKey(sshKey)
                        .user(task.getUsername())
                        .build();
                
                }
                
                try (RemoteScriptStatementExecutor executor = new RemoteScriptStatementExecutor(remoteHost, loginCredentials, executionHandlerService))
                {
                    executor.execute(task.getApplication(), new RemoteTaskExecutionHandler(task, count));
                }
                
                LOG.debug("Task [{}] executed on host [{}]", task.getApplication().getName(), task.getHostAndPort().getHost());
            }
            catch (Exception exception)
            {
                LOG.error("Error on executing the task: [{}] on host/username [{}/{}]. Error message: [{}]. Cause: [{}]", 
                          task.getApplication().getName(),
                          task.getHostAndPort().getHost(),
                          task.getUsername(),
                          exception.getMessage(),
                          exception.getCause() != null ? exception.getCause().getMessage() : "",
                          exception);
                
                AnyThrow.throwUncheked(exception);
            }
        }
        
        awaitTerminationAndShutdownAndIgnoreInterruption(executionHandlerService, 1, TimeUnit.MINUTES);
        
        LOG.debug("[{}] of [{}] tasks finished successfully on node [{}]", 
                count.get(), 
                tasks.size(), 
                remoteHost.getConfiguration().getPublicDnsName());
        
        boolean isExcaliburRunning = startExcaliburApplication(remoteHost, tasks.first(), loginCredentials);
        
        if (isExcaliburRunning)
        {
            LOG.debug("Registering the new instance [{}/{}] on application manager", 
                       remoteHost.getName(), 
                       remoteHost.getConfiguration().getPublicIpAddress());

            NodeManagerFactory.getManagerReference().addIdleInstance(remoteHost);

            LOG.debug("Instance [{}] registered on application manager [{}]", 
                      remoteHost.getName(), 
                      NodeManagerFactory.getManagerReference().getThisNodeReference().getName());
        }
    }
    
    private boolean startExcaliburApplication(VirtualMachine remoteHost, RemoteTask task, LoginCredentials loginCredentials)
    {
        LOG.debug("Starting the excalibur application");
        
        HeartbeatSender heartbeatSender = new HeartbeatSender(remoteHost.getConfiguration().getPublicIpAddress());

        try
        {
            String text = IOUtils2.readLines(ClassUtils.getDefaultClassLoader().getResourceAsStream(
                    "org/excalibur/service/deployment/resource/script/05-excalibur-script.script"));

            ScriptStatement script = new ScriptStatement().setStatement(text).setName("excalibur");
            ScriptStatementProcessor.assignVariableValues(remoteHost, script);

            String username = task.getUsername();
            HostAndPort host = HostAndPort.fromParts(remoteHost.getConfiguration().getPublicIpAddress(), 22);
            final StringBuilder output = new StringBuilder();

            final SshClient client = SshClientFactory.defaultSshClientFactory().create(host, loginCredentials);
            client.connect();

            final OnlineChannel shell = client.shell();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getOutput()));

            String home = String.format("/home/%s/excalibur", username);
            String sh = String.format("%s/excalibur.sh", home);
            client.connect();

            client.put(sh, script.getStatement());

            Thread t = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            String line = reader.readLine();
                            if (line == null)
                            {
                                break;
                            }

                            output.append(line).append(Strings2.NEW_LINE);
                        }
                        catch (IOException exception)
                        {
                            LOG.warn("Error on reading the shell's output. Error message [{}]", exception.getMessage());
                        }
                    }
                }
            });

            t.start();

            shell.write(String.format("cd %s\n", home));
            shell.write(String.format("chmod +x %s/*.sh\n", home));
            shell.write("pwd\n");
            shell.write(String.format("nohup %s &\n", sh));
            shell.write(String.format("tail -f %s/nohup.out\n", home));
            
            heartbeatSender.setHeartbeat(30);
            
            long now = System.currentTimeMillis();
            long waitMaximumTwoMinutes = now + 2 * 1000 * 60; 
            
            while (heartbeatSender.getLastActivityTime() < 1 && System.currentTimeMillis() < waitMaximumTwoMinutes)
            {
                org.excalibur.core.util.ThreadUtils.sleep(TimeUnit.SECONDS.toMillis(40));
            }
            
            shell.close();
            client.disconnect();
            reader.close();
            heartbeatSender.shutdown();
            
            LOG.debug("Executed the script to start excalibur. The output is [{}]", output);
        }
        catch (IOException e)
        {
            LOG.error(">Error on creating the script to start excalibur.", e.getMessage(), e);
        }
        
        LOG.debug("Is excalibur running? [{}]", heartbeatSender.getLastActivityTime() > 0);
        
        return heartbeatSender.getLastActivityTime() > 0;
    }
    
    protected class RemoteTaskExecutionHandler implements ApplicationExecutionHandler
    {
        private final RemoteTask task_;
        private final AtomicInteger inc_;

        protected RemoteTaskExecutionHandler(RemoteTask task, final AtomicInteger inc)
        {
            this.task_ = task;
            this.inc_ = inc;
        }

        @Override
        public void onStarted(ApplicationExecutionResult application)
        {
            LOG.info("Executing application [{}] on host [{}]", 
                    application.getApplication().getName(), 
                    application.getInstance().getConfiguration().getPublicIpAddress());
            try
            {
                applicationExecutionService_.insertStartExecution(application);
            }
            catch (Exception exception)
            {
                LOG.error("Error on inserting the execution of application [{}] on host [{}] with username. Error message: [[]]", 
                        application.getApplication().getName(),
                        task_.getHostAndPort().getHost(),
                        task_.getUsername(),
                        exception.getMessage(), exception);
            }
        }

        @Override
        public void onFinished(ApplicationExecutionResult result)
        {
            inc_.incrementAndGet();
            
            LOG.info("Finished the execution of application [{}] on host [{}] with exit code [{}]. The error message is: [{}] ",
                    result.getApplication().getName(), 
                    task_.getHostAndPort().getHost(), 
                    result.getExitCode(), 
                    result.getError());
            
            try
            {
                applicationExecutionService_.insertFinishedExecution(result);
            }
            
            catch (Exception exception)
            {
                LOG.error("Error on inserting the result of application [{}]. Error message: [[]]", 
                        result.getApplication().getName(),
                        exception.getMessage(), exception);
            }
        }
    }
}
