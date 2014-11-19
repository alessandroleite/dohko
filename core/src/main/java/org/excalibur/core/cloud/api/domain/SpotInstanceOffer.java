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

import java.math.BigDecimal;
import java.util.Date;

import com.google.common.base.Preconditions;

public class SpotInstanceOffer extends InstanceTemplate
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 7788936184973818290L;

    /**
     * Specifies the maximum hourly price for any spot instance launched to fulfill the request.
     */
    private BigDecimal offerValue_ = BigDecimal.ZERO;

    /**
     * <p>
     * Defines the start date of the request.
     * </p>
     * The request becomes active at this date and time and remains active until all instances launch, the request expires, or the request is
     * canceled.
     */
    private Date validFrom_;

    /**
     * The type of spot instance request. The default is {@link SpotType#ONE_TIME}.
     * @see SpotType
     */
    private SpotType type_ = SpotType.ONE_TIME;
    
    /**
     * <p>
     * End date of the request.
     * </p>
     * The request remains active until all instances launch, the request is canceled, or this date is reached.
     */
    private Date validUntil_;
    

    /**
     * @return the spotPrice
     */
    public BigDecimal getOfferValue()
    {
        return offerValue_;
    }

    /**
     * @param spotPrice
     *            the spotPrice to set
     */
    public SpotInstanceOffer setOfferValue(BigDecimal spotPrice)
    {
        this.offerValue_ = spotPrice;
        return this;
    }

    public SpotInstanceOffer setOfferValue(double spotPrice)
    {
        return this.setOfferValue(String.valueOf(spotPrice));
    }

    public SpotInstanceOffer setOfferValue(String spotPrice)
    {
        return this.setOfferValue(new BigDecimal(spotPrice));
    }
    
    public SpotInstanceOffer setNumberOfInstances(int value)
    {
        Preconditions.checkState(value > 0);
        this.setMaxCount(value);
        return this;
    }
    

    /**
     * @return the validFrom
     */
    public Date getValidFrom()
    {
        return validFrom_;
    }

    /**
     * @param validFrom
     *            the validFrom to set
     */
    public SpotInstanceOffer setValidFrom(Date validFrom)
    {
        this.validFrom_ = validFrom;
        return this;
    }

    /**
     * @return the validUntil
     */
    public Date getValidUntil()
    {
        return validUntil_;
    }

    /**
     * @param validUntil
     *            the validUntil to set
     */
    public SpotInstanceOffer setValidUntil(Date validUntil)
    {
        this.validUntil_ = validUntil;
        return this;
    }

    /**
     * @return the type
     */
    public SpotType getType()
    {
        return type_;
    }

    /**
     * @param type
     *            the type to set
     */
    public SpotInstanceOffer setType(SpotType type)
    {
        this.type_ = type;
        return this;
    }
    
    public Integer getNumberOfInstances()
    {
        return (this.getMaxCount() == null && this.getMinCount() == null) ? 1 : getMaxCount() == null ? getMinCount() : getMaxCount();
    }
}
