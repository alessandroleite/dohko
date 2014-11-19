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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "metric-value")
public class MetricValue implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 3463543048608518086L;

    @XmlElement(name = "value")
    private Object value_;

    @XmlElement(name = "value-type")
    private MetricValueType valueType_;

    @XmlRootElement(name = "metric-value-type")
    @XmlEnum
    public enum MetricValueType implements Serializable
    {
        /**
         * Numeric value. Example: 1.02, 0.73.
         */
        NUMERIC,

        /**
         * A text value.
         */
        TEXT,

        /**
         * A enumeration.
         */
        ENUMERATION;
    }

    /**
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public <V> V getValue()
    {
        return (V) value_;
    }

    /**
     * @param value
     *            the value to set
     */
    public MetricValue setValue(Object value)
    {
        this.value_ = value;
        if (value != null)
        {
            if (Number.class.isAssignableFrom(value.getClass()))
            {
                this.valueType_ = MetricValueType.NUMERIC;
            }
            else if (String.class.isAssignableFrom(value.getClass()))
            {
                this.valueType_ = ((String) value).contains(",") ? MetricValueType.ENUMERATION : MetricValueType.TEXT;
            }
        }
        return this;
    }

    /**
     * @return the valueType
     */
    public MetricValueType getValueType()
    {
        return valueType_;
    }

    /**
     * @param valueType
     *            the valueType to set
     */
    public MetricValue setValueType(MetricValueType valueType)
    {
        this.valueType_ = valueType;
        return this;
    }
}
