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
import java.util.UUID;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.Applications;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.repository.JobRepository;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

public class JobRepositoryTest extends TestSupport
{
    private JobRepository jobRepository_;
    private TaskRepository taskRepository_;
    
    @Override
    public void setup() throws IOException
    {
        super.setup();
        this.jobRepository_ = openRepository(JobRepository.class);
        this.taskRepository_ = openRepository(TaskRepository.class);
    }
    
    @Test
    public void must_insert_on_job_with_one_task()
    {
        ApplicationDescriptor job = new ApplicationDescriptor();
        
        Application who = new Application()
                .setCommandLine("who")
                .setId(UUID.randomUUID().toString())
                .setJob(job).setPlainText("who")
                .setStatus(TaskStatus.PENDING);
        
        job.setApplications(new Applications().add(who))
                .setId(UUID.randomUUID().toString())
                .setCreatedIn(System.currentTimeMillis())
                .setPlainText("job-t")
                .setUser(user);
        
        Integer jobId = this.jobRepository_.insert(job);
        job.setInternalId(jobId);
        
        Integer taskId = taskRepository_.insert(who);
        who.setInternalId(taskId);
        
        this.taskRepository_.findAllTasksOfJob(job.getId());
    }
}
