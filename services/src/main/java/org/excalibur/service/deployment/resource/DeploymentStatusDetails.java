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
package org.excalibur.service.deployment.resource;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.DeploymentStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType(name = "deployment-status-details", propOrder = { "deploymentId_", "user_", "status_", "date_" })
@JsonRootName("deployment-status-details")
@JsonPropertyOrder(value = { "deploymentId_", "user_", "status_", "date_" })
public class DeploymentStatusDetails implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 573226023084538803L;

    @JsonProperty("deployment-id")
    @XmlAttribute(name = "deployment-id", required = true)
    private Integer deploymentId_;

    @JsonProperty("username")
    @XmlAttribute(name = "username", required = true)
    private String user_;

    @JsonProperty("status")
    @XmlElement(name = "status", required = true)
    private DeploymentStatus status_;

    @JsonProperty("status-time")
    @XmlElement(name = "status-time", required = true)
    private Date date_;

    public DeploymentStatusDetails()
    {
        super();
    }

    public DeploymentStatusDetails(Integer id, String username, DeploymentStatus status, Date time)
    {
        this.deploymentId_ = id;
        this.user_ = username;
        this.status_ = status;
        this.date_ = time;
    }

    public static DeploymentStatusDetails fromDeployment(Deployment deployment)
    {
        return new DeploymentStatusDetails(deployment.getId(), deployment.getUsername(), deployment.getStatus(), deployment.getStatusTime());
    }

    public DeploymentStatusDetails withDeplomentId(Integer id)
    {
        this.deploymentId_ = id;
        return this;
    }

    public DeploymentStatusDetails withUser(String user)
    {
        this.user_ = user;
        return this;
    }

    public DeploymentStatusDetails withDeplomentStatus(DeploymentStatus status)
    {
        this.status_ = status;
        return this;
    }

    public DeploymentStatusDetails withDate(Date date)
    {
        this.date_ = date;
        return this;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user_;
    }

    /**
     * @param user
     *            the user to set
     */
    public DeploymentStatusDetails setUser(String user)
    {
        this.user_ = user;
        return this;
    }

    /**
     * @return the deploymentId
     */
    public Integer getDeploymentId()
    {
        return deploymentId_;
    }

    /**
     * @param deploymentId
     *            the deploymentId to set
     */
    public DeploymentStatusDetails setDeploymentId(Integer deploymentId)
    {
        this.deploymentId_ = deploymentId;
        return this;
    }

    /**
     * @return the status
     */
    public DeploymentStatus getStatus()
    {
        return status_;
    }

    /**
     * @param status
     *            the status to set
     */
    public DeploymentStatusDetails setStatus(DeploymentStatus status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the date
     */
    public Date getDate()
    {
        return date_;
    }

    /**
     * @param date
     *            the date to set
     */
    public DeploymentStatusDetails setDate(Date date)
    {
        this.date_ = date;
        return this;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this.getClass()).omitNullValues().toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deploymentId_ == null) ? 0 : deploymentId_.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof DeploymentStatusDetails))
        {
            return false;
        }

        DeploymentStatusDetails other = (DeploymentStatusDetails) obj;
        if (deploymentId_ == null)
        {
            if (other.deploymentId_ != null)
            {
                return false;
            }
        }
        else if (!deploymentId_.equals(other.deploymentId_))
        {
            return false;
        }
        return true;
    }

}
