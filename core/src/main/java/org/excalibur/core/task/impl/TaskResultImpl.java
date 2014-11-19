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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.excalibur.core.ID;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.TaskType;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

class TaskResultImpl<T extends Serializable> implements TaskResult<T>
{
    private final T result_;
    private final TaskState taskState_;
    private final String message_;
    private final TaskType<T> task_;
    private final Map<ID, Serializable> resultValues_;
    private final long startTime_;
    private final long finishTime_;

    TaskResultImpl(TaskType<T> task, T taskResult, TaskState state, String message, long startTime, long finishTime)
    {
        this.task_ = checkNotNull(task);
        this.result_ = taskResult;
        this.taskState_ = checkNotNull(state);
        this.message_ = message;
        this.resultValues_ = new HashMap<ID, Serializable>();
        this.startTime_ = startTime;
        this.finishTime_ = finishTime;
    }

    public Serializable addResultData(ID id, Serializable data)
    {
        return this.resultValues_.put(id, data);
    }

    @Override
    public Map<ID, Serializable> getResultData()
    {
        return Collections.unmodifiableMap(resultValues_);
    }

    @Override
    public T getResult()
    {
        return result_;
    }

    @Override
    public String getMessage()
    {
        return message_;
    }

    @Override
    public TaskState getTaskState()
    {
        return taskState_;
    }

    public TaskType<T> getTask()
    {
        return task_;
    }

    /**
     * @return the startTime
     */
    @Override
    public long getStartTime()
    {
        return startTime_;
    }

    /**
     * @return the finishTime
     */
    @Override
    public long getFinishTime()
    {
        return finishTime_;
    }

    @Override
    public TaskResultBuilder<T> toBuilder()
    {
        return new TaskResultBuilder<T>()
                .setFinishTimeInMillis(this.getFinishTime())
                .setState(getTaskState()).setTask(getTask())
                .setTaskResult(getResult())
                .setStartTimeInMillis(this.getStartTime());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("started in", getStartTime())
                .add("status", getTaskState())
                .add("finished in", getFinishTime())
                .toString();
    }
}
