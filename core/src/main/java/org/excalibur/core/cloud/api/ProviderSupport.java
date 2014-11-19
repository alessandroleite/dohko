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

import org.excalibur.core.cloud.api.domain.Services;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "provider")
public class ProviderSupport implements Provider, Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -285924487384389211L;

    @XmlTransient
//    @XmlAttribute(name = "id")
    private Integer id_;

    @XmlElement(name = "name")
    private String name_;

    @XmlElement(name = "max-resource-per-type")
    private Integer limitOfResourcesPerType_;

    @XmlElement(name = "description")
    private String description_;

    @XmlElement(name = "service-class")
    private String serviceClass_;

    public ProviderSupport()
    {
    }

    public ProviderSupport(String name)
    {
        this.name_ = name;
    }

    @Override
    public Integer getId()
    {
        return id_;
    }

    @Override
    public String getName()
    {
        return name_;
    }

    @Override
    public String getDescription()
    {
        return description_;
    }

    @Override
    public Integer getLimitOfResourcesPerType()
    {
        return limitOfResourcesPerType_;
    }

    @Override
    public Services getServices()
    {
        return new Services();
    }

    /**
     * @return the serviceClass_
     */
    public String getServiceClass()
    {
        return serviceClass_;
    }

    /**
     * @param id_
     *            the id_ to set
     */
    public ProviderSupport setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @param name
     *            the name_ to set
     */
    public ProviderSupport setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @param limitOfResourcesPerType
     *            the limitOfResourcesPerType_ to set
     */
    public ProviderSupport setLimitOfResourcesPerType(Integer limitOfResourcesPerType)
    {
        this.limitOfResourcesPerType_ = limitOfResourcesPerType;
        return this;
    }

    /**
     * @param description
     *            the description to set
     */
    public ProviderSupport setDescription(String description)
    {
        this.description_ = description;
        return this;
    }

    /**
     * @param serviceClass_
     *            the serviceClass_ to set
     */
    public ProviderSupport setServiceClass(String serviceClass)
    {
        this.serviceClass_ = serviceClass;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId(), this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ProviderSupport))
        {
            return false;
        }

        ProviderSupport other = (ProviderSupport) obj;

        return (this.getId() != null && Objects.equal(this.getId(), other.getId())) || 
                Objects.equal(this.getName(), other.getName());
    }
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("name", this.getName())
                .add("description", this.getDescription())
                .add("max-instances", this.getLimitOfResourcesPerType())
                .omitNullValues()
                .toString();
    }

    @Override
    public ProviderSupport clone()
    {
        ProviderSupport cloned;
        try
        {
            cloned = (ProviderSupport) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new ProviderSupport()
                    .setDescription(this.getDescription())
                    .setLimitOfResourcesPerType(this.getLimitOfResourcesPerType())
                    .setName(this.getName()).
                    setServiceClass(this.getServiceClass());
        }

        return cloned;
    }

}
