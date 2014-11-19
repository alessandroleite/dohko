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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.excalibur.core.cloud.api.InstanceType;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SpotPriceHistoryRequest implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 500643955172978861L;
    
    private final List<InstanceType> instanceTypes_ = new ArrayList<InstanceType>();
    
    /**
     * The virtual machine images type. For instance: Linux/Unix; Linux/SUSE, etc. 
     */
    private final List<String> imageTypes = new ArrayList<String>();
    
    private Region             region_;
    private Date               from_;
    private Date               until_;
    private Integer            maxResult_;
    
    
    public SpotPriceHistoryRequest withFrom(Date from)
    {
        if (from != null)
        {
            if (until_ != null && from.getTime() > until_.getTime())
            {
                
                throw new IllegalArgumentException("The start date is after than the final date!");
            }
        }
        
        this.from_ = from;
        return this;
    }
    
    public SpotPriceHistoryRequest withimageTypes (String ... descriptions)
    {
        if (descriptions != null)
        {
            for(String description: descriptions)
            {
                if (!Strings.isNullOrEmpty(description))
                {
                    this.imageTypes.add(description);
                }
            }
        }
        return this;
    }
    
    public SpotPriceHistoryRequest withInstanceTypes (InstanceType ... types)
    {
        if (types != null)
        {
            for (InstanceType type: types)
            {
                if (type != null && !Strings.isNullOrEmpty(type.getName()))
                {
                    this.instanceTypes_.add(type);
                }
            }
        }
        return this;
    }
    
    public SpotPriceHistoryRequest withMaximumResult(Integer maximumResult)
    {
        Preconditions.checkState(maximumResult != null ? maximumResult > 0 : true);
        
        this.maxResult_ = maximumResult;
        return this;
    }
    
    public SpotPriceHistoryRequest withRegion(Region region)
    {
        this.region_ = region;
        return this;
    }
    
    public SpotPriceHistoryRequest withUntil(Date until)
    {
        if (until != null)
        {
            if (from_ != null && from_.getTime() > until.getTime())
            {
                throw new IllegalArgumentException("The start date is after the final date!");
            }
        }
        this.until_ = until;
        
        return this;
    }
    
    /**
     * @return the from
     */
    public Date getFrom()
    {
        return from_;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(Date from)
    {
        this.withFrom(from);
    }

    /**
     * @return the until
     */
    public Date getUntil()
    {
        return until_;
    }

    /**
     * @param until the until to set
     */
    public void setUntil(Date until)
    {
        this.withUntil(until);
    }

    /**
     * @return the maxResult
     */
    public Integer getMaxResult()
    {
        return maxResult_;
    }

    /**
     * @param maxResult the maxResult to set
     */
    public void setMaxResult(Integer maxResult)
    {
        withMaximumResult(maxResult); 
    }

    /**
     * @return the region
     */
    public Region getRegion()
    {
        return region_;
    }

    /**
     * @return the instanceTypes
     */
    public List<InstanceType> getInstanceTypes()
    {
        return Collections.unmodifiableList(instanceTypes_);
    }

    /**
     * @return the imageTypes
     */
    public List<String> getImageTypes()
    {
        return Collections.unmodifiableList(imageTypes);
    }
    
}
