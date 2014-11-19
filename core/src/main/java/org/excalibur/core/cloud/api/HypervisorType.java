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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "hypervisor")
@XmlEnum(String.class)
public enum HypervisorType
{
    @XmlEnumValue("ovm")
    OVM(1, "ovm"),

    @XmlEnumValue("xen")
    XEN(2, "xen");

    private final Integer id_;
    private final String value_;

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @return the description
     */
    public String getValue()
    {
        return value_;
    }

    private HypervisorType(Integer id, String description)
    {
        this.id_ = id;
        this.value_ = description;
    }

    public static HypervisorType valueOf(Integer id)
    {
        for (HypervisorType value : values())
        {
            if (value.getId().equals(id))
            {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid %s %s.", id, HypervisorType.class.getSigners()));
    }
    
    public static HypervisorType valueOfFromValue(String value)
    {
        for (HypervisorType type : values())
        {
            if (type.getValue().equalsIgnoreCase(value))
            {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid %s %s.", value, HypervisorType.class.getSimpleName()));
    }

}
