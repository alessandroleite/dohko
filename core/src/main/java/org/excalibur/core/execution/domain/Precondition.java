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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


import static java.util.Collections.*;

import static com.google.common.base.Joiner.*;
import static com.google.common.base.Strings.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "precondition")
@XmlType(name = "precondition")
public class Precondition implements Serializable, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = 1876878688034296497L;

	private final transient Lock LOCK_ = new ReentrantLock();

	@XmlElement(name="packages")
	private final List<String> packages_ = new ArrayList<>();
	
	public Precondition ()
	{
		super();
	}
	
	public Precondition (String [] packages) 
	{
		if (packages != null)
		{
			for (String pkg: packages)
			{
				if (!isNullOrEmpty(pkg))
				{
					packages_.add(pkg);
				}
			}
			sort(packages_);
		}
	}
	
	/**
	 * Creates and returns an instance of {@link Precondition} with the given packages as dependencies.
	 * 
	 * @param packages packages to be included as precondition
	 * @return an instance of this class
	 */
	@Nonnull
	public static Precondition newPrecondition(String ... packages)
	{
		return new Precondition(packages);
	}
	
	/**
	 * Returns a read-only view of the packages that represent a precondition.
	 * 
	 * @return the packages a read-only view of the package that represent a precondition. It is never <code>null</code>.
	 */
	public List<String> getPackages() 
	{
		return Collections.unmodifiableList(packages_);
	}
	
	/**
	 * Appends the given package's name as a precondition,  if and only if it's not <code>null</code> or empty. 
	 * 
	 * @param name name of the package to include as a precondition
	 * @return this instance
	 */
	public Precondition add(String name) 
	{
		if (!isNullOrEmpty(name))
		{
			LOCK_.lock();
			
			try
			{
				if (!packages_.contains(name)) 
				{
					packages_.add(name);
				}
				
				sort(packages_);
			}
			finally 
			{
				LOCK_.unlock();
			}
		}
		
		return this;
	}
	
	/**
	 * Removes from this precondition all of the packages that are contained in the given {@link List}. 
	 * 
	 * @param packages packages to remove
	 * @return this instance
	 */
	public Precondition removeAll(List<String> packages)
	{
		LOCK_.lock();
		
		try
		{
			packages_.removeAll(packages);
			sort(packages_);
		}
		finally
		{
			LOCK_.unlock();
		}
			
			
				
		return this;
	}
	
	/**
	 * Removes from this precondition the given package.
	 * 
	 * @param name name of the package to remove. <code>null</code> values are ignored
	 * @return this instance
	 */
	public Precondition remove(String name)
	{
		if (!isNullOrEmpty(name))
		{
			LOCK_.lock();
			
			try
			{
				packages_.remove(name);
				sort(packages_);
			}
			finally
			{
				LOCK_.unlock();
			}
		}
		
		return this;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null || !this.getClass().equals(obj.getClass()))
		{
			return false;
		}
		
		Precondition other = (Precondition)obj;
		
		if (packages_.size() != other.packages_.size())
		{
			return false;
		}
		
		
		LOCK_.lock();
		
		try
		{
			for (String pkg: packages_)
			{
				if (!other.packages_.contains(pkg))
				{
					return false;
				}
			}
		}
		finally
		{
			LOCK_.unlock();
		}
		
		return true;
	}
	
	@Override
	public int hashCode() 
	{
		return on(" ").join(packages_).length();
	}
	
	@Override
	public String toString() 
	{
		return Objects.toStringHelper(this)
				      .omitNullValues()
				      .add("packages", on(",").join(packages_))
				      .toString();
	}
	
	@Override
	public Precondition clone() 
	{
		Precondition clone;
		
		try 
		{
			clone = (Precondition) super.clone();
			
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new Precondition(packages_.toArray(new String[packages_.size()]));
		}
		
		return clone;
	}
}
