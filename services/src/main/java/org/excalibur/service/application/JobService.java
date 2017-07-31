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
package org.excalibur.service.application;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.repository.JobRepository;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.execution.job.ApplicationExecutionResult;
import org.excalibur.core.services.UserService;
import org.excalibur.core.util.AnyThrow;
import org.excalibur.jackson.databind.JsonYamlObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class JobService
{
    private static final Logger LOG = LoggerFactory.getLogger(JobService.class.getName());
            
    private final JsonYamlObjectMapper YAML_MAPPER = new JsonYamlObjectMapper();
    
    @Autowired
    private JobRepository jobRepository_;
    
    @Autowired
    private TaskRepository taskRepository_;
    
    @Autowired
    private UserService userService_;
    
    public ApplicationDescriptor findJobByUUID(String id)
    {
       return completJobState(this.jobRepository_.findByUUID(id));
    }
    
    public List<ApplicationDescriptor> listPendentJobs()
    {
        List<ApplicationDescriptor> jobs = new ArrayList<ApplicationDescriptor>();

        for (ApplicationDescriptor job : this.jobRepository_.listAllPending())
        {
            jobs.add(completJobState(job));
        }
        
        return jobs;
    }
    
    public List<ApplicationExecutionResult> getJobTasksResult(String jobId)
    {
    	return taskRepository_.getJobTasksResult(jobId);
    }
    
    protected ApplicationDescriptor completJobState(final ApplicationDescriptor job)
    {
        if (job == null)
        {
            return job;
        }
        
        try
        {
            ApplicationDescriptor jb = YAML_MAPPER.readValue(job.getPlainText(), ApplicationDescriptor.class);
            
            jb.setInternalId(job.getInternalId());
            jb.setUser(this.userService_.findUserById(job.getUser().getId()));
            jb.setPlainText(job.getPlainText());
            jb.setCreatedIn(job.getCreatedIn());
            jb.setFinishedIn(job.getFinishedIn());
            
            jb.getApplications().clear();
            
            for (Application task : this.taskRepository_.findAllTasksOfJob(job.getId()))
            {
                completeTaskState(task).setJob(jb);
                jb.getApplications().add(task);
            }
            
            return jb;
        }
        catch (IOException e)
        {
            LOG.error("Error on parsing the job [{}]. Error message: [{}]", job.getId(), e.getMessage(), e);
        }
        return job;
    }
    
    protected Application completeTaskState(Application task)
    {
        try
        {
            Application app = YAML_MAPPER.readValue(task.getPlainText().getBytes(), Application.class);
            task.setCommandLine(app.getCommandLine())
                .addAll(app.getFiles())
                .setName(app.getName());
        }
        catch (IOException e)
        {
        }
        return task;
    }

    public Application findApplicationByUUID(String id)
    {
        return completeTaskState(this.taskRepository_.findByUUID(id));
    }

    public void update(Application task, VirtualMachine worker, int exitValue, String uuid, long elapsedTime, String result, String sysout, String syserr)
    {
        this.taskRepository_.update(task, worker, exitValue, uuid, elapsedTime, result, sysout, syserr);
    }

    public ApplicationDescriptor finishJob(String jobId, long timeInMillis)
    {
        this.jobRepository_.finished(jobId, timeInMillis);
        
        return this.findJobByUUID(jobId);
    }

    public void insertJob(ApplicationDescriptor job)
    {
        for (Application application: job.getApplications())
        {
            try
            {
                application.setPlainText(YAML_MAPPER.writeValueAsString(application));
                application.setJob(job);
            }
            catch (JsonProcessingException e)
            { 
                AnyThrow.throwUncheked(e);
            }
        }
        
        job.setInternalId(this.jobRepository_.insert(job));
        this.taskRepository_.insert(job.getApplications());
        
        for (Application task: job.getApplications())
        {
            task.setInternalId(this.taskRepository_.findByUUID(task.getId()).getInternalId());
        }
    }

    protected void insertTask(Application task)
    {
        task.setInternalId(this.taskRepository_.insert(task));
    }
}