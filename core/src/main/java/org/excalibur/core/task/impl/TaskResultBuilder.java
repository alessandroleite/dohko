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
package org.excalibur.core.task.impl;

import java.io.Serializable;

import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.util.Exceptions;

import com.google.common.base.Preconditions;

public class TaskResultBuilder<T extends Serializable>
{
    private TaskType<T> task_;
    private T taskResult_;
    private TaskState taskState_;
    private String message_;
    
    private long startTimeInMillis;
    
    private long finishTimeInMillis;
    

    public TaskResultBuilder<T> setTask(TaskType<T> task)
    {
        this.task_ = task;
        return this;
    }

    public TaskResultBuilder<T> setTaskResult(T result)
    {
        this.taskResult_ = result;
        return this;
    }

    public TaskResultBuilder<T> setState(TaskState state)
    {
        this.taskState_ = state;
        return this;
    }

    public TaskResultBuilder<T> setState(TaskState state, Throwable exception)
    {
        setState(state);
        this.message_ = exception != null ? Exceptions.toString(exception) : null;

        return this;
    }
    
    public TaskResultBuilder<T> setStartTimeInMillis(long startTimeInMillis)
    {
        this.startTimeInMillis = startTimeInMillis;
        return this;
    }
    
    public TaskResultBuilder<T> setFinishTimeInMillis(long finishTimeInMillis)
    {
        this.finishTimeInMillis = finishTimeInMillis;
        return this;
    }

    public TaskResultBuilder<T> setState(String message)
    {
        this.message_ = message;
        return this;
    }

    public TaskResult<T> build()
    {
        Preconditions.checkState(this.task_ != null);
        return new TaskResultImpl<T>(this.task_, this.taskResult_, this.taskState_, this.message_, startTimeInMillis, finishTimeInMillis);
    }
}
