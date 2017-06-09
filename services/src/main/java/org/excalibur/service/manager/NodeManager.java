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


import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.excalibur.aqmp.handler.QueueStatsProcessor;
import org.excalibur.core.cloud.api.Cloud;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypeReq;
import org.excalibur.core.cloud.api.InstanceTypes;
import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.cloud.service.xmpp.JID;
import org.excalibur.core.cloud.service.xmpp.PresenceType;
import org.excalibur.core.cloud.service.xmpp.events.PresenceEvent;
import org.excalibur.core.cloud.service.xmpp.listeners.PresenceListener;
import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.repository.InstanceTypeRepository;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.services.UserService;
import org.excalibur.core.util.DateUtils2;
import org.excalibur.core.util.Lists2;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.discovery.service.DiscoveryService;
import org.excalibur.discovery.ws.ext.ObjectMapperProvider;
import org.excalibur.jackson.databind.JsonYamlObjectMapper;
import org.excalibur.service.application.JobService;
import org.excalibur.service.application.resource.ApplicationExecutionRequest;
import org.excalibur.service.application.resource.ApplicationExecutionResult;
import org.excalibur.service.compute.executor.Worker;
import org.excalibur.service.deployment.service.DeploymentService;
import org.excalibur.service.xmpp.service.XmppService;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import static java.lang.Runtime.*;
import static java.lang.System.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Maps.*;
import static java.util.concurrent.TimeUnit.*;
import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.*;
import static org.excalibur.core.execution.domain.TaskStatus.*;
import static org.excalibur.core.io.utils.IOUtils2.*;
import static org.excalibur.core.io.utils.ZipUtil.*;

@SuppressWarnings("unused")
public class NodeManager implements Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger(NodeManager.class.getName());
    
    private final Configuration configuration_;
    
    private final VirtualMachine thisNode_;
    
    private final JID jid_;
    
    private final ComputeService computeService_;
    
    private final ExecutorService dispatcher_;
    
    private final ExecutorService taskDispatcher_;
    
    @Autowired
    private XmppService xmppService;
    
	@Autowired
    private DiscoveryService discoveryService_;
    
    @Autowired
    private QueueStatsProcessor queueStatsProcessor_;
    
    @Autowired
    private InstanceTypeRepository instanceTypeRepository_;
    
    @Autowired
    private DeploymentService deploymentService_;
    
    @Autowired
    private InstanceService instanceService_;
    
    @Autowired
    private UserService userService_;
    
    @Autowired
    private JobService jobService_;
    
    @Autowired
    private RegionRepository regionRepository_;
    
    @Resource(name = "applicationExecutionAmqpTemplate")
    private RabbitTemplate applicationTemplate_;
    
    private final BlockingQueue<VirtualMachine> idleInstances_ = new BlockingArrayQueue<VirtualMachine>();
    
    private final ConcurrentMap<String, VirtualMachine> busyInstances_ = newConcurrentMap();
    
    private final BlockingQueue<Application> waitingApplications_ =  new BlockingArrayQueue<Application>();
    
    private final ConcurrentMap<String, Application> submittedApplications_ =  newConcurrentMap();
    
    private final JsonYamlObjectMapper YAML_MAPPER = new JsonYamlObjectMapper();
    
    public NodeManager (Configuration configuration, VirtualMachine node, ComputeService computeService)
    {
        this.configuration_ = checkNotNull(configuration);
        this.thisNode_ = checkNotNull(node, "node must node be null!");
        this.jid_ = new JID(String.format("%s@%s", thisNode_.getName(), getProperty("org.excalibur.xmpp.server.domain")));
        this.computeService_ = checkNotNull(computeService);
        
        dispatcher_ = DynamicExecutors.newListeningDynamicScalingThreadPool(this.thisNode_.getName(), getRuntime().availableProcessors());
        this.taskDispatcher_ = DynamicExecutors.newListeningDynamicScalingThreadPool("task-" + this.thisNode_.getName() + "-executor", 
                getRuntime().availableProcessors());
        
        dispatcher_.submit(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                while (true)
                {
                    final Application application = waitingApplications_.take();
                    final VirtualMachine worker = idleInstances_.take();
                    final User owner = userService_.findUserByUsername(worker.getOwner().getUsername());
                    
                    Future<Response> future = taskDispatcher_.submit(new Callable<Response>()
                    {
                        @Override
                        public Response call() throws Exception
                        {
                            Client client = ClientBuilder.newClient().register(ObjectMapperProvider.class).register(JacksonFeature.class);
                            LOG.info("Sending a task to node: [{}], address:[{}:8080/application]",
                                    worker.getName(),
                                    worker.getConfiguration().getPublicIpAddress());
                            
                            WebTarget target = client.target(String.format("http://%s:%s/application", 
                                    worker.getConfiguration().getPublicIpAddress(),
                                    8080));
                            

                            ApplicationExecutionRequest request = new ApplicationExecutionRequest()
                                     .setApplication(application)
                                     .setId(UUID.randomUUID().toString());
                            
                            UserKey key = owner.getKey(worker.getConfiguration().getKeyName());
                            
                            checkState(key != null, String.format("User [{}] does not have the key [{}]", 
                                    owner.getUsername(), worker.getConfiguration().getKeyName()));

                            request.setKeyPairs(
                                    new KeyPairs().setPrivateKey(new KeyPair().setKeyName(key.getName()).setKeyMaterial(key.getPrivateKeyMaterial()))
                                                  .setPublicKey(new KeyPair().setKeyName(key.getName()).setKeyMaterial(key.getPublicKeyMaterial())))
                                   .setOwner(owner)
                                   .setManager(thisNode_)
                                   .setWorker(worker)
                                   .setJobId(application.getJob().getId());
                            
                            Response response = target.request(APPLICATION_XML_TYPE).post(Entity.entity(request, APPLICATION_XML_TYPE));
                            return response;
                        }
                    });
                    
                    Futures.addCallback((ListenableFuture<Response>)future, new FutureCallback<Response>()
                    {
                        @Override
                        public void onSuccess(Response result)
                        {
                            if (result.getStatus() == ACCEPTED.getStatusCode())
                            {
                                LOG.info("Task [{}] sent to node: [{}], address:[{}:8080/application]. [{}]",
                                        application.getId(),
                                        worker.getName(),
                                        worker.getConfiguration().getPublicIpAddress(),
                                        application.getExecutableCommandLine());
                                
                                busyInstances_.putIfAbsent(worker.getName(), worker);
                                submittedApplications_.putIfAbsent(application.getId(), application);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t)
                        {
                            submittedApplications_.remove(application.getId());
                            busyInstances_.remove(worker.getName());
                            
                            VirtualMachine vm = instanceService_.getInstanceByName(worker.getName());
                            
                            if (vm != null && InstanceStateType.RUNNING.equals(vm.getState().getState()))
                            {
                                addIdleInstance(worker);
                            }
                            
                            waitingApplications_.offer(application);
                            LOG.error(t.getMessage(), t.getCause());
                            
                            LOG.error("Error on executing the task: [{}] on node [{}]. Error message: [{}]", 
                                    application.getId(), worker.getName(), t.getMessage(), t);
                            
//                            addIdleInstance(worker);
                        }
                    });
                }
            }
        });
    }
    
    public void start() throws JsonParseException, JsonMappingException, IOException
    {
        loadPendingJobs();
        loadNodes();

        xmppService.registerAccount(jid_).registerListener(jid_, new PresenceListener()
        {
            @Override
            public void onPresenceChanged(PresenceEvent event)
            {
                LOG.debug("Received a presence event from [{}] status [{}]", event.getValue().getFromJid(), event.getType());
                
                JID from = event.getValue().getFromJid();
                
                if (jid_.getName().equals(from.getName()))
                {
                    return;
                }
                
                if (PresenceType.UNAVAILABLE.equals(event.getType()))
                {
                    VirtualMachine instance = busyInstances_.remove(from.getName());

                    if (instance == null)
                    {
                        instance = new VirtualMachine().setName(from.getName());
                    }

                    idleInstances_.remove(instance);
                }
            }
        });
    }

    private void loadPendingJobs() throws JsonParseException, JsonMappingException, IOException
    {
        for (ApplicationDescriptor job : this.jobService_.listPendentJobs())
        {
            for (Application task : job.getApplications())
            {
                if (TaskStatus.PENDING.equals(task.getStatus()))
                {
                    this.waitingApplications_.add(task);
                }
            }
        }
    }
    

    private void loadNodes()
    {
        Instances instances = this.instanceService_.listRunningInstances(this.configuration_.getUser());
        
        for(VirtualMachine instance: instances)
        {
            this.addIdleInstance(instance);
        }
    }

    public void finished(ApplicationExecutionResult result, VirtualMachine worker)
    {
        LOG.info("Received the result of task: [{}] executed during [{} seconds] from request: [{}]", 
                result.getApplication().getId(),
                NANOSECONDS.toSeconds(result.getElapsedTime()), 
                result.getId());

        try
        {
            Application task = this.submittedApplications_.remove(result.getApplication().getId());
            final TaskStatus status = result.getExitValue() == 0 ? FINISHED : FAILED;

            if (task == null)
            {
                task = this.jobService_.findApplicationByUUID(result.getApplication().getId());

                if (task != null && FINISHED.equals(task.getStatus()))
                {
                    task = null;
                }
            }

            if (task != null)
            {
                this.jobService_.update
                (
                        task.setStatus(status), 
                        worker, 
                        result.getExitValue(), 
                        task.getId(), 
                        result.getElapsedTime(), 
                        new String(uncompress(Base64.decodeBase64(result.getOutput().getBytes())))
                );
            }
        }
        finally
        {
            VirtualMachine idle = this.busyInstances_.remove(worker.getName());
            this.addIdleInstance(idle);

            if (submittedApplications_.isEmpty() && this.waitingApplications_.isEmpty())
            {
                ApplicationDescriptor finishedJob = this.jobService_.finishJob(result.getJobId(), System.currentTimeMillis());
                long elapsedTime = finishedJob != null ? finishedJob.getFinishedIn() - finishedJob.getCreatedIn() : 0;
                
                LOG.info("Job [{}] finished in [{}] seconds", result.getJobId(), DateUtils2.seconds(elapsedTime));
                
                FinishedJobProcessing postJob = new FinishedJobProcessing
                (
                        this.jobService_.findJobByUUID(result.getJobId()), 
                        this.instanceService_.getRunningInstancesWithTagFromUser
                        (
                                Tag.valueOf("app-deployment-id", result.getJobId()), configuration_.getUser()
                        ),
                        configuration_.getCredentials(),
                        this.userService_
                );
                
                this.dispatcher_.submit(postJob);
            }
        }
    }

    public void provision(ApplicationDescriptor descriptor) throws JsonProcessingException
    {
        checkNotNull(descriptor);
        
        User user = this.userService_.findUserByUsername(descriptor.getUser().getUsername());
        
        if (user == null)
        {
            if (descriptor.getUser() == null || isNullOrEmpty(descriptor.getUser().getUsername()))
            {
                throw new IllegalArgumentException("A user is required");
            }
            
            user = descriptor.getUser();
            this.userService_.insertUser(user);
        }else
        {
            descriptor.setUser(user);
        }
        
        String group = String.format("%s-%s", configuration_.getUser().getUsername().replaceAll("\\W", ""), 
                descriptor.getName()).replaceAll("\\W", "");
        
        LOG.info("Received the job [{}] with [{}] application(s) to execute on [{}] cloud(s)", 
                descriptor.getId(), 
                descriptor.getApplications().size(), 
                descriptor.getClouds().size());
        
        for (Cloud cloud: descriptor.getClouds())
        {
            Deployment deployment = new Deployment()
                    .setDescription(descriptor.getDescription())
                    .setUser(configuration_.getUser())
                    .setUsername(configuration_.getUser().getUsername())
                    .withCredential(new Credential().setName(String.format("%s-%s-%s", 
                            descriptor.getUser().getUsername(), 
                            descriptor.getName(),
                            descriptor.getId())));
            
            int i = 1;
            
            for (final Region region : cloud.getRegions())
            {
                Region region_ = regionRepository_.findByName(region.getName());
                checkArgument(region_ != null, "Invalid region [%s]", region.getName());
                
                region_.addZones(regionRepository_.listZoneOfRegion(region_.getId()));
                BeanUtils.copyProperties(region_, region);
                region.addZones(region_.getZones());
                
                Zone zone = Lists2.first(region_.getZones());
                checkState(zone != null, "Invalid system's state. Region %s does not have one zone (data center).", region_.getName());
                
                for (InstanceTypeReq type : this.getInstanceReqTypes(cloud))
                {
                    VirtualMachineImage image = instanceService_.listAvailableImagesForInstanceType(type.getName(), region_.getId()).get(0);
                    
                    Node node = new Node()
                            .setCount(type.getNumberOfInstances())
                            .setGroup(type.getInstanceType().getSupportPlacementGroup().toBoolean() ? group : null)
                            .setName(String.format("%s-%s", descriptor.getId(), i++))
                            .setProvider
                            (
                                    new Provider().setImageId(image.getName())
                                    .setInstanceType(type.getName())
                                    .setName(cloud.getProvider().getName())
                            )
                            .setRegion(region_.getName())
                            .setZone(zone.getName())
                            .addTags(new Tag().setName("app-deployment-id").setValue(descriptor.getId()))
                            .addTags(new Tag().setName("manager").setValue(this.thisNode_.getName()));
                    
                    deployment.withNode(node);
                }
            }
            deploymentService_.create(deployment);
        }
        
        descriptor.setPlainText(YAML_MAPPER.writeValueAsString(descriptor));
        descriptor.setCreatedIn(System.currentTimeMillis());
        
        this.jobService_.insertJob(descriptor);
        
        for (Application task: descriptor.getApplications())
        {
            this.waitingApplications_.offer(task);
        }
    }
    
    protected InstanceTypes getInstanceTypes(Cloud cloud)
    {
        checkState(!cloud.getInstanceTypes().isEmpty());
        InstanceTypes instanceTypes = InstanceTypes.newInstanceTypes();
        
        for (InstanceTypeReq req: cloud.getInstanceTypes())
        {
            InstanceType type = this.instanceService_.findInstanceTypeByName(req.getName());
            
           for (int i = 0; i < req.getNumberOfInstances(); i++)
           {
               instanceTypes.add(type);
           }
        }
        return instanceTypes;
    }
    
    private InstanceTypeReq[] getInstanceReqTypes(Cloud cloud)
    {
        java.util.List<InstanceTypeReq> instanceTypes = new ArrayList<InstanceTypeReq>();
        
        for (InstanceTypeReq req: cloud.getInstanceTypes())
        {
            InstanceType type = this.instanceService_.findInstanceTypeByName(req.getName());
            instanceTypes.add(req.setInstanceType(type));
        }
        
        return instanceTypes.toArray(new InstanceTypeReq[instanceTypes.size()]);
    }
    
    public NodeManager addIdleInstance(VirtualMachine instance)
    {
        // if (!this.thisNode_.equals(instance))
        // {
        checkNotNull(instance);
        checkState(!isNullOrEmpty(instance.getName()));

        LOG.debug("Registering the node [{}] of provider/location [{}/{}]", 
                instance.getName(), 
                instance.getType().getProvider().getName(),
                instance.getLocation());

        if (this.idleInstances_.offer(instance))
        {
            LOG.debug("Registered the node [{}] of provider/location [{}/{}]", 
                    instance.getName(), 
                    instance.getType().getProvider().getName(),
                    instance.getLocation());
        }
        
        // }
        return this;
    }

    public void execute(final ApplicationExecutionRequest request) throws ExecuteException, IOException
    {
        final long start = nanoTime();
        final long timeS = currentTimeMillis();
        
        request.setOwner(this.userService_.createIfDoesNotExist(request.getOwner(), request.getKeyPairs()));
        
        new Worker().execute(request.getApplication(), new ExecuteResultHandler()
        {
            @Override
            public void onProcessFailed(ExecuteException e)
            {
                onComplete(e, e.getExitValue());
            }

            @Override
            public void onProcessComplete(int exitValue)
            {
                onComplete(null, exitValue);
            }
            
            private void onComplete(ExecuteException failure, int exitValue)
            {
                final long elapsedTime = nanoTime() - start;
                final long elapsedTimeM = currentTimeMillis() - timeS;
                String failureReason = Strings.nullToEmpty(failure != null ? failure.getMessage() : null);
                
                LOG.info("Completed the execution of the task: [{}] in [{}/{} seconds] of job: [{}], manager: [{}], " +
                         " with exitValue: [{}] from requestId: [{}], failure reason: [{}]", 
                         request.getApplication().getId(), 
                         MILLISECONDS.toSeconds(elapsedTimeM),
                         NANOSECONDS.toSeconds(elapsedTime), 
                         request.getJobId(), 
                         request.getManager().getName(), 
                         exitValue, 
                         request.getId(),
                         failureReason);
                
                Client client = ClientBuilder.newClient().register(ObjectMapperProvider.class).register(JacksonFeature.class);
                WebTarget target = client.target(String.format("http://%s:%s/application",
                        request.getManager().getConfiguration().getPublicIpAddress(), 8080));
                
                String output = new String(Base64.encodeBase64(compress(readLinesQuietly(request.getApplication().getOuputFile()))));
                
                ApplicationExecutionResult result = new ApplicationExecutionResult();
                result.setApplication(request.getApplication())
                      .setId(request.getId())
                      .setJobId(request.getJobId())
                      .setUser(request.getOwner())
                      .setElapsedTime(elapsedTime)
                      .setElapsedTimeMillis(elapsedTimeM)
                      .setFailureReason(failureReason)
                      .setReplyId(request.getId())
                      .setExitValue(exitValue)
                      .setWorker(request.getWorker())
                      .setOutput(output);
                
                target.path("reply").request(APPLICATION_XML_TYPE).post(Entity.entity(result, APPLICATION_XML_TYPE));
            }
        });
    }

    public VirtualMachine getThisNodeReference()
    {
        return this.thisNode_;
    }
    
    public void purgeQueue(String queueName)
    {
        this.queueStatsProcessor_.purgeQueue(queueName);
    }

    @Override
    public void close() throws IOException
    {
        if (computeService_ != null)
        {
            computeService_.close();
        }
    }
}
