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
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metric-unit")
public class MetricUnit implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -5567757476544166614L;

    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlAttribute(name = "symbol", required = true)
    private String symbol_;

    public MetricUnit()
    {
        super();
    }

    public MetricUnit(String name, String symbol)
    {
        this.name_ = name;
        this.symbol_ = symbol;
    }

    public MetricUnit(MetricUnit that)
    {
        this.name_ = that.name_;
        this.symbol_ = that.symbol_;
    }

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
    public MetricUnit setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the symbol
     */
    public String getSymbol()
    {
        return symbol_;
    }

    /**
     * @param symbol
     *            the symbol to set
     */
    public MetricUnit setSymbol(String symbol)
    {
        this.symbol_ = symbol;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof MetricUnit))
        {
            return false;
        }

        MetricUnit other = (MetricUnit) obj;
        return Objects.equal(this.getName(), other.getName()) && Objects.equal(this.getSymbol(), other.getSymbol());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName());
    }

    @Override
    public Object clone()
    {
        Object clone;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new MetricUnit(this);
        }
        return clone;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("name", this.getName()).add("symbol", this.getSymbol()).omitNullValues().toString();
    }
}
