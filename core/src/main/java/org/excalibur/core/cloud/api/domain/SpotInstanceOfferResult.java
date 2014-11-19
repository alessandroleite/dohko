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

import org.excalibur.core.cloud.api.VirtualMachine;

public class SpotInstanceOfferResult implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6734562288536244525L;

    private Integer                    id_;
    private String                     spotRequestId;
    private SpotInstanceOffer          offerRequest_;
    private Date                       createTime_;
    private SpotInstanceOfferStateType state_;
    private SpotInstanceOfferStatus    status_;
    private VirtualMachine             instance_;

    /**
     * @return the time
     */
    public Date getCreateTime()
    {
        return createTime_;
    }

    /**
     * @param time
     *            the time to set
     */
    public SpotInstanceOfferResult setCreateTime(Date time)
    {
        this.createTime_ = time;
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
    public SpotInstanceOfferResult setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the spotRequestId
     */
    public String getSpotRequestId()
    {
        return spotRequestId;
    }

    /**
     * @param id
     *            the id to set
     */
    public SpotInstanceOfferResult setSpotRequestId(String id)
    {
        this.spotRequestId = id;
        return this;
    }

    /**
     * @return the offer
     */
    public SpotInstanceOffer getOfferRequest()
    {
        return offerRequest_;
    }

    /**
     * @param request
     *            the request to set
     */
    public SpotInstanceOfferResult setOfferRequest(SpotInstanceOffer request)
    {
        this.offerRequest_ = request;
        return this;
    }

    /**
     * @return the state
     */
    public SpotInstanceOfferStateType getState()
    {
        return state_;
    }

    /**
     * @param state
     *            the state to set
     */
    public SpotInstanceOfferResult setState(SpotInstanceOfferStateType state)
    {
        this.state_ = state;
        return this;
    }

    /**
     * @return the status
     */
    public SpotInstanceOfferStatus getStatus()
    {
        return status_;
    }

    /**
     * @param status
     *            the status to set
     */
    public SpotInstanceOfferResult setStatus(SpotInstanceOfferStatus status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the instance
     */
    public VirtualMachine getInstance()
    {
        return this.instance_ == null ? (instance_ = new VirtualMachine()) : this.instance_;
    }

    public SpotInstanceOfferResult setInstance(VirtualMachine instance)
    {
        this.instance_ = instance;

        return this;
    }
}
