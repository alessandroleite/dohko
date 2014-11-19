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
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.util.YesNoEnum;

@XmlRootElement(name = "task-data")
@XmlType(name = "task-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskDataDescription implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 3780956417991298882L;
   
    @XmlAttribute(name = "id")
    private Integer         id_;
    
    @XmlTransient
    private TaskDescription task_;
    
    @XmlElement(name="name")
    private String          name_;
    
    @XmlElement(name="path")
    private String          path_;
    
    @XmlElement(name="size-gb")
    private BigDecimal      sizeGb_ = BigDecimal.ZERO;
    
    @XmlElement(name="splittable")
    private YesNoEnum       splittable_ = YesNoEnum.NO;
    
    @XmlElement(name="generated")
    private YesNoEnum       dynamic_ = YesNoEnum.NO;
    
    @XmlElement(name="type")
    private DataType        type_;
    
    @XmlElement(name="description")
    private String          description_;

    public TaskDataDescription()
    {
        super();
    }
    
    public TaskDataDescription(Integer id)
    {
        this.id_ = id;
    }

    public TaskDataDescription(TaskDataDescription that)
    {
        this(that.getId());
        setTask(that.getTask())
        .setName(that.getName())
        .setPath(that.getPath())
        .setSizeGb(that.getSizeGb())
        .setSplittable(that.isSplittable())
        .setDynamic(that.isDynamic())
        .setType(that.getType())
        .setDescription(that.getDescription());
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
    public TaskDataDescription setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the task_
     */
    public TaskDescription getTask()
    {
        return task_;
    }

    /**
     * @param task_
     *            the task_ to set
     */
    public TaskDataDescription setTask(TaskDescription task)
    {
        this.task_ = task;
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
    public TaskDataDescription setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the path_
     */
    public String getPath()
    {
        return path_;
    }

    /**
     * @param path_
     *            the path_ to set
     */
    public TaskDataDescription setPath(String path)
    {
        this.path_ = path;
        return this;
    }

    /**
     * @return the sizeGb_
     */
    public BigDecimal getSizeGb()
    {
        return sizeGb_;
    }

    /**
     * @param sizeGb_
     *            the sizeGb_ to set
     */
    public TaskDataDescription setSizeGb(BigDecimal sizeGB)
    {
        this.sizeGb_ = sizeGB;
        return this;
    }

    /**
     * @return the splittable_
     */
    public YesNoEnum isSplittable()
    {
        return splittable_;
    }
    
    public YesNoEnum getSplittable()
    {
        return this.isSplittable();
    }

    /**
     * @param splittable_
     *            the splittable_ to set
     */
    public TaskDataDescription setSplittable(YesNoEnum splittable)
    {
        this.splittable_ = splittable;
        return this;
    }

    /**
     * @return the dynamic_
     */
    public YesNoEnum isDynamic()
    {
        return dynamic_;
    }
    
    public YesNoEnum getDynamic()
    {
        return this.isDynamic();
    }

    /**
     * @param dynamic_
     *            the dynamic_ to set
     */
    public TaskDataDescription setDynamic(YesNoEnum dynamic)
    {
        this.dynamic_ = dynamic;
        return this;
    }

    /**
     * @return the type_
     */
    public DataType getType()
    {
        return type_;
    }

    /**
     * @param type_
     *            the type_ to set
     */
    public TaskDataDescription setType(DataType type)
    {
        this.type_ = type;
        return this;
    }

    /**
     * @return the description_
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @param description_
     *            the description_ to set
     */
    public TaskDataDescription setDescription(String description)
    {
        this.description_ = description;
        return this;
    }
    
    @Override
    public TaskDataDescription clone()
    {
        TaskDataDescription cloned = null;
        try
        {
            cloned = (TaskDataDescription) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new TaskDataDescription(this);
        }
        
        cloned.setId(null);
        return cloned;
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
        
    
        if (!(obj instanceof TaskDataDescription))
        {
            return false;
        }
        
        TaskDataDescription other = (TaskDataDescription) obj;
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
