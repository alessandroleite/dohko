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
package org.excalibur.core.test.workflow.repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.excalibur.core.test.TestSupport;
import org.excalibur.core.util.YesNoEnum;
import org.excalibur.core.workflow.domain.DataType;
import org.excalibur.core.workflow.domain.TaskDataDescription;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.repository.TaskRepository;
import org.excalibur.core.workflow.repository.WorkflowRepository;
import org.junit.Before;
import org.junit.Test;

import ch.vorburger.exec.ManagedProcessException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TaskRepositoryTest extends TestSupport
{
    private TaskRepository taskRepository;
    private WorkflowActivityDescription activity;

    @Override
    @Before
    public void setup() throws IOException, ManagedProcessException
    {
        super.setup();
        
        WorkflowRepository workflowRepository = openRepository(WorkflowRepository.class);
        taskRepository = openRepository(TaskRepository.class);

        WorkflowDescription workflow = new WorkflowDescription()
                .setCreatedIn(new Date())
                .setName("wk-1")
                .setStartActivityId(1)
                .setUser(user);

        workflow.setId(workflowRepository.insert(workflow));

        activity = new WorkflowActivityDescription()
                .setWorkflow(workflow)
                .setId(1)
                .setLabel("a1")
                .setType("foo.Bar");

        activity.setInternalId(workflowRepository.insert(activity));
    }
    
    @Test
    public void must_insert_one_task()
    {
        TaskDescription task = new TaskDescription()
                   .setActivity(activity)
                   .setExecutable("cat")
                   .setTypeClass("shell.linux.Command");
        
        task.setId(taskRepository.insert(task));
        assertThat(task.getId(), is(1));
        
        TaskDescription taskInserted = taskRepository.findTaskById(task.getId());
        assertThat(task, equalTo(taskInserted));
        
        TaskDataDescription data = new TaskDataDescription().setTask(task)
                .setDynamic(YesNoEnum.YES)
                .setName("profile")
                .setPath("~./profile")
                .setSplittable(YesNoEnum.NO)
                .setType(DataType.INPUT);
        
        data.setId(taskRepository.insertTaskData(data));
        assertThat(data.getId(), is(1));
        
        assertThat(data, equalTo(taskRepository.findTaskDataDescriptionById(data.getId())));
        
        List<TaskDescription> tasks = taskRepository.getTasksOfActivity(activity.getId());
        assertThat(tasks.size(), is(1));
        assertThat(tasks.get(0), equalTo(task));
                
        List<TaskDataDescription> taskData = taskRepository.getDataOfTask(task.getId());
        assertThat(taskData.size(), is(1));
        assertThat(taskData.get(0), equalTo(data));
        
        assertThat(taskRepository.getTaskStates(task.getId()).size(), is(0));
        
    }
}
