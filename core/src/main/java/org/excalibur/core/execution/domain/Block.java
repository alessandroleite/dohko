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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.util.CloneIterableFunction;
import org.excalibur.core.util.Lists2;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Lists.*;

/**
 * A {@link Block} represents a set of tasks that might be executed in any order. Therefore, the ordering of a block must be respected.  
 * A {@link Block} may have one or more parents. 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="block")
@XmlType(name="block", propOrder = {"id_", "name_", "parents_", "repeat_", "applications_"})
public class Block implements Serializable, Cloneable, Iterable<Application>, Comparable<Block>, Comparator<Block>
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization
	 */
	private static final long serialVersionUID = -2045058945696994770L;

	/**
	 * It must be unique in an application descriptor.
	 */
	@XmlElement(name = "id", required = true)
	private String id_;
	
	@XmlElement(name = "name")
	private String name_;
	
	/**
	 * A comma separated value of the parents' ids. 
	 */
	@XmlElement(name="parents", required = false, nillable = true)
	private String parents_;
	
	
	@XmlElement(name="repeat", required = false, defaultValue = "0")
	private Integer repeat_;
	
	@XmlElement(name="applications")
	private final List<Application> applications_ = newArrayList();
	
	public Block()
	{
		super();
	}
	
	public Block(List<Application> applications)
	{
		applications_.addAll(applications);
	}
	
	public Block addApplication(Application app)
	{
		if (app != null && !applications_.contains(app))
		{
			applications_.add(app);
		}
		
		return this;
	}
	
	public Block addApplications(Iterable<Application> applications)
	{
		if (applications != null)
		{
			applications.forEach(this::addApplication);
		}
		
		return this;
	}
	
	public Block addApplications(Application ...applications)
	{
		if (applications != null)
		{
			for (Application application : applications) 
			{
				addApplication(application);
			}
		}
		
		return this;
	}
	
	public Block removeApplication(Application app)
	{
		applications_.remove(app);
		return this;
	}
	
	public Block removeApplication(int index)
	{
		if (!applications_.isEmpty() && Lists2.isInRage(index, applications_.size()))
		{
			applications_.remove(index);
		}
		
		return this;
	}
	
	public Optional<Application> getApplication(int index)
	{
		if (!applications_.isEmpty() && Lists2.isInRage(index, applications_.size()))
		{
			return Optional.of(applications_.get(index));
		}
		
		return Optional.absent(); 
	}
	
	/**
	 * Returns a read-only view of the applications
	 * @return the applications
	 * @see #addApplication(Application)
	 * @see #removeApplication(Application)
	 */
	public ImmutableList<Application> getApplications() 
	{
		return applications();
	}
	
	/**
	 * Returns the applications of this block 
	 * @return a read-only list with the applications of this block. It is never <code>null</code>
	 */
	public ImmutableList<Application> applications() 
	{
		return ImmutableList.copyOf(applications_);
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
	public Block setId(String id) 
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
	 * @param name the name to set
	 */
	public Block setName(String name) 
	{
		this.name_ = name;
		return this;
	}

	/**
	 * @return the parents
	 */
	public String getParents()
	{
		return parents_;
	}
	
	/**
	 * Returns a non-null array with the parents' id of this {@link Block} if there is any.
	 * @return a non-null array with the parents' id of this {@link Block} if there is any.
	 */
	public String[] parents()
	{
		return getParents() != null ? getParents().split(",") : new String[0];
	}

	/**
	 * @param parents the parents to set
	 */
	public Block setParents(String parents) 
	{
		this.parents_ = parents;
		return this;
	}
	
	/**
	 * @return the repeat
	 */
	public Integer getRepeat() 
	{
		return repeat_;
	}

	/**
	 * @param repeat the repeat to set
	 */
	public Block setRepeat(Integer repeat) 
	{
		this.repeat_ = repeat;
		return this;
	}
	
	@Override
	public Iterator<Application> iterator() 
	{
		return getApplications().iterator();
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hashCode(getName(), getId(), getParents());
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
		
		Block other = (Block) obj;
		
		return Objects.equal(getId(), other.getId()) && 
			   Objects.equal(getParents(), getParents());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				       .add("id", getId())
				       .add("name", getName())
				       .add("parents", getParents())
				       .add("repeat", getRepeat())
				       .omitNullValues()
				       .toString();
	}
	
	@Override
	public Block clone() 
	{
		Block clone;
		
		try 
		{
			clone = (Block) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new Block().setId(getId()).setName(getName()).setRepeat(getRepeat()).setParents(getParents());
		}
		
		clone.applications_.clear();
		Iterable<Application> applications = new CloneIterableFunction<Application>().apply(applications_);
		applications.forEach(clone::addApplication);
		
		return clone;
	}

	@Override
	public int compareTo(Block that) 
	{
		return that == null ? 1 : Comparator.<String>naturalOrder().compare(getId(), that.getId());
	}

	@Override
	public int compare(Block o1, Block o2) 
	{
		return compare(o1, o2);
	}
}
