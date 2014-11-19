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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "volume-type")
@XmlType(name = "volume-type", propOrder = {"provider_", "name_", "minSizeGb_", "maxSizeGb_", "minIops_", "maxIops_"})
public class VolumeType implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6951113236388896651L;

    @XmlAttribute(name = "id")
    private Integer id_;

    @XmlElement(name = "provider", nillable = false, required = true)
    private ProviderSupport provider_;

    @XmlElement(name = "name", required = true)
    private String name_;

    @XmlElement(name = "min-size-gb", required = true, nillable = false)
    private Integer minSizeGb_;

    @XmlElement(name = "max-size-gb")
    private Integer maxSizeGb_;

    @XmlElement(name = "min-iops", required = true, nillable = false)
    private Integer minIops_;

    @XmlElement(name = "max-iops")
    private Integer maxIops_;

    public VolumeType()
    {
        super();
    }

    public VolumeType(Integer id)
    {
        this.id_ = id;
    }

    public static VolumeType valueOf(Integer id)
    {
        return new VolumeType(id);
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
    public VolumeType setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the provider
     */
    public ProviderSupport getProvider()
    {
        return provider_;
    }

    /**
     * @param provider
     *            the provider to set
     */
    public VolumeType setProvider(ProviderSupport provider)
    {
        this.provider_ = provider;
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
    public VolumeType setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the minSizeGb
     */
    public Integer getMinSizeGb()
    {
        return minSizeGb_;
    }

    /**
     * @param minSizeGb
     *            the minSizeGb to set
     */
    public VolumeType setMinSizeGb(Integer minSizeGb)
    {
        this.minSizeGb_ = minSizeGb;
        return this;
    }

    /**
     * @return the maxSizeGb
     */
    public Integer getMaxSizeGb()
    {
        return maxSizeGb_;
    }

    /**
     * @param maxSizeGb
     *            the maxSizeGb to set
     */
    public VolumeType setMaxSizeGb(Integer maxSizeGb)
    {
        this.maxSizeGb_ = maxSizeGb;
        return this;
    }

    /**
     * @return the minIops
     */
    public Integer getMinIops()
    {
        return minIops_;
    }

    /**
     * @param minIops
     *            the minIops to set
     */
    public VolumeType setMinIops(Integer minIops)
    {
        this.minIops_ = minIops;
        return this;
    }

    /**
     * @return the maxIops
     */
    public Integer getMaxIops()
    {
        return maxIops_;
    }

    /**
     * @param maxIops
     *            the maxIops to set
     */
    public VolumeType setMaxIops(Integer maxIops)
    {
        this.maxIops_ = maxIops;
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
        
        if (!(obj instanceof VolumeType))
        {
            return false;
        }
        
        VolumeType other = (VolumeType) obj;
        
        return Objects.equal(this.getId(), other.getId());
    }
}
