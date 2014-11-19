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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.utils.DeploymentUtils;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.util.AnyThrow;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.workflow.context.WorkflowContextImpl;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.flow.ActivityExecutionContext;
import org.excalibur.core.workflow.flow.Workflow;
import org.excalibur.core.workflow.flow.WorkflowBuilder;
import org.excalibur.core.workflow.flow.WorkflowContext;
import org.excalibur.core.workflow.flow.WorkflowExecutionStrategy;
import org.excalibur.core.workflow.flow.WorkflowExecutor;
import org.excalibur.core.workflow.repository.TaskRepository;
import org.excalibur.core.workflow.repository.WorkflowRepository;
import org.excalibur.service.deployment.service.DeploymentService;
import org.excalibur.service.deployment.service.WorkflowService;
import org.excalibur.service.manager.NodeManagerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import static com.google.common.base.Preconditions.*;

@Component("deploymentHandler")
public class DeploymentHandler
{
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DeploymentHandler.class.getName());
    
    @Autowired
    private DeploymentService deploymentService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowRepository workflowRepository_;

    @Autowired
    private TaskRepository taskRepository_;

    @Autowired
    private InstanceService instanceService_;

    @Autowired
    private RegionRepository regionRepository_;

    @Autowired
    private UserRepository userRepository_;

    @Resource(name = "newInstancesAmqpTemplate")
    private RabbitTemplate newInstanceTemplate_;

    @SuppressWarnings("unchecked")
    public void handle(Deployment deployment)
    {
        NodeManagerFactory.getManagerReference();
        
        try
        {

            WorkflowDescription workflowDescription = deploymentService.createWorkflowFor(deployment);
            String thisHostname = System.getProperty("org.excalibur.instance.hostname");

            VirtualMachine localNode = instanceService_.getInstanceByName(thisHostname);
            checkState(localNode != null);

            ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("event-bus-for-workflow-" + workflowDescription.getName() + "-%d")
                    .build();

            int numberOfActivities = workflowDescription.getNumberOfActivities();

            LOG.debug("workflow [{}] has [{}] activities [{}]", workflowDescription.getName(), numberOfActivities);

            ExecutorService eventBusExecutor = DynamicExecutors.newScalingThreadPool(1, numberOfActivities, 5, TimeUnit.MINUTES, threadFactory);
            EventBus bus = new AsyncEventBus(eventBusExecutor);

            final Workflow workflow = new WorkflowBuilder().description(workflowDescription).eventBus(bus).build();
            final WorkflowExecutionStrategy strategy = new WorkflowExecutionStrategy(workflow);

            WorkflowContext context = new WorkflowContextImpl(workflow, workflowRepository_, strategy);
            context.setWorkflowCoordinator(localNode);

            context.registerExecutors(eventBusExecutor);

            final WorkflowExecutor executor = new WorkflowExecutor(context, taskRepository_, userRepository_, regionRepository_, localNode);
            List<ActivityExecutionContext> executionContexts = executor.execute();

            for (ActivityExecutionContext executionContext : executionContexts)
            {
                for (TaskType<?> task : executionContext.getTasks())
                {
                    TaskResult<Instances> result = (TaskResult<Instances>) task.getResult();

                    if (result != null && TaskState.SUCCESS.equals(result.getTaskState()))
                    {
                        LOG.debug("Task [{}] executed successfully in [{}] ms",
                                TimeUnit.MILLISECONDS.toMillis(result.getFinishTime() - result.getStartTime()));

                        newInstanceTemplate_.convertAndSend(result.getResult());
                    }
                }
            }
            
            long elapsedTime = workflowDescription.getCreatedIn() != null ? System.currentTimeMillis() - workflowDescription.getCreatedIn().getTime() : 0;
            LOG.debug("Finished the workflow [{}] in [{}] seconds", workflowDescription.getName(), TimeUnit.MILLISECONDS.toSeconds(elapsedTime));
        }
        catch (Exception exception)
        {
            String text = DeploymentUtils.marshalQuietly(deployment);
            LOG.error("Error on executing the deployment [{}]. Error message [{}]", text, exception.getMessage(), exception);
            AnyThrow.throwUncheked(exception);
        }
    }
}
