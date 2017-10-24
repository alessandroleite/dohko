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

import java.io.IOException;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.TaskOutput;
import org.excalibur.core.execution.domain.repository.JobRepository;
import org.excalibur.core.execution.domain.repository.TaskOutputRepository;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

import static java.util.UUID.*;

import com.google.common.collect.Iterables;

import ch.vorburger.exec.ManagedProcessException;

import static org.excalibur.core.execution.domain.TaskOutputType.*;

import static org.junit.Assert.*;

public class TaskOutputRepositoryTest extends TestSupport
{
    private TaskOutputRepository taskOutputRepository_;
    private Application task;
    
    @Override
    public void setup() throws IOException, ManagedProcessException
    {
        super.setup();
        taskOutputRepository_ = openRepository(TaskOutputRepository.class);
        
        
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
    public void must_insert_one_output() throws Exception 
    {
    	TaskOutput sysout = new TaskOutput().setTaskId(task.getId()).setType(SYSOUT).setValue(1).setId(randomUUID().toString());
    	Integer id = taskOutputRepository_.insert(sysout);
    	
    	assertNotNull(id);
    	assertTrue(id > 0);
    	
    	Iterable<TaskOutput> outputs = taskOutputRepository_.getAllOutputsOfTask(sysout.getTaskId());
    	
    	assertNotNull(outputs);
    	assertFalse(Iterables.isEmpty(outputs));
    	assertEquals(1, Iterables.size(outputs));
    	assertEquals(sysout, Iterables.getFirst(outputs, null));
    	
    	assertEquals(sysout, taskOutputRepository_.getById(sysout.getId()));
    	
    	taskOutputRepository_.delete(sysout.getId());
    }
}
