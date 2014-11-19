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
package org.excalibur.core.workflow.flow;

import java.util.concurrent.ExecutorService;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.executor.Context;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.repository.WorkflowRepository;

public interface WorkflowContext extends Context
{
    /**
     * Returns the reference for the {@link WorkflowDescription} in execution.
     * 
     * @return The reference to the workflow in execution. It's never <code>null</code>.
     */
    Workflow getWorkflow();

    /**
     * Returns the {@link WorkflowRepository} associated to this context.
     * 
     * @return The repository of this context.
     */
    WorkflowRepository getWorkflowRepository();

    /**
     * Assigns the node that is responsible for the execution of the workflow.
     * 
     * @param owner
     *            The node to coordinate the execution of the workflow. Might not be <code>null</code>.
     */
    void setWorkflowCoordinator(VirtualMachine owner);

    /**
     * Returns the reference to the responsible to coordinate the execution of the workflow ({@link #getWorkflow()}) of this context. It might not be
     * <code>null</code>, and a workflow cannot be executed without one.
     * 
     * @return The reference to the node responsible for executing the {@link Workflow}.
     */
    VirtualMachine getWorkflowCoordinator();

    /**
     * Returns the execution policy of the {@link Workflow}.
     * 
     * @return The execution policy to execute the workflow.
     */
    WorkflowExecutionStrategy getWorkflowExecutionStrategy();

    /**
     * 
     * @return
     */
    ExecutorService[] getRegisteredWorkflowExecutors();

    void registerExecutors(ExecutorService... executorServices);
}
