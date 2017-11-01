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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.Cloud;
import org.excalibur.core.domain.User;
import org.excalibur.core.util.Lists2;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import static java.util.Arrays.*;

import static org.excalibur.core.util.CloneIterableFunction.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "application-descriptor")
@XmlType(name = "application-descriptor", propOrder = { 
		"id_", "name_", "description_", "user_", "requirements_", "preconditions_", "clouds_", "applications_", "blocks_",
        "createdIn_", "finishedIn_", "onFinished_" })
public class ApplicationDescriptor implements Serializable, Cloneable
{
	/**
	 * Serial code version <code>serialVersionUID</code> for serialization.
	 */
	private static final long serialVersionUID = 1759115607404731621L;

    @XmlTransient
    private Integer internalId;

    /**
     * Opaque application's deployment ID. It can be for instance, the hash (SHA-1) of the content.
     */
    @XmlElement(name = "id", required = true, nillable=false)
    private String id_;
    
    @XmlElement(name = "name", required = true, nillable=false)
    private String name_;

    @XmlElement(name = "user", nillable = false, required = true)
    private User user_;

    @XmlElement(name = "requirements", nillable = false, required = true)
    private Requirements requirements_;
    
    @XmlElement(name = "preconditions")
    private final List<Precondition> preconditions_ = new ArrayList<>();

    @XmlElement(name="clouds")
//    private final Clouds clouds_ = new Clouds();
    private final List<Cloud> clouds_ = new ArrayList<>();

    @XmlElement(name = "applications")
    private final List<Application> applications_ = new ArrayList<>();
    
    @XmlElement(name = "blocks")
    private final List<Block> blocks_ = new ArrayList<>();
    
    @XmlElement(name = "description")
    private String description_;
    
    @XmlElement(name = "submitted-in", nillable = false, required = true)
    private Long createdIn_;
    
    @XmlElement(name = "execution-finished-in")
    private Long finishedIn_;
    
    @XmlTransient
    private String plainText_;
    
    @XmlElement(name = "on-finish", required = true, nillable = false)
    private FinishAction onFinished_ = FinishAction.NONE;
    
    
    public ApplicationDescriptor()
    {
        super();
    }
    
    public ApplicationDescriptor(String id)
    {
        this.id_ = id;
    }
    
    public ApplicationDescriptor addApplication(Application app)
    {
    	if (app != null && !applications_.contains(app))
    	{
    		applications_.add(app);
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor addApplications(Iterable<Application> apps)
    {
    	if (apps != null)
    	{
    		apps.forEach(this::addApplication);
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor addApplications (Application ... apps)
    {
    	if (apps != null)
    	{
    		for (int i = 0; i < apps.length; i++) 
    		{
    			addApplication(apps[i]);
			}
    	}
    	
    	return this;
    }
    
    public Optional<Application> getApplication(int index)
    {
    	return Lists2.isInRage(index, applications_.size()) ? Optional.of(applications_.get(index)) : Optional.<Application>absent();
    }
    
    /**
     * @return the applications
     */
    public ImmutableList<Application> getApplications()
    {
    	return applications();
    }
    
    public ImmutableList<Application> applications()
    {
    	return ImmutableList.copyOf(applications_);
    }
    
    public ApplicationDescriptor addBlock(Block block)
    {
    	if (block != null && !blocks_.contains(block))
    	{
    		blocks_.add(block);
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor addBlocks(Iterable<Block> blocks)
    {
    	blocks.forEach(this::addBlock);
    	return this;
    }
    
    public ApplicationDescriptor addBlocks(Block ...blocks)
    {
    	if (blocks != null)
    	{
    		for (Block block : blocks) 
    		{
    			addBlock(block);
			}
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor removeBlock(int index)
    {
    	if (Lists2.isInRage(index, blocks_.size()))
    	{
    		blocks_.remove(index);
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor removeBlock(Block block)
    {
    	blocks_.remove(block);
    	return this;
    }
    
    public Optional<Block> getBlock(int index)
    {
    	return Lists2.isInRage(index, blocks_.size()) ? Optional.of(blocks_.get(index)) : Optional.<Block>absent();
    }
    
    
    /**
     * Returns the blocks of ordered by their ids.
     * @return the blocks of this job ordered by their ids.
     */
    public ImmutableList<Block> blocks()
    {
    	List<Block> blocks = Lists.newArrayList(blocks_);
    	Collections.sort(blocks);
    	
    	return ImmutableList.copyOf(blocks);
    }
    
    /**
	 * @return the blocks
	 */
	public ImmutableList<Block> getBlocks() 
	{
		return blocks();
	}
    
    public ApplicationDescriptor addCloud(Cloud cloud)
    {
        if (cloud != null)
        {
            clouds_.add(cloud);
        }
        
        return this;
    }
    
    public ApplicationDescriptor removeCloud(Cloud cloud)
    {
        clouds_.remove(cloud);
        return this;
    }
    
    public Optional<Cloud> getCloud(int index)
    {
    	return Lists2.isInRage(index, clouds_.size()) ? Optional.of(clouds_.get(index)) : Optional.<Cloud>absent();
    }
    
    /**
     * @return the clouds
     */
    public List<Cloud> getClouds()
    {
        return Collections.unmodifiableList(clouds_);
    }
    
    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @param description the description to set
     */
    public ApplicationDescriptor setDescription(String description)
    {
        this.description_ = description;
        return this;
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
    public ApplicationDescriptor setId(String id)
    {
        this.id_ = id;
        return this;
    }
    
    

    /**
     * @return the internalId
     */
    public Integer getInternalId()
    {
        return internalId;
    }

    /**
     * @param internalId the internalId to set
     */
    public ApplicationDescriptor setInternalId(Integer internalId)
    {
        this.internalId = internalId;
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
    public ApplicationDescriptor setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the onFinished
     */
    public FinishAction getOnFinished()
    {
        return onFinished_;
    }

    /**
     * @param onFinished the onFinished to set
     */
    public ApplicationDescriptor setOnFinished(FinishAction onFinished)
    {
        this.onFinished_ = onFinished;
        return this;
    }
    
    public ApplicationDescriptor addPrecondition(Precondition precondition)
    {
    	if (precondition != null)
    	{
    		preconditions_.add(precondition);
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor addPreconditions(Precondition ...preconditions)
    {
    	if (preconditions != null)
    	{
    		addPreconditions(asList(preconditions));
    	}
    	
    	return this;
    }
    
    public ApplicationDescriptor addPreconditions(Iterable<Precondition> preconditions)
    {
    	return this;
    }
    
    
    public ApplicationDescriptor removePrecondition(Precondition precondition)
    {
    	if (precondition != null)
    	{
    		preconditions_.remove(precondition);
    	}
    	
    	return this;
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user_;
    }

    /**
     * @param user the user to set
     */
    public ApplicationDescriptor setUser(User user)
    {
        this.user_ = user;
        return this;
    }

    /**
     * @return the requirements
     */
    public Requirements getRequirements()
    {
        return requirements_;
    }

    /**
     * @param requirements the requirements to set
     */
    public ApplicationDescriptor setRequirements(Requirements requirements)
    {
        this.requirements_ = requirements;
        return this;
    }

//    /**
//     * @param applications the application to set
//     * @return this instance
//     */
//    public ApplicationDescriptor setApplications(Applications applications)
//    {
//        applications_ = applications;
//        return this;
//    }
    

	/**
     * @return the plainText
     */
    public String getPlainText()
    {
        return plainText_;
    }

    /**
     * @return the createdIn
     */
    public Long getCreatedIn()
    {
        return createdIn_;
    }

    /**
     * @param createdIn the createdIn to set
     */
    public ApplicationDescriptor setCreatedIn(Long createdIn)
    {
        this.createdIn_ = createdIn;
        return this;
    }

    /**
     * @return the finishedIn
     */
    public Long getFinishedIn()
    {
        return finishedIn_;
    }

    /**
     * @param finishedIn the finishedIn to set
     */
    public ApplicationDescriptor setFinishedIn(Long finishedIn)
    {
        this.finishedIn_ = finishedIn;
        return this;
    }

    /**
     * @param plainText the plainText to set
     */
    public ApplicationDescriptor setPlainText(String plainText)
    {
        this.plainText_ = plainText;
        return this;
    }
    
    /**
     * Returns an unmodifiable view of the preconditions. Any attempt to modify the returned list, 
     * whether direct or via its iterator, result in an UnsupportedOperationException.
     * 
	 * @return the preconditions a read-only view of the preconditions.
	 */
	public List<Precondition> getPreconditions() 
	{
		return Collections.unmodifiableList(preconditions_);
	}

	@Override
    public int hashCode()
    {
       return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof ApplicationDescriptor))
        {
            return false;
        }
        
        ApplicationDescriptor other = (ApplicationDescriptor) obj;
        return Objects.equal(this.getId(), other.getId());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("name", getName())
                .add("created-in", getCreatedIn())
                .add("finished-in", getFinishedIn())
                .add("user", getUser())
                .add("requirements", getRequirements())
                .add("description", getDescription())
                .add("clouds", getClouds())
                .add("applications", getApplications())
                .add("blocks", getBlocks())
                .add("preconditions", getPreconditions())
                .omitNullValues()
                .toString();
    }
    
    @Override
    public ApplicationDescriptor clone() 
    {
    	ApplicationDescriptor clone;
    	
    	try 
    	{
			clone = (ApplicationDescriptor) super.clone();
		} 
    	catch (CloneNotSupportedException e) 
    	{
    		clone = new ApplicationDescriptor()
//    				      .setApplications(getApplications().clone())
    				      .setCreatedIn(getCreatedIn())
    				      .setDescription(getDescription())
    				      .setFinishedIn(getFinishedIn())
    				      .setId(getId())
    				      .setInternalId(getInternalId())
    				      .setName(getName())
    				      .setOnFinished(getOnFinished())
    				      .setPlainText(getPlainText())
    				      .addPreconditions(cloneIterable(preconditions_))
    				      .setRequirements(getRequirements().clone())
    				      .setUser(getUser().clone());
		}
    	
    	return clone;
    }
}
