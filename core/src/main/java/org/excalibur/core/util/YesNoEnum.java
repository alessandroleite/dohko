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
package org.excalibur.core.util;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Strings;

@XmlRootElement(name = "status")
@XmlEnum(String.class)
public enum YesNoEnum
{
    @XmlEnumValue("Y")
    YES('Y'),

    @XmlEnumValue("N")
    NO('N');

    private final char id;

    private YesNoEnum(char id)
    {
        this.id = id;
    }

    /**
     * @return the id
     */
    public char getId()
    {
        return id;
    }

    public static YesNoEnum valueOf(char id)
    {
        for (YesNoEnum value : YesNoEnum.values())
        {
            if (value.getId() == id)
            {
                return value;
            }
        }
        return null;
    }

    public boolean toBoolean()
    {
        return this.getId() == 'Y';
    }

    public static YesNoEnum valueOfFrom(String value)
    {
        if (Strings.isNullOrEmpty(value))
        {
            return YesNoEnum.NO;
        }
        
        for (YesNoEnum val: values())
        {
            if (val.getId() == value.charAt(0))
            {
                return val;
            }
        }
        
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
