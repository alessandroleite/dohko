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
package org.excalibur.core;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "interval")
@XmlType(name = "interval", propOrder = { "min_", "max_" })
public class Interval implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -2743852553105056487L;

    @XmlAttribute(name = "min")
    private Integer min_;

    @XmlAttribute(name = "max")
    private Integer max_;

    public Interval()
    {
        super();
    }

    public Interval(Interval that)
    {
        this.setMin(that.min_).setMax(that.max_);
    }

    /**
     * @return the min
     */
    public Integer getMin()
    {
        return min_;
    }

    /**
     * @param min
     *            the min to set
     */
    public Interval setMin(Integer min)
    {
        this.min_ = min;
        return this;
    }

    /**
     * @return the max
     */
    public Integer getMax()
    {
        return max_;
    }

    /**
     * @param max
     *            the max to set
     */
    public Interval setMax(Integer max)
    {
        this.max_ = max;
        return this;
    }

    @Override
    protected Interval clone()
    {
        Interval clone;

        try
        {
            clone = (Interval) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Interval(this);
        }
        return clone;
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		          .omitNullValues()
        		          .add("min", this.getMin())
        		          .add("max", getMax())
        		          .toString();
    }

}
