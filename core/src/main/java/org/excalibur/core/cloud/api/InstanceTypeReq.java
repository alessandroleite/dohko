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
package org.excalibur.core.cloud.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "instance-type-req")
@XmlType(name = "instance-type-req", propOrder = { "name_", "numberOfInstances_" })
public class InstanceTypeReq implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2851598555265828519L;

    @XmlElement(name = "name", required = true, nillable = false)
    private String name_;

    @XmlElement(name = "number-of-instances", defaultValue = "1", required = true, nillable = false)
    private Integer numberOfInstances_ = 1;

    @XmlTransient
    private InstanceType instanceType_;

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
    public InstanceTypeReq setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the numberOfInstances
     */
    public Integer getNumberOfInstances()
    {
        return numberOfInstances_;
    }

    /**
     * @param numberOfInstances
     *            the numberOfInstances to set
     */
    public InstanceTypeReq setNumberOfInstances(Integer numberOfInstances)
    {
        this.numberOfInstances_ = numberOfInstances;
        Preconditions.checkState(numberOfInstances > 0);
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof InstanceTypeReq))
        {
            return false;
        }

        InstanceTypeReq other = (InstanceTypeReq) obj;
        return Objects.equal(this.getName(), other.getName());
    }

    @Override
    public InstanceTypeReq clone()
    {
        InstanceTypeReq clone;

        try
        {
            clone = (InstanceTypeReq) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new InstanceTypeReq().setName(getName()).setNumberOfInstances(getNumberOfInstances());
        }
        return clone;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", this.getName())
                .add("number-of-instances", this.getNumberOfInstances())
                .omitNullValues()
                .toString();
    }

    public InstanceTypeReq setInstanceType(InstanceType type)
    {
        this.instanceType_ = type;
        return this;
    }
    
    public InstanceType getInstanceType()
    {
        return this.instanceType_;
    }
}
