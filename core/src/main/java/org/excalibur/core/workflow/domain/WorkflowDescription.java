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
package org.excalibur.core.workflow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.domain.User;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "workflow")
@XmlType(name = "workflow")
public class WorkflowDescription implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7936259994734598411L;

    /**
     * The default separator char used to separate the dependencies of an activity.
     */
    public static final String DEFAULT_ACTIVITY_DEPENDENCY_SEPARATOR_CHAR = System.getProperty(
            "excalibur.default.workflow.activity.dependencies.separator", ",");

    @XmlAttribute(name = "id", required = true)
    private Integer id_;

    @XmlElement(name = "name")
    private String name_;

    @XmlElement(name = "owner")
    private User user_;

    @XmlElement(name = "submitted-in")
    private Date createdIn_;

    @XmlElement(name = "finished-in")
    private Date finishedIn_;

    @XmlElement(name = "start-activity-id")
    private Integer startActivityId_;
    
    @XmlElement(name="activity")
    @XmlElementWrapper(name="activities")
    private List<WorkflowActivityDescription> activitiesList = new ArrayList<WorkflowActivityDescription>();

    /**
     * It's not thread safe.
     */
    @XmlTransient
    private final Map<Integer, WorkflowActivityDescription> activities_ = new HashMap<Integer, WorkflowActivityDescription>();
    
    private transient final Object lock_ = new Object();

    public WorkflowDescription()
    {
        super();
        this.createdIn_ = new Date();
    }

    public WorkflowDescription(Integer id)
    {
        this();
        this.id_ = id;
    }

    public WorkflowDescription addActivity(WorkflowActivityDescription activity)
    {
        if (activity != null)
        {
            this.addActivities(Collections.singletonList(activity));
        }
        return this;
    }

    public WorkflowDescription addActivities(WorkflowActivityDescription... activities)
    {
        if (activities != null)
        {
            addActivities(Arrays.asList(activities));
        }
        return this;
    }

    public WorkflowDescription addActivities(Iterable<WorkflowActivityDescription> workflowActivities)
    {
        synchronized (lock_)
        {
            for (WorkflowActivityDescription activity : workflowActivities)
            {
                activity.setWorkflow(this);
                activities_.put(activity.getId(), activity);
            }

            for (Integer workflowActivityId : this.activities_.keySet())
            {
                WorkflowActivityDescription workflowActivity = this.activities_.get(workflowActivityId);
                String[] parents = workflowActivity.getParents() != null ? 
                        workflowActivity.getParents().split(DEFAULT_ACTIVITY_DEPENDENCY_SEPARATOR_CHAR) : new String[0];

                for (String parentId : parents)
                {
                    workflowActivity.addParent(this.activities_.get(Integer.parseInt(parentId)));
                }
            }
            
            activitiesList.clear();
            activitiesList.addAll(this.activities_.values());
        }

        return this;
    }

    public void removeActivity(WorkflowActivityDescription activity)
    {
        synchronized (lock_)
        {
            activities_.remove(activity);
        }
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public WorkflowDescription setId(Integer id)
    {
        this.id_ = id;
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
     * @param user
     *            the user to set
     */
    public WorkflowDescription setUser(User user)
    {
        this.user_ = user;
        return this;
    }

    /**
     * @return the createdIn
     */
    public Date getCreatedIn()
    {
        return createdIn_;
    }

    /**
     * @param createdIn
     *            the createdIn to set
     */
    public WorkflowDescription setCreatedIn(Date createdIn)
    {
        this.createdIn_ = createdIn;
        return this;
    }

    /**
     * @return the finishedIn
     */
    public Date getFinishedIn()
    {
        return finishedIn_;
    }

    /**
     * @param finishedIn
     *            the finishedIn to set
     */
    public WorkflowDescription setFinishedIn(Date finishedIn)
    {
        this.finishedIn_ = finishedIn;
        return this;
    }

    /**
     * @return the activities
     */
    public List<WorkflowActivityDescription> getActivities()
    {
        List<WorkflowActivityDescription> activities;
        synchronized (lock_)
        {
            activities = new ArrayList<WorkflowActivityDescription>(activities_.values());
        }
        return Collections.unmodifiableList(activities);
    }

    /**
     * 
     * @return
     */
    public Map<Integer, WorkflowActivityDescription> getActivitesMap()
    {
        synchronized (lock_)
        {
            return Collections.unmodifiableMap(activities_);
        }
    }

    /**
     * @return the startActivityId
     */
    public Integer getStartActivityId()
    {
        return startActivityId_;
    }

    /**
     * @param startActivityId
     *            the startActivityId to set
     */
    public WorkflowDescription setStartActivityId(Integer startActivityId)
    {
        this.startActivityId_ = startActivityId;
        return this;
    }

    /**
     * @return the name_
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name_
     *            the name_ to set
     */
    public WorkflowDescription setName(String name)
    {
        this.name_ = name;

        return this;
    }
    
    public int getNumberOfActivities()
    {
        return this.activities_.size();
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(getId());
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
        
        WorkflowDescription other = (WorkflowDescription) obj;
        return Objects.equal(getId(), other.getId());
    }
    
    @Override
    public WorkflowDescription clone()
    {
    	WorkflowDescription clone;
    	
		try 
		{
			clone = (WorkflowDescription) super.clone();
		} 
		catch (CloneNotSupportedException e) 
		{
			clone = new WorkflowDescription(getId())
					.setCreatedIn(getCreatedIn())
					.setFinishedIn(getFinishedIn())
					.setName(getName())
					.setStartActivityId(getStartActivityId())
					.setUser(getUser().clone());
		}
		
    	return clone;
    }
}
