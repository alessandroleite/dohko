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

@XmlRootElement(name="virtualization-type")
@XmlEnum(String.class)
public enum VirtualizationType
{
    /**
     * 
     */
    @XmlEnumValue("HVM")
    HVM (1, "hvm"),
    
    /**
     * 
     */
    @XmlEnumValue("PARAVIRTUAL")
    PARAVIRTUAL(2, "paravirtual"),
    
    @XmlEnumValue("ANY")
    ANY(3, "any");
    
    private final Integer id_;
    private final String value_;

    private VirtualizationType(Integer id, String description)
    {
        this.id_ = id;
        this.value_ = description;
    }

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
    
    public static VirtualizationType valueOf(Integer id)
    {
        for (VirtualizationType value : values())
        {
            if (value.getId().equals(id))
            {
                return value;
            }
        }
        throw new IllegalStateException(String.format("Invalid %s %s.", id, VirtualizationType.class.getSimpleName()));
    }
    
    public static VirtualizationType valueOfFromValue(String name)
    {
        for (VirtualizationType value : values())
        {
            if (value.getValue().equals(name))
            {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid %s:%s.", name, VirtualizationType.class.getSimpleName()));
    }
}
