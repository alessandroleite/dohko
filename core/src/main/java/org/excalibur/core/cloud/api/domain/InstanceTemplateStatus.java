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
package org.excalibur.core.cloud.api.domain;

import java.io.Serializable;
import java.util.Date;

import org.excalibur.core.Status;

public class InstanceTemplateStatus implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 1628727182294291751L;

    private Status status_;
    private String message_;
    private Date statusTime_;

    public InstanceTemplateStatus()
    {
        this.statusTime_ = new Date();
    }

    /**
     * @return the status_
     */
    public Status getStatus_()
    {
        return status_;
    }

    /**
     * @param status_
     *            the status_ to set
     */
    public InstanceTemplateStatus setStatus(Status status)
    {
        this.status_ = status;
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
    public InstanceTemplateStatus setMessage(String message)
    {
        this.message_ = message;
        return this;
    }

    /**
     * @return the statusTime_
     */
    public Date getStatusTime()
    {
        return statusTime_;
    }

    /**
     * @param statusTime
     *            the statusTime_ to set
     */
    public InstanceTemplateStatus setStatusTime(Date statusTime)
    {
        this.statusTime_ = statusTime;
        return this;
    }
}
