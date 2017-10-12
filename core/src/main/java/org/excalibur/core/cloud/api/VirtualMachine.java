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
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "instance")
@XmlType(name = "instance")
public class VirtualMachine implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = -8043871280369519669L;

    @XmlElement(name = "id")
    private Integer id_;

    @XmlElement(name = "instance-name")
    private String name_;

    @XmlElement(name = "image-id")
    private String imageId_;

    @XmlElement(name = "instance-type")
    private InstanceType type_;
    
    @XmlElement(name = "zone")
    private Zone location_;

    // TODO move the attributes: placement, launchTime, and all the configuration's attributes to class InstanceStateDetails
    @XmlElement(name = "instance-state")
    private InstanceStateDetails state_;

    @XmlElement(name = "cost")
    private BigDecimal cost_;
    
    @XmlElement(name = "disks")
    private final Volumes volumes_ = new Volumes(this);
    
    /**
     * The Base64-encoded MIME user data.
     */
    @XmlElement(name="user-data")
    private String userData_;

    /**
     * The time this VM was launched.
     */
    @XmlElement(name = "instance-launch-time")
    private Date launchTime_;

    /**
     * Logical grouping for your cluster instances. For instance, in EC2, placement group have low latency, full bisection 10 Gbps bandwidth between
     * instances.
     */
    @XmlElement(name = "instance-group")
    private Placement placement_ = new Placement();

    /**
     */
    @XmlElement(name = "instance-configuration")
    private VmConfiguration configuration_;

//    /**
//     * Monitoring service for this VM.
//     */
//    // @XmlAttribute(name = "instance-monitoring-service")
//    @XmlTransient
//    private Monitoring monitoring_;

    /**
     * Instance's owner. Might not be <code>null</code>.
     */
    @XmlElement(name = "owner")
    private User owner_;
    
    @XmlElement(name = "tags")
    private final Tags tags_ = new Tags();

    public VirtualMachine()
    {
        super();
    }

    public VirtualMachine(Integer id)
    {
        this.id_ = id;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id_
     *            the id_ to set
     */
    public VirtualMachine setId(Integer id)
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
    public VirtualMachine setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the image
     */
    public String getImageId()
    {
        return imageId_;
    }

    /**
     * @param image
     *            the image to set
     */
    public VirtualMachine setImageId(String image)
    {
        this.imageId_ = image;
        return this;
    }

    /**
     * @return the type
     */
    public InstanceType getType()
    {
        return type_;
    }

    /**
     * @param type
     *            the type to set
     */
    public VirtualMachine setType(InstanceType type)
    {
        this.type_ = type;
        return this;
    }

    /**
     * @return the state
     */
    public InstanceStateDetails getState()
    {
        return state_;
    }

    /**
     * @param state
     *            the state to set
     */
    public VirtualMachine setState(InstanceStateDetails state)
    {
        this.state_ = state;

        if (this.state_ != null)
        {
            state.setInstance(this);
        }

        return this;
    }

    /**
     * @return the lauchTime
     */
    public Date getLaunchTime()
    {
        return launchTime_;
    }

    /**
     * @param lauchTime
     *            the lauchTime to set
     */
    public VirtualMachine setLaunchTime(Date lauchTime)
    {
        this.launchTime_ = lauchTime;
        return this;
    }

    /**
     * @return the placement
     */
    public Placement getPlacement()
    {
        return placement_;
    }

    /**
     * @param placement
     *            the placement to set
     */
    public VirtualMachine setPlacement(Placement placement)
    {
        this.placement_ = placement;
        return this;
    }

    /**
     * @return the configuration
     */
    public VmConfiguration getConfiguration()
    {
        return configuration_;
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public VirtualMachine setConfiguration(VmConfiguration configuration)
    {
        this.configuration_ = configuration;
        return this;
    }
    
    

//    /**
//     * @return the monitoring
//     */
//    public Monitoring getMonitoring()
//    {
//        return monitoring_;
//    }
//
//    /**
//     * @param monitoring
//     *            the monitoring to set
//     */
//    public VirtualMachine setMonitoring(Monitoring monitoring)
//    {
//        this.monitoring_ = monitoring;
//        return this;
//    }

    /**
     * @return the userData
     */
    public String getUserData()
    {
        return userData_;
    }

    /**
     * @param userData the userData to set
     */
    public VirtualMachine setUserData(String userData)
    {
        this.userData_ = userData;
        return this;
    }

    /**
     * @return the owner
     */
    public User getOwner()
    {
        return owner_;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public VirtualMachine setOwner(User owner)
    {
        this.owner_ = owner;
        return this;
    }

    /**
     * @return the location
     */
    public Zone getLocation()
    {
        return location_;
    }

    /**
     * @param location
     *            the location to set
     */
    public VirtualMachine setLocation(Zone location)
    {
        this.location_ = location;
        return this;
    }

    /**
     * @return the cost
     */
    public BigDecimal getCost()
    {
        return cost_ == null ? BigDecimal.ZERO : cost_;
    }

    /**
     * @param cost the cost to set
     */
    public VirtualMachine setCost(BigDecimal cost)
    {
        this.cost_ = cost;
        return this;
    }
    

    /**
     * @return the volumes
     */
    public Volumes getVolumes()
    {
        return volumes_;
    }
    
    public VirtualMachine setTags(Tags tags)
    {
        tags.copyFrom(tags);
        return this;
    }

    /**
     * @return the tags
     */
    public Tags getTags()
    {
        return tags_;
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
        
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        
        VirtualMachine other = (VirtualMachine) obj;
        return equal (this.getName(), other.getName()) || 
               (this.getId() != null && equal(this.getId(), other.getId()));
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("image", getImageId())
                .add("name", getName())
                .add("type", getType())
                .add("location", getLocation())
                .omitNullValues()
                .toString();
    }
    
    @Override
    public VirtualMachine clone()  
    {
    	VirtualMachine clone;
    	
		try 
		{
			clone = (VirtualMachine) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new VirtualMachine()
					     .setConfiguration(configuration_ != null ? configuration_.clone() : null)
					     .setCost(getCost())
					     .setId(getId())
					     .setImageId(getImageId())
					     .setLaunchTime(getLaunchTime())
					     .setLocation(getLocation() != null ? getLocation().clone() : null)
					     .setName(getName())
					     .setOwner(getOwner() != null ? getOwner().clone() : null)
					     .setPlacement(getPlacement() != null ? getPlacement().clone(): null)
					     .setState(getState() != null ? getState().clone() : null)
					     .setTags(getTags() != null ? getTags().clone(): null)
					     .setType(getType() != null ? getType().clone(): null)
					     .setUserData(getUserData());			
		}
    	
    	return clone;
    }
}
