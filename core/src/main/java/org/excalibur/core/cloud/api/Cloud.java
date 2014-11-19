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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.excalibur.core.cloud.api.domain.Region;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cloud")
public class Cloud implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 838925016499533524L;

    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "provider")
    private ProviderSupport provider_;

    @XmlElement(name = "access-key", required = true, nillable = false)
    private AccessKey accessKey_;

    @XmlElementWrapper(name = "regions")
    @XmlElement(name = "region")
    private final List<Region> regions_ = new ArrayList<Region>();

    @XmlElement(name="instance-types")
    private final InstanceTypeReqs instanceTypes_ = new InstanceTypeReqs();

    public Cloud addInstanceType(InstanceTypeReq instanceType)
    {
        this.instanceTypes_.add(instanceType);
        return this;
    }

    public Cloud removeInstanceType(InstanceTypeReq instanceType)
    {
        this.instanceTypes_.remove(instanceType);

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
     * @param accessKey
     *            the accessKey to set
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
    public InstanceTypeReqs getInstanceTypes()
    {
        return this.instanceTypes_;
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

        if (!(obj instanceof Cloud))
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
        return toStringHelper(this)
                .add("name", getName())
                .add("provider", getProvider())
                .add("access-key", this.getAccessKey())
                .add("regions", getRegions())
                .omitNullValues()
                .toString();
    }
    
    @Override
    public Cloud clone() 
    {
        Cloud cloned;
        
        try
        {
            cloned = (Cloud) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new Cloud().setAccessKey(this.accessKey_.clone()).setName(this.getName()).setProvider(this.provider_.clone());
        }
        
        cloned.regions_.clear();
        cloned.instanceTypes_.clear();
        
        for (Region region: this.regions_)
        {
            cloned.addRegion(region.clone());
        }
        
        for (InstanceTypeReq req: instanceTypes_)
        {
            cloned.addInstanceType(req.clone());
        }
        
        return cloned;
    }
}
