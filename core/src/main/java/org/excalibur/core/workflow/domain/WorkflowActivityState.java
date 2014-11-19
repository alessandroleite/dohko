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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "activity-state")
@XmlType(name = "activity-state", propOrder={"activity_", "state_", "updateTime_", "message_"})
public class WorkflowActivityState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -9164459235845230446L;

    @XmlElement(name = "activity")
    private WorkflowActivityDescription activity_;
    
    @XmlElement(name = "state")
    private String                      state_;
    
    @XmlElement(name = "update-time")
    private Date                        updateTime_;
    
    @XmlElement(name = "update-message")
    private String                      message_;
    
    public WorkflowActivityState(WorkflowActivityDescription activity)
    {
        this.activity_ = checkNotNull(activity);
        checkState(activity.getId() != null);
    }
    
    public WorkflowActivityState()
    {
        super();
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime_;
    }

    /**
     * @param updateTime the updateTime to set
     */
    public WorkflowActivityState setUpdateTime(Date updateTime)
    {
        this.updateTime_ = updateTime;
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
     * @param message the message to set
     */
    public WorkflowActivityState setMessage(String message)
    {
        this.message_ = message;
        return this;
    }

    /**
     * @return the activity
     */
    public WorkflowActivityDescription getActivity()
    {
        return activity_;
    }
    
    public WorkflowActivityState setActivity(WorkflowActivityDescription activity_)
    {
        this.activity_ = activity_;
        return this;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state_;
    }

    /**
     * @param state the state to set
     */
    public WorkflowActivityState setState(String state)
    {
        this.state_ = state;
        return this;
    }
}
