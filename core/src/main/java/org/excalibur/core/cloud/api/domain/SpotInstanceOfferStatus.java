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

public class SpotInstanceOfferStatus implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 5621699499980690828L;
    
    private String code_;
    private Date   updateTime_;
    private String message_;

    /**
     * @return the code
     */
    public String getCode()
    {
        return code_;
    }

    /**
     * @param code
     *            the code to set
     */
    public SpotInstanceOfferStatus setCode(String code)
    {
        this.code_ = code;
        return this;
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime_;
    }

    /**
     * @param updateTime
     *            the updateTime to set
     */
    public SpotInstanceOfferStatus setUpdateTime(Date updateTime)
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
     * @param message
     *            the message to set
     */
    public SpotInstanceOfferStatus setMessage(String message)
    {
        this.message_ = message;
        return this;
    }
}
