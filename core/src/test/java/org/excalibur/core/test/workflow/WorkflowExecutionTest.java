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
package org.excalibur.core.test.workflow;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.domain.repository.InstanceRepository;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.test.TestSupport;
import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.workflow.context.WorkflowContextImpl;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.flow.Workflow;
import org.excalibur.core.workflow.flow.WorkflowBuilder;
import org.excalibur.core.workflow.flow.WorkflowContext;
import org.excalibur.core.workflow.flow.WorkflowExecutionStrategy;
import org.excalibur.core.workflow.flow.WorkflowExecutor;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;
import org.excalibur.core.workflow.repository.WorkflowRepository;
import org.junit.Test;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ch.vorburger.exec.ManagedProcessException;

import static org.junit.Assert.*;

public class WorkflowExecutionTest extends TestSupport
{
    private WorkflowRepository workflowRepository_;
    private WorkflowTaskRepository taskRepository_;
    private InstanceRepository instanceRepository_;
    private UserRepository userRepository_;
    private RegionRepository regionRepository_;
    private VirtualMachine localNode;

    @Override
    public void setup() throws IOException, ManagedProcessException
    {
        super.setup();
        workflowRepository_ = openRepository(WorkflowRepository.class);
        taskRepository_ = openRepository(WorkflowTaskRepository.class);
        instanceRepository_ = openRepository(InstanceRepository.class);
        userRepository_ = openRepository(UserRepository.class);
        regionRepository_ = openRepository(RegionRepository.class);
        
        localNode = new VirtualMachine();

        localNode.setConfiguration(new VmConfiguration()
                              .setKeyName("keytest")
                              .setPlatform("linux")
                              .setPlatformUserName("ubuntu")
                              .setPrivateIpAddress("127.0.0.1")
                              .setPublicDnsName("localhost")
                              .setPublicIpAddress("127.0.0.1"));
        localNode.setImageId("ami-832b72ea")
                .setLaunchTime(new Date())
                .setName("i-fd6125d3")
                .setType(InstanceType.valueOf("t1.micro").setId(120))
                .setOwner(user)
                .setLocation(zone);
        
        localNode.setId(instanceRepository_.insertInstance(localNode));
    }
    
    @Test
    public void must_execute_one_workflow() throws InterruptedException, JAXBException
    {
        TaskDescription task = new TaskDescription().setExecutable("who").setTypeClass(DummyTask.class.getName());

        WorkflowActivityDescription a = new WorkflowActivityDescription().setLabel("a").setId(1).addTask(task.clone().setId(1));
        WorkflowActivityDescription b = new WorkflowActivityDescription().setLabel("b").setParents("1").setId(2).addTask(task.clone().setId(1));
        WorkflowActivityDescription c = new WorkflowActivityDescription().setLabel("c").setParents("1").setId(3).addTask(task.clone().setId(1));
        WorkflowActivityDescription d = new WorkflowActivityDescription().setLabel("d").setParents("2").setId(4).addTask(task.clone().setId(1));
        WorkflowActivityDescription e = new WorkflowActivityDescription().setLabel("e").setParents("2,3").setId(5).addTask(task.clone().setId(1));
        WorkflowActivityDescription f = new WorkflowActivityDescription().setLabel("f").setParents("4,5").setId(6).addTask(task.clone().setId(1));

        b.addParent(a);
        c.addParent(a);
        d.addParent(b);

        e.addParents(b, c);
        f.addParents(d, e);

        WorkflowDescription workflowDescription = new WorkflowDescription().setName("wk-test").setStartActivityId(a.getId()).addActivities(a, b, c, d, e, f);
        
        workflowDescription.setId(workflowRepository_.insert(workflowDescription.setUser(user)));
        
        a.setId(workflowRepository_.insert(a)).setInternalId(a.getId());
        b.setId(workflowRepository_.insert(b)).setInternalId(b.getId());
        c.setId(workflowRepository_.insert(c)).setInternalId(c.getId());
        d.setId(workflowRepository_.insert(d)).setInternalId(d.getId());
        e.setId(workflowRepository_.insert(e)).setInternalId(e.getId());
        f.setId(workflowRepository_.insert(f)).setInternalId(f.getId());
        
        a.getTasks().get(0).setId(taskRepository_.insert(a.getTasks().get(0)));
        b.getTasks().get(0).setId(taskRepository_.insert(b.getTasks().get(0)));
        c.getTasks().get(0).setId(taskRepository_.insert(c.getTasks().get(0)));
        d.getTasks().get(0).setId(taskRepository_.insert(d.getTasks().get(0)));
        e.getTasks().get(0).setId(taskRepository_.insert(e.getTasks().get(0)));
        f.getTasks().get(0).setId(taskRepository_.insert(f.getTasks().get(0)));
        
        
        JAXBContextFactory<WorkflowDescription> factory = new JAXBContextFactory<WorkflowDescription>(WorkflowDescription.class);
        assertNotNull(factory.marshal(workflowDescription));

        ThreadFactory threadFactory2 = new ThreadFactoryBuilder()
                .setNameFormat("event-bus-for-workflow-" + workflowDescription.getName() + "-%d")
                .build();
        
        ExecutorService executorService = DynamicExecutors.newScalingThreadPool(6, 100, 3, TimeUnit.MINUTES, threadFactory2);
        EventBus bus = new AsyncEventBus(executorService);

        final Workflow workflow = new WorkflowBuilder().description(workflowDescription).eventBus(bus).build();
        final WorkflowExecutionStrategy strategy = new WorkflowExecutionStrategy(workflow);
        
        WorkflowContext context = new WorkflowContextImpl(null, workflow, workflowRepository_, strategy);
        context.setWorkflowCoordinator(localNode);
        context.registerExecutors(executorService);
        
        final WorkflowExecutor executor = new WorkflowExecutor(context, taskRepository_, userRepository_, regionRepository_, localNode);
        executor.execute();
        assertTrue(workflow.isFinished());
    }
}
