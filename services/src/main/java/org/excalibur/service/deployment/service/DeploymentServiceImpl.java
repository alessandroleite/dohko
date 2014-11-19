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
package org.excalibur.service.deployment.service;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.excalibur.core.deployment.utils.DeploymentUtils.DEFAULT_DEPLOYMENT_TASK_TYPE;
import static org.excalibur.core.deployment.utils.DeploymentUtils.marshalQuietly;
import static org.excalibur.core.deployment.utils.DeploymentUtils.unmarshal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Dependency;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.DeploymentStatus;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.deployment.domain.repository.DeploymentRepository;
import org.excalibur.core.deployment.domain.task.DummyTask;
import org.excalibur.core.deployment.validation.DeploymentValidator;
import org.excalibur.core.deployment.validation.InvalidDeploymentException;
import org.excalibur.core.deployment.validation.ValidationContext;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserKey;
import org.excalibur.core.domain.UserNotFoundException;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.VirtualMachineImageRepository;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.services.UserService;
import org.excalibur.core.util.concurrent.Futures2;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.service.deployment.resource.DeploymentStatusDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeploymentServiceImpl implements DeploymentService
{
    private static final Logger  LOG = LoggerFactory.getLogger(DeploymentServiceImpl.class.getName());
    
    @Autowired
    private WorkflowService               workflowService_;
    
    @Autowired
    private UserService                   userService;
    
    @Autowired
    private DeploymentRepository          deploymentRepository_;
    
    @Autowired
    private InstanceService               instanceService_;
    
    @Autowired
    private VirtualMachineImageRepository virtualMachineImageRepository;
    
    @Autowired
    private RegionRepository              regionRepository_;
    
    @Resource(name="deploymentAmqpTemplate")
    private RabbitTemplate                deploymentTemplate_;
    
    
    @Override
    public void deploy(@Nonnull Iterable<InstanceType> types, @Nonnull User user)
    {
        Deployment deployment = new Deployment().withUsername(user.getUsername());
        
        String[] uuidParts = UUID.randomUUID().toString().split("-");
        
        int i = 0;
        
        for (InstanceType type: types)
        {
            VirtualMachineImage image = instanceService_.listAvailableImagesForInstanceType(type.getName(), type.getRegion().getId()).get(0);
            
            List<Zone> zones = regionRepository_.listZoneOfRegion(type.getRegion().getId());
            
            Node node = new Node()
                    .setCount(1)
                    .setCredential(new Credential().setName(user.getUsername()))
                    .setProvider(new Provider().setImageId(image.getName()).setInstanceType(type.getName()).setName(type.getProvider().getName()))
                    .setRegion(type.getRegion().getName())
                    .setZone(zones.isEmpty() ? null : zones.get(0).getName())
                    .setName(String.format("%s-%s-%s",type.getName().replaceAll("\\.", "-"), uuidParts[uuidParts.length -1], i++));
            
            deployment.withNode(node);
        }
        
        this.create(deployment);
    }
    
    @Override
    public Deployment findDeployment(final String username, final Integer deploymentId)
    {                     
        checkState(username != null && deploymentId != null, "Username and deploymentId must not be null!");
        
        final Deployment deployment = deploymentRepository_.findDeploymentById(deploymentId, username);

        if (deployment != null)
        {
            try
            {
                Deployment fromXml = unmarshal(deployment.getAsText());

                for (Node node : fromXml.getNodes())
                {
                    deployment.withNode(node);
                }

                deployment.withCredentials(fromXml.getCredentials().toArray(new Credential[fromXml.getCredentials().size()]));
            }
            catch (JAXBException exception)
            {
                LOG.error("Error in parsing deployment data. Cause: {}", exception.getMessage(), exception);
            }
        }
        return deployment;
    }
    
    @Override
    public void create(@Nonnull Deployment deployment)
    {
        deployment.setUuid(UUID.randomUUID().toString());
        
        Map<String, List<Node>> nodesByRegion = newHashMap();
        
        if (deployment != null)
        {
            ValidationContext validationContext = new ValidationContext();
            
            for (Node node: deployment.getNodes())
            {
                VirtualMachineImage image = virtualMachineImageRepository.findByExactNameOnRegion(node.getProvider().getImageId(), node.getRegion());
                
                if (image == null)
                {
                    validationContext.addError(String.format("Image [%s] does not exist on region [%s]", node.getProvider().getImageId(), node.getRegion()));
                }
                
                if (!nodesByRegion.containsKey(node.getRegion()))
                {
                    nodesByRegion.put(node.getRegion(), new ArrayList<Node>()); 
                }
                
                nodesByRegion.get(node.getRegion()).add(node);
            }
            
            if (validationContext.hasError())
            {
                throw new InvalidDeploymentException(deployment, validationContext);
            }
            
            Credential [] credentials = deployment.getCredentials().toArray(new Credential[deployment.getCredentials().size()]);
            
            if (nodesByRegion.size() > 1)
            {
                for (String region: nodesByRegion.keySet())
                {
                    Deployment dr = new Deployment()
                            .setUsername(deployment.getUsername())
                            .setDescription(deployment.getDescription())
                            .withCredentials(credentials)
                            .setNodes(nodesByRegion.get(region))
                            .setUuid(UUID.randomUUID().toString());
                    
                    String xml = marshalQuietly(deployment);
                    
                    if (xml != null)
                    {
                        this.deploymentTemplate_.convertAndSend(dr);
                    }
                }
            }
            else
            {
                String xml = marshalQuietly(deployment);
                
                if (xml != null)
                {
                    this.deploymentTemplate_.convertAndSend(deployment);
                }
                
            }
        }
    }
    
    @Override
    public Integer createDeployment(Deployment deployment)
    {
        createWorkflowFor(deployment);
        return deployment.getId();
    }

    @Override
    public DeploymentStatusDetails getDeploymentStatus(String username, Integer deploymentId)
    {
        Deployment deployment = deploymentRepository_.findDeploymentById(deploymentId, username);

        DeploymentStatusDetails status = new DeploymentStatusDetails().withDeplomentId(deploymentId).withUser(username);

        if (deployment == null)
        {
            status.withDeplomentStatus(DeploymentStatus.UNKNOWN).withDate(new Date()).withUser(username);
        }
        else
        {
            status.withDate(deployment.getStatusTime()).withDeplomentStatus(deployment.getStatus());
        }

        return status;
    }

    private WorkflowActivityDescription createWorkflowActivity(WorkflowActivityDescription startActivity, Node node, Map<String, Node> nodes,
            WorkflowDescription workflow, Map<String, WorkflowActivityDescription> activities,
            Map<Integer, WorkflowActivityDescription> activitiesWithtoutDependencies, Sequence increment)
    {
        WorkflowActivityDescription activity = activities.get(node.getId());

        if (activity == null)
        {
            StringBuilder parents = new StringBuilder(startActivity.getId().toString()).append(',');

            for (Dependency dependency : node.getDependencies())
            {
                WorkflowActivityDescription parentActivity = activities.get(dependency.getNode());
                
                if (parentActivity == null)
                {
                    parentActivity = createWorkflowActivity(startActivity, nodes.get(dependency.getNode()), nodes, workflow, activities,
                            activitiesWithtoutDependencies, increment);
                }

                parents.append(parentActivity.getId()).append(',');
            }

            String nodeDescriptionAsXml = marshalQuietly(node);
            
            activity = new WorkflowActivityDescription()
                    .setId(increment.incrementAndGet())
                    .setLabel(node.getName().trim())
                    .setType(DEFAULT_DEPLOYMENT_TASK_TYPE)
                    .setWorkflow(workflow)
                    .addTask(new TaskDescription()
                                 .setTypeClass(DEFAULT_DEPLOYMENT_TASK_TYPE)
                                 .setExecutable(nodeDescriptionAsXml));

            activities.put(activity.getLabel(), activity);

//            if (!node.hasDependencies())
//            {
//                activitiesWithtoutDependencies.put(activity.getId(), activity);
//            }

            activity.setParents(parents.length() > 0 ? parents.substring(0, parents.length() - 1) : null);
            workflow.addActivity(activity);
        }

        return activity;
    }

    private class Sequence
    {
        private final AtomicInteger value_ = new AtomicInteger(0);

        public Integer incrementAndGet()
        {
            return this.value_.incrementAndGet();
        }
    }

    @Override
    public WorkflowDescription createWorkflowFor(Deployment deployment)
    {
        checkNotNull(deployment, "Deployment must not be null!");
        
        ValidationContext validationResult = new DeploymentValidator().validate(deployment).get();
        Integer deploymentId = null;
        WorkflowDescription workflow = null;

        if (validationResult.hasError() || validationResult.isCyclic())
        {
            throw new InvalidDeploymentException(deployment, validationResult);
        }
        else
        {
//            this.workflowService_.findWorkflowByUUID(deployment.getUuid());
            
            final User user = this.userService.findUserByUsername(deployment.getUsername());

            if (user == null)
            {
                throw new UserNotFoundException(deployment.getUsername(), String.format("User [%s] does not exist.", deployment.getUsername()));
            }
            
            createUserSshKeysFromCredentials(user, deployment);

            final String deploymentAsXml = marshalQuietly(deployment);
            checkState(!isNullOrEmpty(deploymentAsXml));
            
            deployment.withUser(user)
                      .withStatus(DeploymentStatus.SUBMITTED)
                      .withStatusTime(new Date())
                      .withText(deploymentAsXml);

            deploymentId = this.deploymentRepository_.insert(deployment);
            
            workflow = new WorkflowDescription()
                    .setCreatedIn(new Date())
                    .setName(String.format("deployment-%d-%s", deploymentId, user.getUsername()))
                    .setUser(user);
            
            Sequence incrementer = new Sequence();
            
            WorkflowActivityDescription startActivity = new WorkflowActivityDescription()
                                           .setId(incrementer.incrementAndGet())
                                           .setLabel("start-activity")
                                           .setType(DummyTask.class.getName());
            
            workflow.addActivity(startActivity);
            workflow.setStartActivityId(startActivity.getId());

            Map<String, Node> nodes = deployment.getNodesMap();
            Map<String, WorkflowActivityDescription> mappingActivityLabel = newHashMap();
            Map<Integer, WorkflowActivityDescription> activitiesWithtoutDependencies = newHashMap();


            for (Node node : nodes.values())
            {
                createWorkflowActivity(startActivity, node, nodes, workflow, mappingActivityLabel, activitiesWithtoutDependencies, incrementer);
            }
            
            checkState(!mappingActivityLabel.isEmpty());
            
            WorkflowActivityDescription finishActivity = new WorkflowActivityDescription()
                                             .setId(incrementer.incrementAndGet())
                                             .setLabel("finish-activity")
                                             .setType(DummyTask.class.getName());

            StringBuilder sb = new StringBuilder(startActivity.getId().toString());
            
            for (WorkflowActivityDescription activity: mappingActivityLabel.values())
            {
                sb.append(',').append(activity.getId());
            }
            
            finishActivity.setParents(sb.toString());
            workflow.addActivity(finishActivity);
            
            workflowService_.insert(workflow);
            deployment.setWorkflow(workflow);
            
            this.deploymentRepository_.update(deployment);
        }
        
        return workflow;
    }

    private  List<UserKey> createUserSshKeysFromCredentials(final User user, final Deployment deployment)
    {
        Map<String, Credential> credentials = newHashMap(deployment.getCredentialMaps());
        
        for (Node node : deployment.getNodes())
        {
            Credential credential = node.getCredential();
            
            if (credential != null && !isNullOrEmpty(credential.getName()))
            {
                if (!credentials.containsKey(credential.getName()))
                {
                    credentials.put(credential.getName(), credential);
                }
            }
        }
        
        List<Callable<UserKey>> tasks = newArrayList();
        for (final Credential credential: credentials.values())
        {
            tasks.add(new Callable<UserKey>()
            {
                @Override
                public UserKey call() throws Exception
                {
                    return userService.generateKeyForUserIfItDoesNotExist(user, credential.getName());
                }
            });
        }
        
        return Futures2.invokeAll(tasks);
    }
}
