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

import org.excalibur.core.ID;
import org.excalibur.core.task.impl.TaskResultBuilder;

/**
 * Represents the result of a {@link TaskType} execution. Please use TaskResultBuilder
 */
public interface TaskResult<T extends Serializable>
{
    /**
     * @return the result from the {@link TaskType} execution.
     */
    java.util.Map<ID, Serializable> getResultData();

    /**
     * Returns the result of a {@link TaskType} execution.
     * 
     * @return
     */
    T getResult();

    /**
     * Returns some messages of the scheduler such as failure reason, etc.
     * 
     * @return
     */
    String getMessage();

    /**
     * Returns {@link TaskState} of the {@link TaskType} execution.
     * 
     * @return {@link TaskState} of the {@link TaskType} execution.
     */
    TaskState getTaskState();

    TaskResultBuilder<T> toBuilder();

    long getStartTime();

    long getFinishTime();
}
