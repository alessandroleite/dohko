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
package org.excalibur.metrics;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metric")
@XmlType(name = "metric", propOrder = { "name_", "type_", "unit_" })
public class Metric implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 900830369567349230L;
   
    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "type", required = true)
    private MetricType type_ = MetricType.RESOURCE;

    @XmlElement(name = "unit", required = true)
    private MetricUnit unit_;

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public Metric setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the unit
     */
    public MetricUnit getUnit()
    {
        return unit_;
    }

    /**
     * @param unit
     *            the unit to set
     */
    public Metric setUnit(MetricUnit unit)
    {
        this.unit_ = unit;
        return this;
    }

    /**
     * @return the type
     */
    public MetricType getType()
    {
        return type_;
    }

    /**
     * @param type
     *            the type to set
     */
    public Metric setType(MetricType type)
    {
        this.type_ = type;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Metric))
        {
            return false;
        }
        
        Metric other = (Metric) obj;
        return Objects.equal(this.getName(), other.getName());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("metric", this.getName())
                .add("type", this.getType())
                .add("unit", this.getUnit())
                .omitNullValues()
                .toString();
    }
}
