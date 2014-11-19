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
import java.util.Date;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.task.TaskState;

public class TaskDescriptionState implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8001386707065100515L;
   
    private Integer id_;
    private TaskDescription task_;
    private VirtualMachine node_;
    private Date stateTime_;
    private TaskState state_;
    private String message_;

    public TaskDescriptionState()
    {
        super();
    }

    public TaskDescriptionState(Integer id)
    {
        this.id_ = id;
    }

    public TaskDescriptionState(TaskDescriptionState that)
    {
        this(that.getId());
        setMessage(that.getMessage()).setNode(that.getNode()).setState(that.getState());
        setStateTime(that.getStateTime()).setTask(that.getTask());
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
    public void setId(Integer id)
    {
        this.id_ = id;
    }

    /**
     * @return the task
     */
    public TaskDescription getTask()
    {
        return task_;
    }

    /**
     * @param task
     *            the task to set
     */
    public TaskDescriptionState setTask(TaskDescription task)
    {
        this.task_ = task;
        return this;
    }

    /**
     * @return the node
     */
    public VirtualMachine getNode()
    {
        return node_;
    }

    /**
     * @param node
     *            the node to set
     */
    public TaskDescriptionState setNode(VirtualMachine node)
    {
        this.node_ = node;
        return this;
    }

    /**
     * @return the stateTime
     */
    public Date getStateTime()
    {
        return stateTime_;
    }

    /**
     * @param stateTime
     *            the stateTime to set
     */
    public TaskDescriptionState setStateTime(Date stateTime)
    {
        this.stateTime_ = stateTime;
        return this;
    }

    /**
     * @return the status
     */
    public TaskState getState()
    {
        return state_;
    }

    /**
     * @param status
     *            the status to set
     */
    public TaskDescriptionState setState(TaskState state)
    {
        this.state_ = state;
        return this;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message_;
    }

    /**
     * @param message
     *            the message to set
     */
    public TaskDescriptionState setMessage(String message)
    {
        this.message_ = message;
        return this;
    }
    
    @Override
    public TaskDescriptionState clone()
    {
        TaskDescriptionState dolly = null;
        try
        {
            dolly = (TaskDescriptionState) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            dolly = new TaskDescriptionState(this);
        }
        
        dolly.setId(null);
        
        return dolly;
    }
}
