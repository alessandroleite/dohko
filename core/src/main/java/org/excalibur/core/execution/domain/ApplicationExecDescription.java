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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.excalibur.core.domain.User;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Objects.equal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application-description")
public class ApplicationExecDescription implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8302731246284510180L;

    @XmlTransient
    private Integer id_;

    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "application", required = true, nillable = false)
    private ScriptStatement application_;
    
    @XmlElement(name = "user", required=true, nillable=false)
    private User user;

    @XmlElement(name = "resource-name", required = true, nillable = false)
    private String resource_;

    @XmlElement(name = "number-of-execution", defaultValue = "1", required = true)
    private Integer numberOfExecutions_ = 1;

    @XmlElement(name = "on-failure")
    private FailureAction failureAction_ = FailureAction.ABORT;
    
    @XmlElement(name="created-in")
    private Date createdIn;

    public ApplicationExecDescription()
    {
        super();
        this.createdIn = new Date();
    }

    public ApplicationExecDescription(Integer id)
    {
        this();
        this.id_ = id;
    }

    public ApplicationExecDescription(String name, ScriptStatement application, String resource)
    {
        this();
        
        this.name_ = name;
        this.application_ = application;
        this.resource_ = resource;
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
    public ApplicationExecDescription setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public ApplicationExecDescription setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the application
     */
    public ScriptStatement getApplication()
    {
        return application_;
    }

    /**
     * @param application
     *            the application to set
     */
    public ApplicationExecDescription setApplication(ScriptStatement application)
    {
        this.application_ = application;
        return this;
    }

    /**
     * @return the numberOfExecutions
     */
    public Integer getNumberOfExecutions()
    {
        return numberOfExecutions_;
    }

    /**
     * @param numberOfExecutions
     *            the numberOfExecutions to set
     */
    public ApplicationExecDescription setNumberOfExecutions(Integer numberOfExecutions)
    {
        this.numberOfExecutions_ = numberOfExecutions;
        return this;
    }

    /**
     * @return the failureAction
     */
    public FailureAction getFailureAction()
    {
        return failureAction_;
    }

    /**
     * @param failureAction
     *            the failureAction to set
     */
    public ApplicationExecDescription setFailureAction(FailureAction failureAction)
    {
        this.failureAction_ = failureAction;
        return this;
    }

    /**
     * @return the resource
     */
    public String getResource()
    {
        return resource_;
    }
    
    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public ApplicationExecDescription setUser(User user)
    {
        this.user = user;
        return this;
    }

    /**
     * @param resource
     *            the resource to set
     */
    public ApplicationExecDescription setResource(String resource)
    {
        this.resource_ = resource;
        return this;
    }
    
    /**
     * @return the createdIn
     */
    public Date getCreatedIn()
    {
        return createdIn;
    }

    /**
     * @param createdIn the createdIn to set
     */
    public ApplicationExecDescription setCreatedIn(Date createdIn)
    {
        this.createdIn = createdIn;
        return this;
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getName(), this.getId());
    }

   
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof ApplicationExecDescription))
        {
            return false;
        }
        
        ApplicationExecDescription other = (ApplicationExecDescription) obj;
        
        return equal(this.getId(), other.getId()) || equal(this.getName(), other.getName()); 
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("name", this.getName())
                .add("on-failure", getFailureAction())
                .add("resource", getResource())
                .add("number-of-execution(s)", getNumberOfExecutions())
                .add("application", this.getApplication())
                .add("owner", this.getUser())
                .toString();
    }
}
