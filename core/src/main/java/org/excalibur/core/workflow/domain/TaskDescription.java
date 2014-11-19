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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task")
@XmlType(name = "type")
public class TaskDescription implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8024274833567883702L;

    @XmlAttribute(name = "id")
    private Integer id_;

    @XmlElement(name = "executable")
    private String executable_;

    @XmlElement(name = "type")
    private String typeClass_;

    @XmlElement(name = "data")
    private List<TaskDataDescription> data_ = new ArrayList<TaskDataDescription>();

    @XmlTransient
    private WorkflowActivityDescription activity_;

    public TaskDescription()
    {
        super();
    }

    public TaskDescription(Integer id)
    {
        this.id_ = id;
    }

    public TaskDescription(Integer id, WorkflowActivityDescription activity)
    {
        this(id);
        this.activity_ = activity;
    }

    public TaskDescription(Integer id, WorkflowActivityDescription activity, String command, String typeClass)
    {
        this(id, activity);
        this.executable_ = command;
        this.typeClass_ = typeClass;
    }

    public TaskDescription(TaskDescription that)
    {
        this(that.id_, that.activity_, that.executable_, that.typeClass_);
        this.addAllData(that.data_);
    }

    public TaskDescription addData(TaskDataDescription data)
    {
        if (data != null)
        {
            data.setTask(this);
            this.data_.add(data);
        }
        return this;
    }

    public TaskDescription addAllData(Collection<TaskDataDescription> collection)
    {
        for (TaskDataDescription data : collection)
        {
            addData(data);
        }

        return null;
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
    public TaskDescription setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the activity
     */
    public WorkflowActivityDescription getActivity()
    {
        return activity_;
    }

    /**
     * @param activity
     *            the activity to set
     */
    public TaskDescription setActivity(WorkflowActivityDescription activity)
    {
        this.activity_ = activity;
        return this;
    }

    /**
     * @return the command
     */
    public String getExecutable()
    {
        return executable_;
    }

    /**
     * @param command
     *            the command to set
     */
    public TaskDescription setExecutable(String command)
    {
        this.executable_ = command;
        return this;
    }

    /**
     * @return the typeClass
     */
    public String getTypeClass()
    {
        return typeClass_;
    }

    /**
     * @param typeClass
     *            the typeClass to set
     */
    public TaskDescription setTypeClass(String typeClass)
    {
        this.typeClass_ = typeClass;
        return this;
    }

    /**
     * @return the data_
     */
    public List<TaskDataDescription> getData()
    {
        return Collections.unmodifiableList(data_);
    }

    @Override
    public TaskDescription clone()
    {
        TaskDescription clone = null;

        try
        {
            clone = (TaskDescription) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new TaskDescription(this);
        }

        clone.setId(null);
        clone.data_.clear();

        for (TaskDataDescription data : this.getData())
        {
            clone.addData(data.clone());
        }

        return clone;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id_ == null) ? 0 : id_.hashCode());
        return result;
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
        if (!(obj instanceof TaskDescription))
        {
            return false;
        }
        TaskDescription other = (TaskDescription) obj;
        if (id_ == null)
        {
            if (other.id_ != null)
            {
                return false;
            }
        }
        else if (!id_.equals(other.id_))
        {
            return false;
        }
        return true;
    }
}
