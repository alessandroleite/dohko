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
package org.excalibur.core.compute.monitoring.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "memory-type")
@XmlEnum(String.class)
public enum MemoryType
{
    @XmlEnumValue("RAM")
    RAM(1),
    
    @XmlEnumValue("SWAP")
    SWAP(2), 
    
    @XmlEnumValue("CACHE")
    CACHE(3);
    
    private final Integer id_;

    private MemoryType(Integer id)
    {
        this.id_ = id;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }
    
    public static MemoryType valueOf(Integer id)
    {
        for(MemoryType value: values())
        {
            if (value.getId().equals(id))
            {
                return value;
            }
        }
        throw new IllegalStateException("Invalid memory type " + id);
    }
}
