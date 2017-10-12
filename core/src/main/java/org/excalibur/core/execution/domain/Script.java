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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "script")
@XmlType(name = "script")
public class Script implements Serializable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -8051730720405666267L;

	@XmlElement(name="local-path")
	private String localPath_;
	
	@XmlElement(name="remote-path")
	private String remotePath_;
	
	@XmlElement(name="privileged", required=true, defaultValue = "false")
	private boolean privileged_;

	/**
	 * @return the localPath
	 */
	public String getLocalPath() 
	{
		return localPath_;
	}

	/**
	 * @param localPath the localPath to set
	 */
	public Script setLocalPath(String localPath) 
	{
		this.localPath_ = localPath;
		return this;
	}

	/**
	 * @return the remotePath
	 */
	public String getRemotePath() 
	{
		return remotePath_;
	}

	/**
	 * @param remotePath the remotePath to set
	 */
	public Script setRemotePath(String remotePath) 
	{
		this.remotePath_ = remotePath;
		return this;
	}

	/**
	 * @return the privileged
	 */
	public boolean isPrivileged() 
	{
		return privileged_;
	}

	/**
	 * @param privileged the privileged to set
	 */
	public Script setPrivileged(boolean privileged) 
	{
		this.privileged_ = privileged;
		return this;
	}

	@Override
	public int hashCode() 
	{
		return Objects.hashCode(getLocalPath(),getRemotePath(), isPrivileged());
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		
		if (obj == null)
		{
			return false;
		}
			
		if (getClass() != obj.getClass()) 
		{
			return false;
		}
		
		Script other = (Script) obj;
		
		return Objects.equal(getLocalPath(), other.getLocalPath())   &&
			   Objects.equal(getRemotePath(), other.getRemotePath()) &&
			   Objects.equal(isPrivileged(), other.isPrivileged());
	}
	
	@Override
	public String toString() 
	{
		return MoreObjects.toStringHelper(this)
				      .omitNullValues()
				      .add("local-path", getLocalPath())
				      .add("remote-path", getRemotePath())
				      .add("privileged", isPrivileged())
				      .toString();
	}
}
