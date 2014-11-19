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
import java.math.BigDecimal;
import java.util.Date;

import org.excalibur.core.cloud.api.InstanceType;

import com.google.common.base.Objects;

public class SpotPriceHistory implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6761701279784226016L;
    
    private InstanceType     instanceType_;
    private Date       time_;
    private Region     region_;
    private BigDecimal price_;
    private String     imageTypeDescription_;
    
    public SpotPriceHistory withInstanceType(InstanceType type)
    {
        this.instanceType_ = type;
        return this;
    }
    
    public SpotPriceHistory withImageTypeDescription(String description)
    {
        this.imageTypeDescription_ = description;
        return this;
    }
    
    public SpotPriceHistory withPrice(BigDecimal price)
    {
        this.price_ = price;
        return this;
    }
    
    public SpotPriceHistory withPrice(String price)
    {
        return this.withPrice(new BigDecimal(price));
    }
    
    public SpotPriceHistory withTime(Date time)
    {
        this.time_ = time;
        return this;
    }
    
    public SpotPriceHistory withRegion(Region region)
    {
        this.region_ = region;
        return this;
    }
    
    /**
     * @return the instanceType
     */
    public InstanceType getInstanceType()
    {
        return instanceType_;
    }
    
    /**
     * @return the imageTypeDescription
     */
    public String getImageTypeDescription()
    {
        return imageTypeDescription_;
    }

    /**
     * @param imageTypeDescription the imageTypeDescription to set
     */
    public void setImageTypeDescription_(String imageTypeDescription)
    {
        this.imageTypeDescription_ = imageTypeDescription;
    }

    /**
     * @param instanceType the instanceType to set
     */
    public void setInstanceType(InstanceType instanceType)
    {
        this.instanceType_ = instanceType;
    }

    /**
     * @return the time
     */
    public Date getTime()
    {
        return time_;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time)
    {
        this.time_ = time;
    }

    /**
     * @return the zone
     */
    public Region getRegion()
    {
        return region_;
    }

    /**
     * @param zone the zone to set
     */
    public void setRegion(Region zone)
    {
        this.region_ = zone;
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice()
    {
        return price_;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price)
    {
        this.price_ = price;
    }
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).omitNullValues().toString();
    }
}
