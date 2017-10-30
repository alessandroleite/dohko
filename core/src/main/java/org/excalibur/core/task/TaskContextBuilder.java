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
import org.excalibur.core.execution.logger.ExecutionLogger;
import org.excalibur.core.execution.logger.impl.MemoryExecutionLogger;
import org.excalibur.core.executor.Context;
import org.excalibur.core.executor.task.ExecutionContext;
import org.excalibur.core.io.handlers.IOHandler;
import org.excalibur.core.task.impl.TaskContextImpl;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;

import static com.google.common.base.Preconditions.*;

public class TaskContextBuilder
{
    private Context                          parentContext_;
    private TaskType<? extends Serializable> task_;
    private IOHandler                        ioHandler_;
    private ExecutionLogger                  logger_;
    private ExecutionContext                 executionContext_;
    private WorkflowTaskRepository                   taskRepository_;
    private UserRepository                   userRepository_;
    private RegionRepository                 regionRepository_;

    public <T extends Serializable> TaskContextBuilder setTask(TaskType<T> task)
    {
        this.task_ = task;
        return this;
    }

    public TaskContextBuilder setParentContext(Context context)
    {
        this.parentContext_ = context;
        return this;
    }

    public TaskContextBuilder setExecutionContext(ExecutionContext context)
    {
        this.executionContext_ = context;
        return this;
    }
    
    public TaskContextBuilder setTaskRepository(WorkflowTaskRepository repository)
    {
        this.taskRepository_ = repository;
        return this;
    }
    
    public TaskContextBuilder setUserRepository(UserRepository repository)
    {
        this.userRepository_ = repository;
        return this;
    }
    
    public TaskContextBuilder setRegionRepository(RegionRepository regionRepository_)
    {
        this.regionRepository_ = regionRepository_;
        return this;
    }

    public TaskContext build()
    {
        if (logger_ == null)
        {
            this.logger_ = new MemoryExecutionLogger();
        }
        
        if (taskRepository_ == null || userRepository_ == null)
        {
            return null;
        }

        return new TaskContextImpl(task_, parentContext_, checkNotNull(executionContext_), ioHandler_, logger_, taskRepository_, userRepository_,
                regionRepository_);
    }
}
