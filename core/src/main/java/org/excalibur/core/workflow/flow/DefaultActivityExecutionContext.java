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

import java.util.Collections;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.executor.Context;
import org.excalibur.core.executor.task.SingleTaskExecutorService;
import org.excalibur.core.executor.task.TaskExecutionService;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;

import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.*;

public class DefaultActivityExecutionContext implements ActivityExecutionContext
{
    private final Context           parentContext_;
    private final Activity          activity_;
    private final List<TaskType<?>> tasks_ = Lists.newArrayList();
    private final WorkflowTaskRepository    taskRepository_;
    private final UserRepository    userRepository_;
    private final RegionRepository  regionRepository_;
    private final VirtualMachine    node_;

    public DefaultActivityExecutionContext(Context parentContext, Activity activity, 
            WorkflowTaskRepository taskRepository, UserRepository userRepository, RegionRepository regionRepository,
            VirtualMachine machine)
    {
        this.parentContext_ = parentContext;
        this.activity_ = checkNotNull(activity);
        this.taskRepository_ = checkNotNull(taskRepository);
        this.userRepository_ = checkNotNull(userRepository);
        this.regionRepository_ = checkNotNull(regionRepository);
        this.node_ = checkNotNull(machine);

        createTasks();
    }

    private void createTasks()
    {
        for (TaskDescription task : activity_.getDescription().getTasks())
        {
            TaskType<?> taskType = (TaskType<?>) new Mirror().on(task.getTypeClass()).invoke().constructor().withArgs(task);
            tasks_.add(taskType);
        }
    }

    @Override
    public Context getParentContext()
    {
        return parentContext_;
    }

    @Override
    public Activity getActivity()
    {
        return activity_;
    }

    @Override
    public VirtualMachine getLocation()
    {
        return node_;
    }

    @Override
    public List<TaskType<?>> getTasks()
    {
        return Collections.unmodifiableList(tasks_);
    }

    @Override
    public TaskExecutionService getTaskExecutionService()
    {
        return new SingleTaskExecutorService();
    }

    @Override
    public WorkflowTaskRepository getTaskRepository()
    {
        return taskRepository_;
    }

    @Override
    public UserRepository getUserRepository()
    {
        return userRepository_;
    }

    @Override
    public RegionRepository getRegionRepository()
    {
        return regionRepository_;
    }
}
