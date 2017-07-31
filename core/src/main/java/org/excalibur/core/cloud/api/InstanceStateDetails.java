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

import static com.google.common.base.Objects.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.excalibur.core.util.DateUtils2;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="instance-state")
public class InstanceStateDetails implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -2674747359845703922L;

    @XmlAttribute(name = "id")
    private Integer           id_;
    
    @XmlTransient
    private VirtualMachine    instance_;
    
    @XmlElement(name = "state")
    private InstanceStateType state_;
    
    @XmlElement(name = "time")
    private Date              time_;
    
    @XmlElement(name = "description")
    private String            description_;
    
    public InstanceStateDetails()
    {
        this(null, new Date());
    }
    
    public InstanceStateDetails(InstanceStateType state, Date time)
    {
        this.state_ = state;
        this.time_ = time;
    }
    
    public InstanceStateDetails(VirtualMachine instance, InstanceStateType state, Date time)
    {
        this(state, time);
        this.instance_ = instance;
    }
    
    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @return the vm
     */
    public VirtualMachine getInstance()
    {
        return instance_;
    }

    /**
     * @param vm the vm to set
     */
    public InstanceStateDetails setInstance(VirtualMachine vm)
    {
        this.instance_ = vm;
        return this;
    }

    /**
     * @return the state
     */
    public InstanceStateType getState()
    {
        return state_;
    }

    /**
     * @param state the state to set
     */
    public InstanceStateDetails setState(InstanceStateType state)
    {
        this.state_ = state;
        return this;
    }

    /**
     * @return the time
     */
    public Date getTime()
    {
        return time_;
    }

    /**
     * @param time the time to set
     */
    public InstanceStateDetails setTime(Date time)
    {
        this.time_ = time;
        return this;
    }

    /**
     * @return the id_
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id_ the id_ to set
     */
    public InstanceStateDetails setId(Integer id)
    {
        this.id_ = id;
        return this;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return false;
        }
        
        if (!(obj instanceof InstanceStateDetails))
        {
            return false;
        }
        
        InstanceStateDetails other = (InstanceStateDetails) obj;
        
        
        return equal(this.getState(),    other.getState())  && 
               equal(this.getInstance(), other.getInstance()) &&
               DateUtils2.equal(this.getTime(), other.getTime());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.getState(), this.getInstance(), getTime());
    }
    
    @Override
    public String toString()
    {
        return toStringHelper(this)
                  .add("instance", getInstance())
                  .add("state", getState())
                  .add("time", DateUtils2.toUTC(getTime()))
                  .omitNullValues()
                  .toString();
    }
    
    @Override
    protected InstanceStateDetails clone() 
    {
    	InstanceStateDetails clone;
    	
		try 
		{
			clone = (InstanceStateDetails) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new InstanceStateDetails()
					.setId(getId())
					.setInstance(getInstance())
					.setState(getState())
					.setTime(getTime());
		}
    	
    	return clone;
    }
}
