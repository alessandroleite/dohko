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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.util.CloneIterableFunction;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Joiner.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cloud")
@XmlType(name = "cloud", propOrder = {"name_", "provider_", "accessKey_", "regions_", "instanceTypes_"})
public class Cloud implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -9193201775910410860L;

    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "provider")
    private ProviderSupport provider_;

    @XmlElement(name = "access-key", required = true, nillable = false)
    private AccessKey accessKey_;

    @XmlElement(name = "regions")
    private final List<Region> regions_ = new ArrayList<Region>();

    @XmlElement(name="instance-types")
//    private final InstanceTypeReqs instanceTypes_ = new InstanceTypeReqs();
    private final List<InstanceTypeReq> instanceTypes_ = new ArrayList<>();

    public Cloud addInstanceType(InstanceTypeReq instanceType)
    {
        instanceTypes_.add(instanceType);
        return this;
    }

    public Cloud removeInstanceType(InstanceTypeReq instanceType)
    {
        instanceTypes_.remove(instanceType);

        return this;
    }
    
    public Cloud addInstancesTypes(Iterable<InstanceTypeReq> instanceTypes)
    {
    	if (instanceTypes != null)
    	{
    		instanceTypes.forEach(this::addInstanceType);
    	}
    	
    	return this;
    }

    public Cloud addAllInstanceTypes(InstanceTypeReq... instanceTypes)
    {
        if (instanceTypes != null && instanceTypes.length > 0)
        {
            for (InstanceTypeReq instanceType : instanceTypes)
            {
                this.addInstanceType(instanceType);
            }
        }
        return this;
    }

    public Cloud addRegion(Region region)
    {
        if (region != null)
        {
            this.regions_.add(region);
        }

        return this;
    }

    public Cloud removeRegion(Region region)
    {
        this.regions_.remove(region);

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
    public Cloud setName(String name)
    {
        this.name_ = name;
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
    public Cloud setProvider(ProviderSupport provider)
    {
        this.provider_ = provider;
        return this;
    }

    /**
     * @return the accessKey
     */
    public AccessKey getAccessKey()
    {
        return accessKey_;
    }

    /**
     * @param accessKey the accessKey to set
     */
    public Cloud setAccessKey(AccessKey accessKey)
    {
        this.accessKey_ = accessKey;
        return this;
    }

    /**
     * @return the regions
     */
    public List<Region> getRegions()
    {
        return Collections.unmodifiableList(regions_);
    }
    

    /**
     * @return the instanceTypes
     */
    public List<InstanceTypeReq> getInstanceTypes()
    {
        return Collections.unmodifiableList(instanceTypes_);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName(), this.getProvider());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        Cloud other = (Cloud) obj;
        return equal(this.getName(), other.getName()) || 
               equal(this.getProvider(), other.getProvider());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("provider", getProvider())
                .add("access-key", getAccessKey())
                .add("regions", on(",").join(getRegions()))
                .omitNullValues()
                .toString();
    }
    
    @Override
    public Cloud clone() 
    {
        Cloud clone;
        
        try
        {
            clone = (Cloud) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Cloud()
            		.setAccessKey(accessKey_.clone())
            		.setName(getName())
            		.setProvider(provider_.clone());
        }
        
        clone.setAccessKey(accessKey_.clone())
              .setProvider(provider_.clone());
        
        clone.regions_.clear();
        clone.instanceTypes_.clear();
        
        new CloneIterableFunction<Region>().apply(regions_).forEach(clone::addRegion);
        new CloneIterableFunction<InstanceTypeReq>().apply(instanceTypes_).forEach(clone::addInstanceType);
        
        return clone;
    }
}
