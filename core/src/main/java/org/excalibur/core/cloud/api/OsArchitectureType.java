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

@XmlRootElement(name = "os-architecture-type")
@XmlEnum(String.class)
public enum OsArchitectureType
{
    @XmlEnumValue("i386")
    I386(1, "i386"),

    @XmlEnumValue("x86_64")
    X86_64(2, "x86_64");

    private final Integer id_;
    private final String  value_;

    private OsArchitectureType(Integer id, String value)
    {
        this.id_ = id;
        this.value_ = value;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }
    
    public String getValue()
    {
        return value_;
    }

    public static OsArchitectureType valueOf(Integer id)
    {
        for (OsArchitectureType value : values())
        {
            if (value.getId().equals(id))
            {
                return value;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid %s %s.", id, OsArchitectureType.class.getSimpleName()));
    }
    
    public static OsArchitectureType valueOfFromValue(String value)
    {
        for (OsArchitectureType type : values())
        {
            if (type.getValue().equalsIgnoreCase(value))
            {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid %s %s.", value, OsArchitectureType.class.getSimpleName()));
    }

}
