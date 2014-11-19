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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.workflow.domain.TaskDataDescription;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.domain.TaskDescriptionState;

import static com.google.common.base.Preconditions.*;

public abstract class AbstractTaskTypeSupport<T extends Serializable> implements TaskType<T>
{
    private final TaskDescription task_;
    private final List<TaskDataDescription> taskData_ = new CopyOnWriteArrayList<TaskDataDescription>();
    private volatile TaskResult<T> result_;

    public AbstractTaskTypeSupport(TaskDescription task)
    {
        this.task_ = checkNotNull(task);
    }

    @Override
    public TaskDescription getDescription()
    {
        return task_;
    }

    @Override
    public List<TaskDataDescription> getData()
    {
        return taskData_;
    }

    @Override
    public final TaskResult<T> execute(TaskContext context)
    {
        checkState(getDescription() == context.getTask().getDescription());

        TaskDescriptionState executingState = new TaskDescriptionState();
        executingState.setNode(context.getExecutionContext().getExecutionEnvironment().getLocation())
                .setState(TaskState.EXECUTING)
                .setStateTime(new Date())
                .setTask(context.getTask().getDescription());

        executingState.setId(context.getTaskRepository().insertTaskState(executingState));

        TaskResult<T> result = doExecute(context);
        checkState(result != null);

        TaskDescriptionState finishState = executingState.clone()
                .setState(result.getTaskState())
                .setStateTime(new Date())
                .setMessage(result.getMessage());

        finishState.setId(context.getTaskRepository().insertTaskState(finishState));
        
        this.result_ = result;
        return result;
    }
    
    @Override
    public TaskResult<T> getResult()
    {
        return result_;
    }

    protected abstract TaskResult<T> doExecute(TaskContext context);
}
