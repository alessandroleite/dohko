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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import io.airlift.command.ProcessCpuState;
import io.airlift.command.ProcessMemoryState;

import static org.excalibur.core.util.CloneIterableFunction.cloneIterable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task-stats")
@XmlType(name = "task-stats", propOrder = { "id_", "cpu_", "memory_" })
public class TaskStats implements Serializable, Cloneable
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization
	 */
	private static final long serialVersionUID = 6293848231081886774L;

	@XmlElement(name = "id")
	private String id_;
	
	@XmlElement(name = "cpu")
	private final List<ProcessCpuState> cpu_ = new ArrayList<>();
	
	@XmlElement(name = "memory")
	private final List<ProcessMemoryState> memory_ = new ArrayList<>();
	
	public TaskStats()
	{
		super();
	}

	public TaskStats(String id, Iterable<ProcessCpuState> cpu, Iterable<ProcessMemoryState> memory) 
	{
		this.id_ = id;
		
		if (cpu != null)
		{
			cpu.forEach(cpu_::add);
		}
		
		if (memory != null)
		{
			memory.forEach(memory_::add);
		}
	}

	/**
	 * @return the id
	 */
	public String getId() 
	{
		return id_;
	}

	/**
	 * @param id the id to set
	 */
	public TaskStats setId(String id) 
	{
		this.id_ = id;
		return this;
	}

	/**
	 * @return the cpu
	 */
	public ImmutableList<ProcessCpuState> getCpu() 
	{
		return ImmutableList.copyOf(cpu_);
	}

	/**
	 * @return the memory
	 */
	public ImmutableList<ProcessMemoryState> getMemory() 
	{
		return ImmutableList.copyOf(memory_);
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				          .add("id", getId())
				          .add("cpu", getCpu())
				          .add("memory", getMemory())
				          .omitNullValues()
				          .toString();
	}
	
	@Override
	protected TaskStats clone() 
	{
		TaskStats clone;
		
		try 
		{
			clone = (TaskStats) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new TaskStats().setId(getId());
		}
		
		cloneIterable(cpu_).forEach(clone.cpu_::add);
		cloneIterable(memory_).forEach(clone.memory_::add);
		
		return clone;
	}
}
