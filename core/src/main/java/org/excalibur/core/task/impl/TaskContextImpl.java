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

import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.execution.logger.ExecutionLogger;
import org.excalibur.core.executor.Context;
import org.excalibur.core.executor.task.ExecutionContext;
import org.excalibur.core.io.handlers.IOHandler;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;

import com.google.common.base.Preconditions;

public final class TaskContextImpl extends AbstractContextBase implements TaskContext
{
    private final TaskType<? extends Serializable> task_;
    private final ExecutionContext                 executionContext_;
    private final IOHandler                        ioHandler;
    private final ExecutionLogger                  logger_;
    private final WorkflowTaskRepository                   taskRepository_;
    private final UserRepository                   userRepository_;
    private final RegionRepository                 regionRepository_;
    
    public TaskContextImpl(TaskType<? extends Serializable> task, Context parent, ExecutionContext executionContext, IOHandler ioHandler,
            ExecutionLogger logger, WorkflowTaskRepository taskRepository, UserRepository userRepository, RegionRepository regionRepository)
    {
        super(parent);
        this.task_ = Preconditions.checkNotNull(task);
        this.executionContext_ = executionContext;
        this.ioHandler = ioHandler;
        this.logger_ = logger;
        this.taskRepository_ = taskRepository;
        this.userRepository_ = userRepository;
        this.regionRepository_ = regionRepository;
    }
    
    @Override
    public ExecutionLogger getLogger()
    {
        return logger_;
    }

    @Override
    public IOHandler getIOHandler()
    {
        return ioHandler;
    }

    @Override
    public ExecutionContext getExecutionContext()
    {
        return executionContext_;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Serializable> TaskType<T> getTask()
    {
        return (TaskType<T>) this.task_;
    }

    @Override
    public WorkflowTaskRepository getTaskRepository()
    {
        return this.taskRepository_;
    }

    @Override
    public UserRepository getUserRepository()
    {
        return userRepository_;
    }

    @Override
    public RegionRepository getRegionRepository()
    {
        return this.regionRepository_;
    }
}
