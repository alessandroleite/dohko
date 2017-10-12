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
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "activity")
@XmlType(name = "activity")
public class WorkflowActivityDescription implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -2710760406345122384L;

    @XmlTransient
    private Integer internalId_;
    
    @XmlAttribute(name = "id", required = true)
    private Integer id_;

    // @XmlElement(name = "workflow", required = true)
    @XmlTransient
    private WorkflowDescription workflow_;

    @XmlElement(name = "label", required = true)
    private String label_;

    @XmlElement(name = "type")
    private String type_;

    @XmlElement(name = "parents")
    private String parents_;

    @XmlElement(name = "task")
    @XmlElementWrapper(name = "tasks")
    private final List<TaskDescription> tasks_ = Lists.newArrayList();

    @XmlTransient
    private final List<WorkflowActivityDescription> parentsList_ = new ArrayList<WorkflowActivityDescription>();

    private transient final Object lock_ = new Object();

    public WorkflowActivityDescription()
    {
        super();
        this.type_ = this.getClass().getName();
    }

    public WorkflowActivityDescription(Integer id)
    {
        this();
        this.id_ = id;
    }

    public WorkflowActivityDescription addParent(WorkflowActivityDescription parent)
    {
        return this.addParents(parent);
    }

    public WorkflowActivityDescription addParents(WorkflowActivityDescription... parents)
    {
        synchronized (lock_)
        {
            for (WorkflowActivityDescription parent : parents)
            {
                if (parent != null && !this.parentsList_.contains(parent))
                {
                    this.parentsList_.add(parent);
                }
            }
        }
        return this;
    }

    public WorkflowActivityDescription addTask(TaskDescription task)
    {
        return this.addTasks(task);
    }

    public WorkflowActivityDescription addTasks(TaskDescription... tasks)
    {
        if (tasks != null)
        {
            for (TaskDescription task : tasks)
            {
                if (task != null)
                {
                    task.setActivity(this);
                    tasks_.add(task);
                }
            }
        }
        return this;
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
    public WorkflowActivityDescription setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the workflow
     */
    public WorkflowDescription getWorkflow()
    {
        return workflow_;
    }

    /**
     * @param workflow
     *            the workflow to set
     */
    public WorkflowActivityDescription setWorkflow(WorkflowDescription workflow)
    {
        this.workflow_ = workflow;
        return this;
    }

    /**
     * @return the label
     */
    public String getLabel()
    {
        return label_;
    }

    /**
     * @param label
     *            the label to set
     */
    public WorkflowActivityDescription setLabel(String label)
    {
        this.label_ = label;
        return this;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type_;
    }

    /**
     * @param type
     *            the type to set
     */
    public WorkflowActivityDescription setType(String type)
    {
        this.type_ = type;
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
     * @param parents
     *            the parents to set
     */
    public WorkflowActivityDescription setParents(String parents)
    {
        this.parents_ = parents;
        return this;
    }

    /**
     * @return the parents
     */
    public List<WorkflowActivityDescription> getDependencies()
    {
        synchronized (lock_)
        {
            return Collections.unmodifiableList(parentsList_);
        }
    }

    /**
     * @return the tasks
     */
    public List<TaskDescription> getTasks()
    {
        return tasks_;
    }

    /**
     * Returns the number of parents of this activity.
     * 
     * @return
     */
    public int getNumberOfDependencies()
    {
        synchronized (lock_)
        {
            return this.parentsList_.size();
        }
    }
    
    

    /**
     * @return the internalId
     */
    public Integer getInternalId()
    {
        return internalId_;
    }

    /**
     * @param internalId the internalId to set
     */
    public WorkflowActivityDescription setInternalId(Integer internalId)
    {
        this.internalId_ = internalId;
        return this;
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getId(), this.getInternalId());
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
        
        WorkflowActivityDescription other = (WorkflowActivityDescription) obj;
        return Objects.equal(this.getId(), other.getId()) || 
               (
                       this.getInternalId() != null && other.getInternalId() != null  && 
                       Objects.equal(this.getInternalId(), other.getInternalId())
               );
        
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this.getClass()).add("id", this.getId())
                .add("label", this.getLabel())
                .add("type", this.getType())
                .omitNullValues()
                .toString();
    }
}
