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

@XmlRootElement(name = "state-type")
@XmlEnum(String.class)
public enum InstanceStateType
{
    /**
     * 
     */
    @XmlEnumValue("PENDING")
    PENDING(1, "Pending"),

    /**
     * Resources are being reserved for the instance. The instance isn't running yet.
     */
    @XmlEnumValue("PROVISIONING")
    PROVISIONING(2, "Provisioning"),

    /**
     * Resources have been acquired and the instance is being prepared for launch.
     */
    @XmlEnumValue("STAGING")
    STAGING(3, "Staging"),

    /**
     * The instance is booting up or running.
     */
    @XmlEnumValue("RUNNING")
    RUNNING(4, "Running"),

    /**
     * 
     */
    @XmlEnumValue("SHUTTING_DOWN")
    SHUTTING_DOWN(5, "Shutting-down"),

    /**
     * 
     */
    @XmlEnumValue("STOPPING")
    STOPPING(6, "Stopping"),

    /**
     * 
     */
    @XmlEnumValue("STOPPED")
    STOPPED(7, "Stopped"),

    /**
     * 
     */
    @XmlEnumValue("TERMINATED")
    TERMINATED(8, "Terminated"),

    /**
     * <p>
     * There is a new instance in the system.
     * <p>
     * <strong>Notice:</strong> This event is only for internal usage.
     */
    @XmlEnumValue("CREATED")
    CREATED(9, "Created"),

    /**
     * <p>
     * The event of an instance changed. The exactly new state is given by the instance.
     * 
     * <p>
     * <strong>Notice:</strong> This event is only for internal usage.
     */
    @XmlEnumValue("UPDATED")
    UPDATED(10, "Updated");

    /**
     * A 16-bit unsigned integer. The high byte is an opaque internal value and should be ignored. The low byte is set based on the state represented.
     */
    private final Integer id_;

    /**
     * The current state of the instance.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>pending, running, shutting-down, terminated, stopping, stopped.
     */
    private final String name_;

    private InstanceStateType(Integer id, String name)
    {
        this.id_ = id;
        this.name_ = name;
    }

    public Integer getId()
    {
        return id_;
    }

    public String getName()
    {
        return name_;
    }

    public static InstanceStateType valueOfFrom(String name)
    {
        for (InstanceStateType state : values())
        {
            if (state.getName().equalsIgnoreCase(name))
            {
                return state;
            }
        }

        throw new IllegalStateException("Unknown state: " + name);
    }

    public static InstanceStateType valueOf(Integer id)
    {
        for (InstanceStateType state : values())
        {
            if (state.getId().equals(id))
            {
                return state;
            }
        }

        throw new IllegalStateException("Unknown state: " + id);
    }
}
