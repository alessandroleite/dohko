package org.excalibur.core.execution.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="task-resource-usage")
@XmlType(name="task-resource-usage")
public class TaskResourceUsage implements Serializable, Cloneable 
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization.
	 */
	private static final long serialVersionUID = -2580264763642319293L;

	@XmlTransient
	private Integer id;
	
	@XmlElement(name="task-id", nillable = false, required = true)
	private String taskId;
	
	@XmlElement(name="resource-type", nillable = false, required = true)
	private ResourceType resourceType;
	
	@XmlElement(name="pid", nillable = false, required = true)
	private Integer pid;
	
	@XmlElement(name="datetime", nillable = false, required = true)
	private Date datetime;
	
	@XmlElement(name="value", nillable = false, required = true)
	private BigDecimal value;

	/**
	 * @return the id
	 */
	public Integer getId() 
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public TaskResourceUsage setId(Integer id) 
	{
		this.id = id;
		return this;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() 
	{
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public TaskResourceUsage setTaskId(String taskId) 
	{
		this.taskId = taskId;
		return this;
	}

	/**
	 * @return the resourceType
	 */
	public ResourceType getResourceType() 
	{
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public TaskResourceUsage setResourceType(ResourceType resourceType) 
	{
		this.resourceType = resourceType;
		return this;
	}

	/**
	 * @return the pid
	 */
	public Integer getPid() 
	{
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public TaskResourceUsage setPid(Integer pid) 
	{
		this.pid = pid;
		return this;
	}

	/**
	 * @return the datetime
	 */
	public Date getDatetime() 
	{
		return datetime;
	}

	/**
	 * @param datetime the datetime to set
	 */
	public TaskResourceUsage setDatetime(Date datetime) 
	{
		this.datetime = datetime;
		return this;
	}

	/**
	 * @return the value
	 */
	public BigDecimal getValue() 
	{
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public TaskResourceUsage setValue(BigDecimal value) 
	{
		this.value = value;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null || this.getClass() != obj.getClass())
		{
			return false;
		}
		
		TaskResourceUsage other = (TaskResourceUsage)obj;
		
		return Objects.equal(getTaskId(), other.getTaskId()) &&
			   Objects.equal(getResourceType(), other.getResourceType()) &&
			   Objects.equal(getPid(), other.getPid()) &&
			   Objects.equal(getDatetime(), other.getDatetime());
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hashCode(getTaskId(), getResourceType(), getPid(), getDatetime());
	}
	
	@Override
	public TaskResourceUsage clone() 
	{
		TaskResourceUsage clone;
		
		try 
		{
			clone = (TaskResourceUsage) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new TaskResourceUsage()
					.setDatetime(getDatetime())
					.setId(getId()).setPid(getPid())
					.setResourceType(getResourceType())
					.setTaskId(getTaskId())
					.setValue(getValue());
		}
		
		return clone;
	}
}
