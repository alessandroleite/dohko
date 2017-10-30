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
package org.excalibur.core.workflow.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.excalibur.core.execution.logger.LogEntryImpl;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.util.Lists2;
import org.excalibur.core.util.concurrent.FutureListener;
import org.excalibur.core.workflow.domain.TaskDataDescription;
import org.excalibur.core.workflow.domain.TaskDescriptionState;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;

class TaskFutureListener<T extends Serializable> implements FutureListener<TaskResult<T>>
{
    private final TaskContext taskContext_;
    private final TaskType<T> task_;

    public TaskFutureListener(TaskType<T> task, TaskContext taskContext)
    {
        this.taskContext_ = taskContext;
        this.task_ = task;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void futureDone(Future<TaskResult<T>> future)
    {
        WorkflowTaskRepository repository = taskContext_.getTaskRepository();
        TaskDescriptionState state = new TaskDescriptionState().setStateTime(new Date());

        List<TaskDataDescription> outputData = new ArrayList<TaskDataDescription>();

        try
        {
            TaskResult<T> taskResult = future.get();
            state.setState(taskResult.getTaskState());

            T result = taskResult.getResult();
            if (result != null && result.getClass().isAssignableFrom(List.class))
            {
                Object first = Lists2.first((List<?>) result);

                if (first != null && first.getClass().isAssignableFrom(TaskDataDescription.class))
                {
                    outputData.addAll((List<TaskDataDescription>) result);
                }
            }
        }
        catch (InterruptedException e)
        {
            taskContext_.getLogger().addLogEntry(new LogEntryImpl(e.getMessage()));
            state.setState(TaskState.FAILED).setMessage(e.getMessage());
        }
        catch (ExecutionException e)
        {
            taskContext_.getLogger().addLogEntry(new LogEntryImpl(e.getMessage()));
            state.setState(TaskState.ERROR).setMessage(e.getMessage());
        }

        state.setTask(task_.getDescription())
             .setNode(taskContext_.getExecutionContext().getExecutionEnvironment().getLocation());
        
        repository.insertTaskState(state);

        if (!outputData.isEmpty())
        {
            repository.insertTaskData(outputData);
        }
    }
}
