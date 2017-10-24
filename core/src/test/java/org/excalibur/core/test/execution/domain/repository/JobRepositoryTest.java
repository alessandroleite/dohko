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
package org.excalibur.core.test.execution.domain.repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.TaskStatusType;
import org.excalibur.core.execution.domain.repository.JobRepository;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.execution.domain.repository.TaskStatusRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

import ch.vorburger.exec.ManagedProcessException;

import static java.util.UUID.*;
import static java.lang.System.*;

import static org.junit.Assert.*;

public class JobRepositoryTest extends TestSupport
{
    private JobRepository jobRepository_;
    private TaskRepository taskRepository_;
    private TaskStatusRepository statusRepository_;
    
    @Override
    public void setup() throws IOException, ManagedProcessException
    {
        super.setup();
        jobRepository_ = openRepository(JobRepository.class);
        taskRepository_ = openRepository(TaskRepository.class);
        statusRepository_ = openRepository(TaskStatusRepository.class);
    }
    
    @Test
    public void must_insert_on_job_with_one_task()
    {
        ApplicationDescriptor job = new ApplicationDescriptor()
        		.setId(randomUUID().toString())
        		.setCreatedIn(currentTimeMillis())
        		.setDescription("test")
        		.setName("j-test")
        		.setPlainText("job-t")
        		.setUser(getUser());
        
        Application who = new Application()
                .setCommandLine("who")
                .setId(randomUUID().toString())
                .setJobId(job.getId())
                .setName("who")
                .setPlainText("who");
        
//        job.setApplications(new Applications().add(who));
        job.addApplication(who);
        
        jobRepository_.insert(job);
        taskRepository_.insert(who);
        
        long statusTime = System.currentTimeMillis();
        
        statusRepository_.insert(new TaskStatus().setDate(new Date(statusTime)).setTaskId(who.getId()).setPid(3200).setType(TaskStatusType.PENDING));
        statusRepository_.insert(new TaskStatus().setDate(new Date(statusTime + 2000)).setTaskId(who.getId()).setPid(6800).setType(TaskStatusType.RUNNING).setWorker("1"));
        
        List<Application> tasks = taskRepository_.findAllTasksOfJob(job.getId());
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        List<TaskStatus> statuses = statusRepository_.getAllStatusesOfTask(who.getId());
        assertNotNull(statuses);
        assertEquals(2, statuses.size());
        
        assertEquals(statuses.get(statuses.size() - 1),  statusRepository_.getLastStatusOfTask(who.getId()).orNull());
        
    }
}
