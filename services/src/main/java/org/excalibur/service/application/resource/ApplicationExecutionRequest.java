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

import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.Application;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application-exec-request")
public class ApplicationExecutionRequest implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8650652806219750494L;

    @XmlElement(name = "id", required = true, nillable = false)
    private String id_;
    
    @XmlElement(name = "job-id", required = true, nillable = false)
    private String jobId_;

    @XmlElement(name = "owner", required = true)
    private User owner_;

    @XmlElement(name = "keys", required = true)
    private KeyPairs keyPairs_;

    @XmlElement(name = "application", required = true)
    private Application application_;

    @XmlElement(name = "manager", required = true)
    private VirtualMachine manager_;

    @XmlElement(name = "worker", required = true)
    private VirtualMachine worker_;
    

    /**
     * @return the id
     */
    public String getId()
    {
        return id_;
    }

    /**
     * @param id the id to set
     */
    public ApplicationExecutionRequest setId(String id)
    {
        this.id_ = id;
        return this;
    }
    

    /**
     * @return the jobID
     */
    public String getJobId()
    {
        return jobId_;
    }

    /**
     * @param jobID the jobID to set
     */
    public ApplicationExecutionRequest setJobId(String jobID)
    {
        this.jobId_ = jobID;
        return this;
    }

    /**
     * @return the owner
     */
    public User getOwner()
    {
        return owner_;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public ApplicationExecutionRequest setOwner(User owner)
    {
        this.owner_ = owner;
        return this;
    }

    /**
     * @return the keyPairs
     */
    public KeyPairs getKeyPairs()
    {
        return keyPairs_;
    }

    /**
     * @param keyPairs
     *            the keyPairs to set
     */
    public ApplicationExecutionRequest setKeyPairs(KeyPairs keyPairs)
    {
        this.keyPairs_ = keyPairs;
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
    public ApplicationExecutionRequest setApplication(Application application)
    {
        this.application_ = application;
        return this;
    }

    /**
     * @return the manager
     */
    public VirtualMachine getManager()
    {
        return manager_;
    }

    /**
     * @param manager
     *            the manager to set
     */
    public ApplicationExecutionRequest setManager(VirtualMachine manager)
    {
        this.manager_ = manager;
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
     * @param worker
     *            the worker to set
     */
    public ApplicationExecutionRequest setWorker(VirtualMachine worker)
    {
        this.worker_ = worker;
        return this;
    }
}
