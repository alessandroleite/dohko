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

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.Identifiable;
import org.excalibur.core.util.CloneIterableFunction;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application")
@XmlType(name = "application", propOrder = {"id_", "jobId_", "name_", "commandLine_", "preconditions_", "files_" })
public class Application implements Serializable, Cloneable, Identifiable<String>
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -4544987511384140652L;

	/**
     * Opaque application's id
     */
    @XmlAttribute(name = "id")
    private String id_;
    
    /**
     * Job's id
     */
	@XmlAttribute(name = "job-id", required = true)
    private String jobId_;

    /**
     * Application's name
     */
    @XmlElement(name = "name", required = true, nillable = false)
    private String name_;

    @XmlElement(name = "command-line", required = true, nillable = false)
    private String commandLine_;

    @XmlElement(name = "files")
    private final List<AppData> files_ = new ArrayList<>();
    
    @XmlElement(name = "preconditions")
    private final List<Precondition> preconditions_ = new ArrayList<>();
    
    @XmlElement(name = "timeout")
    private Long timeout_;
    
    @XmlElement(name = "parents")
    private final List<String> parents_ = new ArrayList<>();
    
    @XmlTransient
    private String plainText_;
    
    @XmlTransient
    private String blockId_;
    
    public Application()
    {
        super();
    }
    
    public Application(String id)
    {
        this.id_ = id;
    }

    public Application addData(AppData data)
    {
        if (data != null && !files_.contains(data))
        {
            files_.add(data);
        }

        return this;
    }

    public Application addAll(Iterable<AppData> data)
    {
        if (data != null)
        {
            for (AppData datum : data)
            {
                this.addData(datum);
            }
        }

        return this;
    }
    
    public Application addPrecondition(Precondition precondition)
    {
    	if (precondition != null)
    	{
    		preconditions_.add(precondition);
    	}
    	
    	return this;
    }
    
    public Application removePrecondition(Precondition precondition)
    {
    	preconditions_.remove(precondition);
    	return this;
    }

    public Application setData(AppData appData)
    {
        return addData(appData);
    }

    public Application removeData(AppData appData)
    {
        this.files_.remove(appData);

        return this;
    }
    
    public Application addParent(String parent)
    {
    	if (!Strings.isNullOrEmpty(parent))
    	{
    		parents_.add(parent);
    	}
    	
    	return this;
    }
    
    public Application addParents(String ...parents)
    {
    	if (parents != null)
    	{
    		for (String parent: parents)
    		{
    			addParent(parent);
    		}
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

    /**
     * @param id the id to set
     */
    public Application setId(String id)
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
    public Application setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the commandLine
     */
    public String getCommandLine()
    {
        return commandLine_;
    }

    /**
     * @param commandLine the commandLine to set
     */
    public Application setCommandLine(String commandLine)
    {
        this.commandLine_ = commandLine;
        return this;
    }

    /**
     * @return the files
     */
    public ImmutableList<AppData> getFiles()
    {
        return ImmutableList.copyOf(files_);
    }
    
    /**
     * @return the job
     */
    public String getJobId()
    {
        return jobId_;
    }

    /**
     * @param job the job to set
     */
    public Application setJobId(String jobId)
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
    public Application setPlainText(String plainText)
    {
        this.plainText_ = plainText;
        return this;
    }
    
    
    /**
     * Returns a read-only view of the preconditions of this application
     * 
	 * @return the preconditions a read-only view of the preconditions of this application. It is never <code>null</code>
	 */
    @Nonnull
	public List<Precondition> getPreconditions() 
	{
		return Collections.unmodifiableList(preconditions_);
	}

	/**
	 * @return the timeout
	 */
	public Long getTimeout() 
	{
		return timeout_;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public Application setTimeout(Long timeout) 
	{
		this.timeout_ = timeout;
		return this;
	}

	/**
	 * @return the parents
	 */
	public ImmutableList<String> getParents() 
	{
		return ImmutableList.copyOf(parents_);
	}
	
	public ImmutableList<String> parents()
	{
		return getParents();
	}
	
	public boolean hasParents()
	{
		return !parents_.isEmpty();
	}
	
	/**
	 * @return the blockId
	 */
	public String getBlockId() 
	{
		return blockId_;
	}

	/**
	 * @param blockId the blockId to set
	 */
	public Application setBlockId(String blockId) 
	{
		this.blockId_ = blockId;
		return this;
	}

	@Override
    public Application clone()
    {
        Application clone;

        try
        {
            clone = (Application) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Application()
            		.setBlockId(getBlockId())
                    .setCommandLine(getCommandLine())
                    .setId(getId())
                    .setName(getName())
                    .setJobId(getJobId())
                    .setPlainText(getPlainText());
        }
        
        final ImmutableList<AppData> files = getFiles();
        clone.files_.clear();
        clone.addAll(new CloneIterableFunction<AppData>().apply(files));

        return clone;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("name", getName())
                .add("command-line", getCommandLine())
                .add("files", files_)
                .add("timeout", getTimeout())
                .add("parents", Joiner.on(",").join(getParents()))
                .add("blockId", getBlockId())
                .omitNullValues()
                .toString();
    }
}
