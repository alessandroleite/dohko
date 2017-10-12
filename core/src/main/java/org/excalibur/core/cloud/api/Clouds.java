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
package org.excalibur.core.cloud.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.collect.Lists.*;
import static com.google.common.base.Joiner.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "clouds")
@Deprecated
public class Clouds implements Iterable<Cloud>, Serializable, Cloneable
{
	/**
     *  Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = 8318092973518116800L;
	
    @XmlElement(name = "cloud")
    private final List<Cloud> clouds_ = newCopyOnWriteArrayList();

    @Nonnull
    public Clouds add(@Nullable final Cloud cloud)
    {
        if (cloud != null)
        {
            clouds_.add(cloud);
        }

        return this;
    }

    @Nonnull
    public Clouds addAll(@Nullable final Iterable<Cloud> clouds)
    {
        if (clouds != null)
        {
            for (Cloud cloud : clouds)
            {
                this.add(cloud);
            }
        }
        return this;
    }
    
    @Nonnull
    public Clouds remove(@Nullable final Cloud cloud)
    {
        this.clouds_.remove(cloud);
        return this;
    }

    @Nonnull
    public List<Cloud> getClouds()
    {
        return Collections.unmodifiableList(this.clouds_);
    }

    @Override
    public Iterator<Cloud> iterator()
    {
        return getClouds().iterator();
    }

    public int size()
    {
        return clouds_.size();
    }

    public boolean isEmpty()
    {
        return clouds_.isEmpty();
    }

    public void clear()
    {
        clouds_.clear();
    }
    
    @Override
    public Clouds clone() 
    {
        Clouds cloned;
        
        try
        {
            cloned = (Clouds) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new Clouds();
        }
        
        cloned.clear();
        
        for (Cloud cloud: this.clouds_)
        {
            cloned.add(cloud.clone());
        }
        
        return cloned;
    }
    
    @Override
    public String toString()
    {
        return on(",").join(clouds_);
    }
}
