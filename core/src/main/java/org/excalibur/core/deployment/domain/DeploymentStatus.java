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
package org.excalibur.core.deployment.domain;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "status")
@XmlEnum
public enum DeploymentStatus
{
    /**
     * 
     */
    @XmlEnumValue("SUBMITTED")
    SUBMITTED,

    /**
     * 
     */
    @XmlEnumValue("INVALID")
    INVALID,

    /**
     * 
     */
    @XmlEnumValue("EXECUTING")
    EXECUTING,

    /**
     * 
     */
    @XmlEnumValue("FINISHED")
    FINISHED,

    /**
     */
    @XmlEnumValue("FAILED")
    FAILED,
    
    /**
     * 
     */
    @XmlEnumValue("UNKNOWN")
    UNKNOWN;

    public String value()
    {
        return name();
    }

    public static DeploymentStatus fromValue(String value)
    {
        return valueOf(value);
    }
}
