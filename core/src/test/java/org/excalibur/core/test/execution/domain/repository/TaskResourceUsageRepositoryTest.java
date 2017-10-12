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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.TaskResourceUsage;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.repository.JobRepository;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.execution.domain.repository.TaskResourceUsageRepository;
import org.excalibur.core.execution.domain.repository.TaskStatusRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

import static java.math.BigDecimal.TEN;
import static org.excalibur.core.execution.domain.ResourceType.CPU;
import static org.excalibur.core.execution.domain.TaskStatusType.RUNNING;

import static org.junit.Assert.*;

public class TaskResourceUsageRepositoryTest extends TestSupport 
{
	private TaskResourceUsageRepository repository_;
	private TaskStatus taskStatus_;
	
	@Override
	public void setup() throws IOException 
	{
		super.setup();
		repository_ = openRepository(TaskResourceUsageRepository.class);
		
		
		ApplicationDescriptor job = new ApplicationDescriptor()
        		.setId(randomUUID().toString())
        		.setCreatedIn(currentTimeMillis())
        		.setDescription("test")
        		.setName("j-test")
        		.setPlainText("job-t")
        		.setUser(getUser());
        
        Application task = new Application()
        		.setCommandLine("who")
                .setId(randomUUID().toString())
                .setJobId(job.getId())
                .setName("who")
                .setPlainText("who");
        
        job.addApplication(task);
        
        job.setInternalId(openRepository(JobRepository.class).insert(job));
        openRepository(TaskRepository.class).insert(task);
        
        taskStatus_ = new TaskStatus()
        		.setDate(new Date())
        		.setPid(3000)
        		.setTaskId(task.getId())
        		.setType(RUNNING)
        		.setWorker("dummy");
        
        openRepository(TaskStatusRepository.class).insert(taskStatus_);
	}
	
	@Test
	public void must_insert_one_task_resource_usage()
	{
		TaskResourceUsage resourceUsage = new TaskResourceUsage()
				.setDatetime(new Date())
				.setPid(taskStatus_.getPid())
				.setResourceType(CPU)
				.setTaskId(taskStatus_.getTaskId())
				.setValue(TEN);
		
		Integer resourceUsageId = repository_.insert(resourceUsage);
		assertNotNull(resourceUsageId);
		
		List<TaskResourceUsage> resources = repository_.getAllTypeOfTask(resourceUsage.getTaskId(), CPU.getId());
		assertEquals(1, resources.size());
		assertEquals(resourceUsage, resources.get(0));
		
		assertEquals(resourceUsageId, resources.get(0).getId());
		assertEquals(TEN.intValue(), resources.get(0).getValue().intValue());
		
	}
	
	@Override
    public void tearDown() throws Exception 
    {
		JobRepository jobRepository = openRepository(JobRepository.class);
		String jobId = jobRepository.findJobOfTaskId(taskStatus_.getTaskId()).getId();
		
		repository_.deleteAllOfTask(taskStatus_.getTaskId());
		openRepository(TaskStatusRepository.class).deleteAllStatusesOfTask(taskStatus_.getTaskId());
    	openRepository(TaskRepository.class).delete(taskStatus_.getTaskId());
    	jobRepository.delete(jobId);
    	
    	super.tearDown();
    }
}
