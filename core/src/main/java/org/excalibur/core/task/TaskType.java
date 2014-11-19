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
import java.util.List;

import org.excalibur.core.workflow.domain.TaskDataDescription;
import org.excalibur.core.workflow.domain.TaskDescription;

/**
 * An Executable Task. TaskTypes are only responsible for executing tasks.
 */
public interface TaskType<T extends Serializable>
{
    /**
     * 
     * @return
     */
    TaskDescription getDescription();
    
    /**
     * 
     * @return
     */
    List<TaskDataDescription> getData();

    /**
     * Executes the task.
     * 
     * @param taskContext
     *            The task to be executed.
     * @return A TaskResult representing the status of the task execution.
     */
    TaskResult<T> execute(TaskContext taskContext);
    
    /**
     * 
     * @return
     */
    TaskResult<T> getResult();
}
