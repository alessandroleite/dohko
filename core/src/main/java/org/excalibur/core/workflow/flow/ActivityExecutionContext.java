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

import java.util.List;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.executor.Context;
import org.excalibur.core.executor.task.TaskExecutionService;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.repository.TaskRepository;

/**
 * Context object passed to an {@link Activity} implementation.
 */
public interface ActivityExecutionContext extends Context
{
    /**
     * Returns the {@link org.excalibur.core.workflow.definition.Activity} which this context belongs to. Must not be <code>null</code>.
     * 
     * @return The activity which this context belongs to.
     */
    Activity getActivity();

    /**
     * The node where the associated {@link Activity} is running.
     * 
     * @return The node responsible for the execution of the associate activity. Must not be <code>null</code>.
     */
    VirtualMachine getLocation();

    /**
     * The tasks of the associated activity to execute. Must not be <code>null</code>.
     * 
     * @return An unmodified {@link List} with the tasks of the associated activity.
     */
    List<TaskType<?>> getTasks();
    
    
    /**
     * 
     * @return
     */
    TaskRepository getTaskRepository();
    
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
    
    /**
     * @return
     */
    TaskExecutionService getTaskExecutionService();
}
