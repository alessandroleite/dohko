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
package org.excalibur.core.execution.domain;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static org.apache.commons.lang3.time.DateUtils.truncate;
import static java.util.Calendar.SECOND;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task-status")
public class TaskStatus implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -8839224387149810271L;

	@XmlAttribute(name="id", required = true)
	private String taskId_;
	
	@XmlAttribute(name="name", required = true)
	private String name_;
	
	@XmlElement(name="status", nillable = false, required = true)
	private TaskStatusType type_;
	
	@XmlElement(name="datetime", nillable = false, required = true)
	private Date date_;
	
	@XmlElement(name="worker")
	private String worker_;
	
	@XmlElement(name="pid")
	private Integer pid_;
	
	
	public static TaskStatus runningTaskStatus(String id, String name)
	{
		return newTaskStatus(id, name, TaskStatusType.RUNNING);
	}
	
	public static TaskStatus newTaskStatus(String id, String name, TaskStatusType status)
	{
		return new TaskStatus().setDate(new Date()).setTaskId(id).setTaskName(name).setType(status);
	}
	
	
	/**
	 * Default constructor
	 */
	public TaskStatus() 
	{
		super();
	}
	
	/**
	 * @return the task
	 */
	public String getTaskId() 
	{
		return taskId_;
	}
	
	/**
	 * @param taskId the task to set
	 */
	public TaskStatus setTaskId(String taskId) 
	{
		this.taskId_ = taskId;
		return this;
	}
	
	/**
	 * @return the name
	 */
	public String getTaskName() 
	{
		return name_;
	}

	/**
	 * @param name the name to set
	 */
	public TaskStatus setTaskName(String name) 
	{
		this.name_ = name;
		return this;
	}

	/**
	 * @return the type
	 */
	public TaskStatusType getType() 
	{
		return type_;
	}
	/**
	 * @param type the type to set
	 */
	public TaskStatus setType(TaskStatusType type) 
	{
		this.type_ = type;
		return this;
	}
	/**
	 * @return the date
	 */
	public Date getDate() 
	{
		return date_;
	}
	/**
	 * @param date the date to set
	 */
	public TaskStatus setDate(Date date) 
	{
		this.date_ = date;
		return this;
	}
	/**
	 * @return the worker
	 */
	public String getWorker() 
	{
		return worker_;
	}
	/**
	 * @param worker the worker to set
	 */
	public TaskStatus setWorker(String worker) 
	{
		this.worker_ = worker;
		return this;
	}
	/**
	 * @return the pid
	 */
	public Integer getPid() 
	{
		return pid_;
	}
	/**
	 * @param pid the pid to set
	 */
	public TaskStatus setPid(Integer pid) 
	{
		this.pid_ = pid;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj == this)
		{
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		
		TaskStatus other = (TaskStatus) obj;
		
		return Objects.equal(getTaskId(), other.getTaskId()) &&
			   Objects.equal(getTaskName(), other.getTaskName()) &&
			   Objects.equal(truncate(getDate(), SECOND), truncate(other.getDate(), SECOND)) && 
			   Objects.equal(getType(), other.getType());
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hashCode(getTaskId(), truncate(getDate(), SECOND), getType(), getTaskName());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				.add("task-id", getTaskId())
				.add("task-name", getTaskName())
				.add("type", getType())
				.add("date", getDate())
				.add("pid", getPid())
				.omitNullValues()
				.toString();
	}
	
	@Override
	public TaskStatus clone()  
	{
		TaskStatus clone;
		try 
		{
			clone = (TaskStatus) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new TaskStatus()
					       .setDate(getDate())
					       .setTaskName(getTaskName())
					       .setPid(getPid())
					       .setTaskId(getTaskId())
					       .setType(getType())
					       .setWorker(getWorker());
		}
		
		return clone;
	}
}
