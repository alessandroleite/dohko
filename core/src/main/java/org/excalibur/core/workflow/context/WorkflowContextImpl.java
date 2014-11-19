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
package org.excalibur.core.workflow.context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.executor.Context;
import org.excalibur.core.workflow.flow.Workflow;
import org.excalibur.core.workflow.flow.WorkflowContext;
import org.excalibur.core.workflow.flow.WorkflowExecutionStrategy;
import org.excalibur.core.workflow.repository.WorkflowRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class WorkflowContextImpl implements WorkflowContext
{
    private final Context parentContext;
    private final Workflow workflow_;
    private final WorkflowRepository workflowRepository_;
    private final List<ExecutorService> executors_ = new ArrayList<ExecutorService>();
    private final WorkflowExecutionStrategy workflowExecutionStrategy_;
    
    private volatile VirtualMachine coordinator_;

    public WorkflowContextImpl(Context parentContext, Workflow workflow, WorkflowRepository workflowRepository,
            WorkflowExecutionStrategy workflowExecutionStrategy)
    {
        this.workflow_ = checkNotNull(workflow);
        this.workflowRepository_ = checkNotNull(workflowRepository);
        this.parentContext = parentContext;
        this.workflowExecutionStrategy_ = checkNotNull(workflowExecutionStrategy);
    }

    public WorkflowContextImpl(Workflow workflow, WorkflowRepository workflowRepository, WorkflowExecutionStrategy workflowExecutionStrategy)
    {
        this(null, workflow, workflowRepository, workflowExecutionStrategy);
    }

    @Override
    public Context getParentContext()
    {
        return parentContext;
    }

    @Override
    public Workflow getWorkflow()
    {
        return workflow_;
    }

    @Override
    public WorkflowRepository getWorkflowRepository()
    {
        return workflowRepository_;
    }

    @Override
    public void setWorkflowCoordinator(VirtualMachine coordinator)
    {
        this.coordinator_ = coordinator;
    }

    @Override
    public VirtualMachine getWorkflowCoordinator()
    {
        return coordinator_;
    }

    @Override
    public WorkflowExecutionStrategy getWorkflowExecutionStrategy()
    {
        return workflowExecutionStrategy_;
    }

    @Override
    public ExecutorService[] getRegisteredWorkflowExecutors()
    {
        return this.executors_.toArray(new ExecutorService[this.executors_.size()]);
    }

    @Override
    public void registerExecutors(ExecutorService... executorServices)
    {
        for (ExecutorService executor : executorServices)
        {
            if (executor != null)
            {
                this.executors_.add(executor);
            }
        }
    }
}
