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

import org.excalibur.core.cloud.api.domain.Region;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "virtual-machine-image")
public class VirtualMachineImage implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -2292493335191231557L;

    @XmlAttribute(name = "id")
    private Integer id_;
    
    @XmlElement(name = "architecture", required = true)
    private OsArchitectureType architecture;

    @XmlElement(name = "name", required = true)
    private String name_;

    @XmlElement(name = "region")
    private Region region_;

    @XmlElement(name = "hypervisor", required = true)
    private HypervisorType hypervisor_;
    
    @XmlElement(name = "virtualization-type", required = true)
    private VirtualizationType virtualizationType_;

    @XmlElement(name = "platform", required = true)
    private Platform platform_;

    @XmlElement(name = "endpoint")
    private String endpoint_;
    
    @XmlElement(name = "username")
    private String defaultUsername_;
    
    @XmlElement(name = "description")
    private String description;
    
    @XmlElement(name = "root-volume", required = true)
    private Volume rootVolume_;

    public VirtualMachineImage()
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
    public VirtualMachineImage setId(Integer id)
    {
        this.id_ = id;
        return this;
    }
    
    

    /**
     * @return the architecture
     */
    public OsArchitectureType getArchitecture()
    {
        return architecture;
    }


    /**
     * @param architecture the architecture to set
     */
    public VirtualMachineImage setArchitecture(OsArchitectureType architecture)
    {
        this.architecture = architecture;
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
    public VirtualMachineImage setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the region
     */
    public Region getRegion()
    {
        return region_;
    }

    /**
     * @param region
     *            the region to set
     */
    public VirtualMachineImage setRegion(Region region)
    {
        this.region_ = region;
        return this;
    }

    /**
     * @return the hypervisor
     */
    public HypervisorType getHypervisor()
    {
        return hypervisor_;
    }

    /**
     * @param hypervisor
     *            the hypervisor to set
     */
    public VirtualMachineImage setHypervisor(HypervisorType hypervisor)
    {
        this.hypervisor_ = hypervisor;
        return this;
    }
    
    /**
     * @return the virtualizationType
     */
    public VirtualizationType getVirtualizationType()
    {
        return virtualizationType_;
    }

    /**
     * @param virtualizationType the virtualizationType to set
     */
    public VirtualMachineImage setVirtualizationType(VirtualizationType virtualizationType)
    {
        this.virtualizationType_ = virtualizationType;
        return this;
    }

    /**
     * @return the platform
     */
    public Platform getPlatform()
    {
        return platform_;
    }

    /**
     * @param platform
     *            the platform to set
     */
    public VirtualMachineImage setPlatform(Platform platform)
    {
        this.platform_ = platform;
        return this;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint()
    {
        return endpoint_;
    }

    /**
     * @param endpoint
     *            the endpoint to set
     */
    public VirtualMachineImage setEndpoint(String endpoint)
    {
        this.endpoint_ = endpoint;
        return this;
    }

    /**
     * @return the defaultUsername
     */
    public String getDefaultUsername()
    {
        return defaultUsername_;
    }


    /**
     * @param defaultUsername the defaultUsername to set
     */
    public VirtualMachineImage setDefaultUsername(String defaultUsername)
    {
        this.defaultUsername_ = defaultUsername;
        return this;
    }
    
    


    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public VirtualMachineImage setDescription(String description)
    {
        this.description = description;
        return this;
    }

    /**
     * @return the rootVolumeDevice
     */
    public Volume getRootVolume()
    {
        return rootVolume_;
    }

    /**
     * @param rootVolumeDevice the rootVolume to set
     */
    public VirtualMachineImage setRootVolume(Volume rootVolume)
    {
        this.rootVolume_ = rootVolume;
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

        if (!(obj instanceof VirtualMachineImage))
        {
            return false;
        }

        VirtualMachineImage other = (VirtualMachineImage) obj;

        return (this.getId() != null && Objects.equal(this.getId(), other.getId())) || 
               Objects.equal(this.getName(), other.getName());
    }
}
