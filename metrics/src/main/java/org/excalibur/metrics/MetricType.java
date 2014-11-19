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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metric-type")
@XmlEnum(Integer.class)
public enum MetricType
{
    /**
     * 
     */
    RESOURCE(1),

    /**
     * 
     */
    CLOUD(2);

    private final int id_;

    private MetricType(int id)
    {
        this.id_ = id;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id_;
    }

    public static MetricType valueOf(Integer id)
    {
        for (MetricType value : values())
        {
            if (value.id_ == id)
            {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid metric type [%s]"));
    }
}
