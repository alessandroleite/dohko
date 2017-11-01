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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.util.CloneIterableFunction;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="job-status")
@XmlType(name = "job-status", propOrder = { "id", "name", "tasksStatuses" })
public class JobStatus implements Cloneable, Serializable
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization.
	 */
	private static final long serialVersionUID = 8443794836420284375L;

	@XmlElement(name = "id")
	private String id;
	
	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "tasks")
	private final List<TaskStatus> tasksStatuses = new ArrayList<>();
	
	public JobStatus()
	{
		super();
	}
	
	public JobStatus(String id) 
	{
		this.id = id;
	}
	
	public JobStatus(String id, String name)
	{
		this(id);
		this.name = name;
	}
	
	public JobStatus(String id, Iterable<TaskStatus> statuses) 
	{
		this(id, null, statuses);
	}
	
	public JobStatus(String id, String name, Iterable<TaskStatus> statuses)
	{
		this(id, name);
		
		if (statuses != null)
		{
			addAllTaskStatus(statuses);
		}
	}
	

	public JobStatus addTaskStatus(TaskStatus status)
	{
		if (status != null)
		{
			tasksStatuses.add(status);
		}
		
		return this;
	}
	
	public JobStatus addAllTaskStatus(Iterable<TaskStatus> statuses)
	{
		statuses.forEach(this::addTaskStatus);
		
		return this;
	}

	/**
	 * @return the id
	 */
	public String getId() 
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public JobStatus setId(String id) 
	{
		this.id = id;
		return this;
	}
	
	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public JobStatus setName(String name) 
	{
		this.name = name;
		return this;
	}

	/**
	 * @return the tasksStatus
	 */
	public ImmutableList<TaskStatus> getTasksStatus() 
	{
		return statuses();
	}
	
	public ImmutableList<TaskStatus> statuses()
	{
		return ImmutableList.copyOf(tasksStatuses);
	}
	
	@Override
	protected JobStatus clone() 
	{
		JobStatus clone;
		
		try 
		{
			clone = (JobStatus) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new JobStatus(id, CloneIterableFunction.cloneIterable(tasksStatuses));
		}
		
		return clone;
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
		
		JobStatus other = (JobStatus) obj;
		
		return Objects.equals(getId(), other.getId()) && 
			   Objects.equals(getName(), other.getName());
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(getId(), getName());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				.add("id", getId())
				.add("name", getName())
				.add("tasks", tasksStatuses)
				.omitNullValues()
				.toString();
	}
}
