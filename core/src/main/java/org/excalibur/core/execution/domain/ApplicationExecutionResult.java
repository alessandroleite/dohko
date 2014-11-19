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
package org.excalibur.core.execution.domain;

import java.io.Serializable;
import java.util.Date;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.ApplicationExecDescription;

import com.google.common.base.Objects;

public class ApplicationExecutionResult implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -1323082988330443102L;

    private Integer                    id_;
    private VirtualMachine             instance_;
    private ApplicationExecDescription application_;
    private Date                       startedIn_;
    private Date                       finishedIn_;
    private Integer                    exitCode_;
    private String                     output_;
    private String                     error_;

    public ApplicationExecutionResult()
    {
        super();
    }
    
    public ApplicationExecutionResult(Integer id)
    {
        this.id_ = id;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public ApplicationExecutionResult setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the instance
     */
    public VirtualMachine getInstance()
    {
        return instance_;
    }

    /**
     * @param instance
     *            the instance to set
     */
    public ApplicationExecutionResult setInstance(VirtualMachine instance)
    {
        this.instance_ = instance;
        return this;
    }

    /**
     * @return the application
     */
    public ApplicationExecDescription getApplication()
    {
        return application_;
    }

    /**
     * @param application the application to set
     */
    public ApplicationExecutionResult setApplication(ApplicationExecDescription application)
    {
        this.application_ = application;
        return this;
    }

    /**
     * @return the startedIn
     */
    public Date getStartedIn()
    {
        return startedIn_;
    }

    /**
     * @param startedIn
     *            the startedIn to set
     */
    public ApplicationExecutionResult setStartedIn(Date startedIn)
    {
        this.startedIn_ = startedIn;
        return this;
    }

    /**
     * @return the finishedIn
     */
    public Date getFinishedIn()
    {
        return finishedIn_;
    }

    /**
     * @param finishedIn
     *            the finishedIn to set
     */
    public ApplicationExecutionResult setFinishedIn(Date finishedIn)
    {
        this.finishedIn_ = finishedIn;
        return this;
    }

    /**
     * @return the exitCode
     */
    public Integer getExitCode()
    {
        return exitCode_;
    }

    /**
     * @param exitCode
     *            the exitCode to set
     */
    public ApplicationExecutionResult setExitCode(Integer exitCode)
    {
        this.exitCode_ = exitCode;
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
     * @return the error
     */
    public String getError()
    {
        return error_;
    }

    /**
     * @param error the error to set
     */
    public ApplicationExecutionResult setError(String error)
    {
        this.error_ = error;
        return this;
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ApplicationExecutionResult))
        {
            return false;
        }

        ApplicationExecutionResult other = (ApplicationExecutionResult) obj;

        return Objects.equal(this.getId(), other.getId());
    }

}
