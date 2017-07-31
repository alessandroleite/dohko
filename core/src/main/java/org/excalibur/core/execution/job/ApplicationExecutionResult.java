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
package org.excalibur.core.execution.job;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.Application;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application-exec-result")
@XmlType(name = "application-exec-result")
public class ApplicationExecutionResult implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = 7247006771249750432L;

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
    
    @XmlElement(name = "failure-reason")
    private String failureReason_;
    
    @XmlElement(name = "worker", required = true)
    private VirtualMachine worker_;
    
    @XmlElement(name = "output")
    private String output_;
    
    @XmlElement(name = "sysout")
    private String sysout_;
    
    @XmlElement(name = "syserr")
    private String syserr_;
    

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
    
    
    /**
	 * @return the sysout
	 */
	public String getSysout() 
	{
		return sysout_;
	}

	/**
	 * @param sysout the sysout to set
	 * @return this instance
	 */
	public ApplicationExecutionResult setSysout(String sysout) 
	{
		this.sysout_ = sysout;
		return this;
	}

	/**
	 * @return the syserr
	 */
	public String getSyserr() 
	{
		return syserr_;
	}

	/**
	 * @param syserr the syserr to set
	 * @return this instance
	 */
	public ApplicationExecutionResult setSyserr(String syserr) 
	{
		this.syserr_ = syserr;
		return this;
	}

	@Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("id", getId())
                .add("job id", getJobId())
                .add("owner", getUser())
                .add("task", getApplication())
                .add("worker", getWorker())
                .add("exit value", getExitValue())
                .add("failure reason", getFailureReason())
                .add("elapsed time(ns)", getElapsedTime())
                .add("elapsed time(ms)", getElapsedTimeMillis())
                .add("system output", getSysout())
                .add("system err output", getSyserr())
                .omitNullValues()
                .toString();
    }
	
	
	@Override
	protected ApplicationExecutionResult clone()  
	{
		ApplicationExecutionResult clone;
		
		try 
		{
			clone = (ApplicationExecutionResult) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new ApplicationExecutionResult()
					.setApplication(getApplication() != null ? getApplication().clone(): null)
					.setElapsedTime(getElapsedTime())
					.setElapsedTimeMillis(getElapsedTimeMillis())
					.setExitValue(getExitValue())
					.setFailureReason(getFailureReason())
					.setId(getId())
					.setJobId(getJobId())
					.setOutput(getOutput())
					.setSyserr(getSyserr())
					.setSysout(getSysout())
					.setUser(getUser() != null ? getUser().clone(): null)
					.setWorker(getWorker());
					
		}
		
		return clone;
	}
}