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
package org.excalibur.core.task;

import java.io.Serializable;

import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.executor.task.ExecutionContext;
import org.excalibur.core.io.handlers.IOHandler;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;

/**
 * Encapsulates the environment and configuration of a task running in a building environment.
 */
public interface TaskContext extends CommonTaskContext
{
    /**
     * Returns the handler responsible for handle the input and output stream of the task.
     * 
     * @return the handler responsible for handle the input and output stream of the task.
     */
    IOHandler getIOHandler();

    /**
     * Returns the information about the execution of the tasks.
     * 
     * @return The information about the task's execution environment.
     */
    ExecutionContext getExecutionContext();

    /**
     * Returns the task associated with this context.
     * 
     * @return The task of this context. It must not be <code>null</code>.
     */
    <T extends Serializable> TaskType<T> getTask();

    /**
     * Returns a reference to the {@link WorkflowTaskRepository}.
     * 
     * @return a reference to the {@link WorkflowTaskRepository}. It might not be <code>null</code>.
     */
    WorkflowTaskRepository getTaskRepository();
    
    /**
     * 
     * @return
     */
    UserRepository getUserRepository();
    
    /**
     * 
     * @return
     */
    RegionRepository getRegionRepository();

}
