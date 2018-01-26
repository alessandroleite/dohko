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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.Identifiable;
import org.excalibur.core.util.CloneIterableFunction;
import org.excalibur.core.util.Lists2;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * A {@link Block} represents a set of tasks that might be executed in any order. Therefore, the ordering of a block must be respected.  
 * A {@link Block} may have one or more parents. 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="block")
@XmlType(name = "block", propOrder = { "id_", "name_", "parents_", "repeat_", "jobId_", "applications_" })
public class Block implements Serializable, Cloneable, Iterable<Application>, Comparable<Block>, Comparator<Block>, Identifiable<String>
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization
	 */
	private static final long serialVersionUID = 1044116344574286559L;

	/**
	 * It must be unique in the application descriptor.
	 */
	@XmlElement(name = "id", required = true)
	private String id_;
	
	@XmlElement(name = "name")
	private String name_;
	
	@XmlElement(name="parents")
	private final List<String> parents_ = new ArrayList<>();
	
	@XmlElement(name="repeat", required = false, defaultValue = "1")
	private Integer repeat_ = 1;
	
	@XmlAttribute(name = "job-id")
	private String jobId_;
	
	@XmlElement(name="applications")
	private final List<Application> applications_ = new ArrayList<>();
	
	@XmlTransient
	private String plainText_;
	
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
	
	public Block addParent(String parent)
	{
		if (!Strings.isNullOrEmpty(parent))
		{
			parents_.add(parent);
		}
		
		return this;
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() 
	{
		return id_;
	}
	
	public String id()
	{
		return this.id_;
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
	
	public String name()
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
	public ImmutableList<String> getParents()
	{
		return ImmutableList.copyOf(parents_);
	}
	
	public String getParentNames()
	{
		return Joiner.on(",").join(getParents());
	}
	
	/**
	 * Returns a non-null array with the parents' id of this {@link Block} if there is any.
	 * @return a non-null array with the parents' id of this {@link Block} if there is any.
	 */
	public String[] parents()
	{
		List<String> parents = getParents(); 
		return parents.toArray(new String[parents.size()]);
	}
	
	public boolean hasParents()
	{
		return !parents_.isEmpty();
	}

	/**
	 * @param parents the parents to set
	 */
	public Block setParents(List<String> parents) 
	{
		if (parents != null)
		{
			parents.forEach(this::addParent);
		}
		
		return this;
	}
	
	public Block setParents(String[] parents)
	{
		if (parents != null)
		{
			setParents(Arrays.asList(parents));
		}
		
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
	
	/**
	 * @return the jobId
	 */
	public String getJobId() 
	{
		return jobId_;
	}

	/**
	 * @param jobId the jobId to set
	 */
	public Block setJobId(String jobId) 
	{
		this.jobId_ = jobId;
		return this;
	}
	
	

	/**
	 * @return the plainText
	 */
	public String getPlainText() 
	{
		return plainText_;
	}

	/**
	 * @param plainText the plainText to set
	 */
	public Block setPlainText(String plainText) 
	{
		this.plainText_ = plainText;
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
		return Objects.hashCode(getName(), getId(), getParents(), getJobId());
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
			   Objects.equal(getParents(), getParents()) &&
			   Objects.equal(getJobId(), other.getJobId());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				       .add("id", getId())
				       .add("name", getName())
				       .add("parents", Joiner.on(",").join(getParents()).toString())
				       .add("repeat", getRepeat())
				       .add("job-id", getJobId())
				       .add("plain-text", getPlainText())
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
			clone = new Block().setId(getId()).setName(getName()).setRepeat(getRepeat()).setParents(getParents()).setJobId(getJobId()).setPlainText(getPlainText());
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
