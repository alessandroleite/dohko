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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import net.vidageek.mirror.dsl.Mirror;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="task-output")
@XmlRootElement(name="task-output")
public class TaskOutput implements Serializable, Cloneable 
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization.
	 */
	private static final long serialVersionUID = -2703320117260208775L;

	@XmlAttribute(name = "id")
	private String id;
	
	@XmlElement(name = "task_id", nillable = false, required = true)
	private String taskId;
	
	@XmlElement(name="type", nillable = false, required = true)
	private TaskOutputType type;
	
	@XmlElement(name="value")
	private Serializable value;
	
	public TaskOutput()
	{
		super();
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
	public TaskOutput setId(String id) 
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
	public TaskOutput setTaskId(String taskId) 
	{
		this.taskId = taskId;
		return this;
	}



	/**
	 * @return the type
	 */
	public TaskOutputType getType() 
	{
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public TaskOutput setType(TaskOutputType type) 
	{
		this.type = type;
		return this;
	}


	/**
	 * @return the value
	 */
	public Serializable getValue() 
	{
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public TaskOutput setValue(Serializable value) 
	{
		this.value = value;
		return this;
	}


	@Override
	protected TaskOutput clone() 
	{
		TaskOutput clone;

		try 
		{
			clone = (TaskOutput) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new TaskOutput()
					.setId(getId())
					.setTaskId(getTaskId())
					.setType(getType())
					.setValue((Serializable) new Mirror().on(getType()).invoke().method("clone").withoutArgs());
		}

		return clone;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj == this)
		{
			return true;
		}
		
		if (obj == null || !getClass().equals(obj.getClass()))
		{
			return false;
		}
		
		final TaskOutput other = (TaskOutput)obj;
		
		return Objects.equal(getId(), other.getId())   && 
			   Objects.equal(getTaskId(), getTaskId()) &&
			   Objects.equal(getType(), other.getType());
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hashCode(getId(), getTaskId(), getType());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				.add("id", getId())
				.add("task-id", getTaskId())
				.add("type", getType())
				.omitNullValues()
				.toString();
	}
}
