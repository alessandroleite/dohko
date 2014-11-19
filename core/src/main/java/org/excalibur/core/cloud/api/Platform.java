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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "platform")
@XmlEnum(String.class)
public enum Platform
{
    /**
     * 
     */
    LINUX("Linux")
    {
        @Override
        public String[] instances()
        {
            return new String[] { "CentOS", "Debian", "Red Hat", "Ubuntu" };
        }
    },

    /**
     * 
     */
    WINDOWS("Windows")
    {
        @Override
        public String[] instances()
        {
            return new String[] {};
        }
    };

    public abstract String[] instances();

    private final String value_;

    /**
     * @return the value
     */
    public String getValue()
    {
        return value_;
    }

    private Platform(String value)
    {
        this.value_ = value;
    }

    public static Platform valueOfFromValue(String value)
    {
        for (Platform type : values())
        {
            if (type.getValue().equalsIgnoreCase(value))
            {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid %s %s.", value, Platform.class.getSimpleName()));
    }
}
