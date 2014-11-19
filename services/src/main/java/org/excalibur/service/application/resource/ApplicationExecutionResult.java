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
package org.excalibur.service.application.resource;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.Application;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application-exec-result")
public class ApplicationExecutionResult implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8969247666358299673L;

    @XmlElement(name = "id", nillable = false, required = true)
    private String id;
    
    @XmlElement(name = "job-id", nillable = false, required = true)
    private String jobId;

    @XmlElement(name = "owner")
    private User user_;

    @XmlElement(name = "application")
    private Application application_;

    @XmlElement(name = "elapsed-time")
    private long elapsedTime_;
    
    @XmlElement(name = "elapsed-time-ms")
    private long elapsedTimeMillis_;
    
    @XmlElement(name = "exit-value")
    private int exitValue_;
    
    private String failureReason_;
    
    @XmlElement(name = "worker", required = true)
    private VirtualMachine worker_;
    
    @XmlElement(name = "output")
    private String output_;
    

    /**
     * @return the exitValue
     */
    public int getExitValue()
    {
        return exitValue_;
    }

    /**
     * @param exitValue the exitValue to set
     */
    public ApplicationExecutionResult setExitValue(int exitValue)
    {
        this.exitValue_ = exitValue;
        return this;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public ApplicationExecutionResult setId(String id)
    {
        this.id = id;
        return this;
    }
    
    /**
     * @return the jobId
     */
    public String getJobId()
    {
        return jobId;
    }

    /**
     * @param jobId the jobId to set
     */
    public ApplicationExecutionResult setJobId(String jobId)
    {
        this.jobId = jobId;
        return this;
    }

    public ApplicationExecutionResult setReplyId(String id)
    {        
        return this.setId(id);
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user_;
    }

    /**
     * @param user
     *            the user to set
     */
    public ApplicationExecutionResult setUser(User user)
    {
        this.user_ = user;
        return this;
    }

    /**
     * @return the application
     */
    public Application getApplication()
    {
        return application_;
    }

    /**
     * @param application
     *            the application to set
     */
    public ApplicationExecutionResult setApplication(Application application)
    {
        this.application_ = application;
        return this;
    }

    /**
     * @return the elapsedTime
     */
    public long getElapsedTime()
    {
        return elapsedTime_;
    }

    /**
     * @param elapsedTime
     *            the elapsedTime to set
     */
    public ApplicationExecutionResult setElapsedTime(long elapsedTime)
    {
        this.elapsedTime_ = elapsedTime;
        return this;
    }
    

    /**
     * @return the elapsedTimeMillis
     */
    public long getElapsedTimeMillis()
    {
        return elapsedTimeMillis_;
    }

    /**
     * @param elapsedTimeMillis the elapsedTimeMillis to set
     */
    public ApplicationExecutionResult setElapsedTimeMillis(long elapsedTimeMillis)
    {
        this.elapsedTimeMillis_ = elapsedTimeMillis;
        return this;
    }

    /**
     * @return the worker
     */
    public VirtualMachine getWorker()
    {
        return worker_;
    }

    /**
     * @param worker the worker to set
     */
    public ApplicationExecutionResult setWorker(VirtualMachine worker)
    {
        this.worker_ = worker;
        return this;
    }

    /**
     * @return the output
     */
    public String getOutput()
    {
        return output_;
    }

    /**
     * @param output the output to set
     */
    public ApplicationExecutionResult setOutput(String output)
    {
        this.output_ = output;
        return this;
    }

    /**
     * @return the failureReason
     */
    public String getFailureReason()
    {
        return failureReason_;
    }

    /**
     * @param failureReason the failureReason to set
     */
    public ApplicationExecutionResult setFailureReason(String failureReason)
    {
        this.failureReason_ = failureReason;
        return this;
    }
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("id", this.getId())
                .add("job id", this.getJobId())
                .add("owner", this.getUser())
                .add("task", this.getApplication())
                .add("worker", this.getWorker())
                .add("exit value", this.getExitValue())
                .add("failure reason", this.getFailureReason())
                .add("elapsed time(ns)", this.getElapsedTime())
                .add("elapsed time(ms)", this.getElapsedTimeMillis())
                .omitNullValues().toString();
    }
}
