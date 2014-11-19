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
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="instance-volume")
public class InstanceVolume implements Serializable, Cloneable, Comparable<InstanceVolume>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6234637027754117397L;

    @XmlAttribute(name = "id")
    private Integer id_;

    @XmlElement(name = "volume", nillable = false)
    private Volume volume_;

    @XmlTransient
    private VirtualMachine instance_;

    @XmlElement(name = "device", required = true, nillable=false)
    private String device_;
    
    @XmlElement(name = "device-type", required = true, nillable=false)
    private StorageType type_;

    public InstanceVolume()
    {
        super();
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
    public InstanceVolume setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the volume
     */
    public Volume getVolume()
    {
        return volume_;
    }

    /**
     * @param volume
     *            the volume to set
     */
    public InstanceVolume setVolume(Volume volume)
    {
        this.volume_ = volume;
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
    public InstanceVolume setInstance(VirtualMachine instance)
    {
        this.instance_ = instance;
        return this;
    }

    /**
     * @return the device
     */
    public String getDevice()
    {
        return device_;
    }

    /**
     * @param device
     *            the device to set
     */
    public InstanceVolume setDevice(String device)
    {
        this.device_ = device;
        return this;
    }
    
    
    /**
     * @return the type
     */
    public StorageType getType()
    {
        return type_;
    }

    /**
     * @param type the type to set
     */
    public InstanceVolume setType(StorageType type)
    {
        this.type_ = type;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId(), this.getInstance(), this.getVolume());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof InstanceVolume))
        {
            return false;
        }

        InstanceVolume other = (InstanceVolume) obj;

        return Objects.equal(this.getId(), other.getId()) || Objects.equal(this.getInstance(), other.getInstance())
                || Objects.equal(this.getVolume(), other.getVolume());

    }

    @Override
    public int compareTo(InstanceVolume that)
    {
        if (this.equals(that))
        {
            return this.getDevice().compareTo(that.getDevice());
        }
        
        return -1;
    }
}
