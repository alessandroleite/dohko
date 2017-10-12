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
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.util.YesNoEnum;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static org.excalibur.core.cloud.api.VirtualizationType.*;
import static org.excalibur.core.util.YesNoEnum.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "instance-type")
@XmlType(name = "instance-type", propOrder = 
   { "familyType", "name", "configuration", "provider", "region", "cost", "requiredVirtualizationType", 
     "supportPlacementGroup"})
public class InstanceType implements Serializable, Comparable<InstanceType>, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 325641865551137117L;

    /**
     * The instance id.
     */
    private Integer id_;

    /**
     * The instance type name. Might not be <code>null</code>.
     */
    private String name_;

    /**
     * The {@link InstanceFamilyType} which this {@link InstanceType} belongs to.
     */
    private InstanceFamilyType familyType_;

    /**
     * The {@link Provider} which this instance belongs to.
     */
    private ProviderSupport provider_;

    /**
     * The configuration data of the {@link InstanceType} such as number of CPU/vCPU, number of cores, memory size, and purpose usage.
     */
    private InstanceHardwareConfiguration configuration_;
    
    /**
     * The region where this instance type is available. 
     */
    private Region region_;
    
    /**
     * The price in US dollar of this instance type.
     */
    private BigDecimal cost_;
    
    /**
     * The virtualization type required by this instance type. <code>null</code> means no constraint at all.
     */
    private VirtualizationType requiredVirtualizationType_ = ANY;
    
    /**
     * It indicates if the instanceType support the grouping feature.
     */
    private YesNoEnum supportPlacementGroup_= NO;
    
    public InstanceType()
    {
        super();
        this.configuration_ = new InstanceHardwareConfiguration(this);
    }

    public InstanceType(Integer id)
    {
        this();
        this.id_ = id;
    }

    public InstanceType(Integer id, String name)
    {
        this(id);
        this.name_ = name;
    }

    public static InstanceType valueOf(String instanceType)
    {
        return new InstanceType().setName(instanceType);
    }

    public InstanceType setProvider(ProviderSupport provider)
    {
        this.provider_ = provider;
        return this;
    }

    /**
     * @return the id
     */
    @XmlTransient
    public Integer getId()
    {
        return id_;
    }

    public InstanceType setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the name
     */
    @XmlElement(name = "name", nillable = false)
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public InstanceType setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the properties
     */
    @XmlElement(name = "hardware-configuration", nillable = false)
    public InstanceHardwareConfiguration getConfiguration()
    {
        return configuration_;
    }

    /**
     * Assigns the details data about this instance type.
     * 
     * @param configuration
     */
    public InstanceType setConfiguration(InstanceHardwareConfiguration configuration)
    {
        this.configuration_ = configuration;

        if (configuration != null)
        {
            configuration.setVmType(this);
        }
        return this;
    }

    /**
     * @return the provider
     */
    @XmlElement(name = "provider")
    public ProviderSupport getProvider()
    {
        return provider_;
    }
    
    @XmlElement(name = "region", required = true)
    public Region getRegion()
    {
        return this.region_;
    }
    
    /**
     * @param region the region_ to set
     */
    public InstanceType setRegion(Region region)
    {
        this.region_ = region;
        return this;
    }

    /**
     * @return the cost
     */
    @XmlElement(name = "cost", required = true)
    public BigDecimal getCost()
    {
        return cost_;
    }

    /**
     * @param cost the cost to set
     */
    public InstanceType setCost(BigDecimal cost)
    {
        this.cost_ = cost;
        return this;
    }

    /**
     * @return the familyType
     */
    @XmlElement(name = "family-type", nillable = false)
    public InstanceFamilyType getFamilyType()
    {
        return familyType_;
    }

    /**
     * @param familyType the familyType to set
     */
    public InstanceType setFamilyType(InstanceFamilyType familyType)
    {
        this.familyType_ = familyType;
        return this;
    }
    
    /**
     * @return the requiredVirtualizationType
     */
    @XmlElement(name="required-virtualization-type")
    public VirtualizationType getRequiredVirtualizationType()
    {
        return requiredVirtualizationType_;
    }

    /**
     * @param requiredVirtualizationType the requiredVirtualizationType to set
     */
    public InstanceType setRequiredVirtualizationType(VirtualizationType requiredVirtualizationType)
    {
        this.requiredVirtualizationType_ = requiredVirtualizationType;
        return this;
    }

    
    /**
     * @return the supportPlacementGroup
     */
    @XmlElement(name = "support-placement-group", defaultValue = "false", nillable=false)
    public YesNoEnum getSupportPlacementGroup()
    {
        return supportPlacementGroup_ == null ? YesNoEnum.NO: this.supportPlacementGroup_;
    }

    /**
     * @param supportPlacementGroup the supportPlacementGroup to set
     */
    public InstanceType setSupportPlacementGroup(YesNoEnum supportPlacementGroup)
    {
        this.supportPlacementGroup_ = supportPlacementGroup;
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

        if (!(obj instanceof InstanceType))
        {
            return false;
        }

        InstanceType other = (InstanceType) obj;
        
        return Objects.equal(this.getId(), other.getId());
    }

    @Override
    public int compareTo(InstanceType o)
    {
        return this.id_.compareTo(o.getId());
    }
    
    @Override
    public InstanceType clone()
    {
        InstanceType type;
        try
        {
            type = (InstanceType) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            type = new InstanceType()
                    .setConfiguration(configuration_)
                    .setCost(getCost())
                    .setFamilyType(getFamilyType())
                    .setName(getName())
                    .setProvider(getProvider())
                    .setRegion(getRegion())
                    .setRequiredVirtualizationType(getRequiredVirtualizationType())
                    .setSupportPlacementGroup(this.getSupportPlacementGroup());
        }
        
        return type;
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("family", getFamilyType())
                .add("region", getRegion())
                .add("cost", getCost())
                .add("hardware", getConfiguration())
                .add("support-placement-grouping", getSupportPlacementGroup())
                .add("required-virtualization-type", getRequiredVirtualizationType())
                .omitNullValues()
                .toString();
    }
}
