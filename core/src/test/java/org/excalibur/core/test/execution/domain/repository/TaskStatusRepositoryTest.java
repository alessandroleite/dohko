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

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;
import static org.excalibur.core.execution.domain.TaskStatusType.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.repository.JobRepository;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.execution.domain.repository.TaskStatusRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import ch.vorburger.exec.ManagedProcessException;

public class TaskStatusRepositoryTest extends TestSupport 
{
	private TaskStatusRepository taskStatusRepository;
	private Application task;
	
	@Override
	public void setup() throws IOException, ManagedProcessException 
	{
		super.setup();
		taskStatusRepository = openRepository(TaskStatusRepository.class);
		
		ApplicationDescriptor job = new ApplicationDescriptor()
        		.setId(randomUUID().toString())
        		.setCreatedIn(currentTimeMillis())
        		.setDescription("test")
        		.setName("j-test")
        		.setPlainText("job-t")
        		.setUser(getUser());
        
        task = new Application()
                .setCommandLine("who")
                .setId(randomUUID().toString())
                .setJobId(job.getId())
                .setName("who")
                .setPlainText("who");
        
        job.addApplication(task);
        
        job.setInternalId(openRepository(JobRepository.class).insert(job));
        openRepository(TaskRepository.class).insert(task);
	}
	
	@Override
    public void tearDown() throws Exception 
    {
    	openRepository(TaskRepository.class).delete(task.getId());
    	openRepository(JobRepository.class).delete(task.getJobId());
    	
    	super.tearDown();
    }
	
	@Test
	public void must_insert_one_task_status()
	{
		TaskStatus status = new TaskStatus()
				.setDate(new Date())
				.setTaskId(task.getId())
				.setTaskName(task.getName())
				.setType(PENDING);
		
		Integer id = taskStatusRepository.insert(status);
		assertNotNull(id);
		assertTrue(id > 0);
		
		Optional<TaskStatus> st = taskStatusRepository.getLastStatusOfTask(status.getTaskId());
		assertTrue(st.isPresent());
		assertEquals(status, st.get());
	}
	
	@Test
	public void must_insert_three_different_statuses_with_same_time()
	{
		Date now = new Date();
		
		TaskStatus status = new TaskStatus()
				.setDate(now)
				.setTaskId(task.getId())
				.setTaskName(task.getName())
				.setType(PENDING);
		
		List<TaskStatus> statuses = Lists.newArrayList(status, status.clone().setType(RUNNING), status.clone().setType(FINISHED));
		taskStatusRepository.insert(statuses);
		
		Optional<TaskStatus> lastTaskStatus = taskStatusRepository.getLastStatusOfTask(task.getId());
		assertTrue(lastTaskStatus.isPresent());
		assertEquals(statuses.get(2), lastTaskStatus.get());
		
		assertEquals(statuses.size(), taskStatusRepository.getAllStatusesOfTask(task.getId()).size());
	}
}
