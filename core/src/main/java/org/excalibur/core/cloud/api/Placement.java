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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "instance-location")
@XmlType(name = "instance-location")
public class Placement implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -5337954226824913439L;

    @XmlElement(name = "group")
    private String groupName_;

    @XmlElement(name = "zone_")
    private String zone_;

    public Placement()
    {
        super();
    }

    /**
     * @return the groupName_
     */
    public String getGroupName()
    {
        return groupName_;
    }

    /**
     * @param groupName_
     *            the groupName_ to set
     */
    public Placement setGroupName(String groupName)
    {
        this.groupName_ = groupName;
        return this;
    }

    /**
     * @return the zone_
     */
    public String getZone()
    {
        return zone_;
    }

    /**
     * @param zone_
     *            the zone_ to set
     */
    public Placement setZone(String zone)
    {
        this.zone_ = zone;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getGroupName(), this.getZone());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Placement))
        {
            return false;
        }

        Placement other = (Placement) obj;

        return Objects.equal(this.getGroupName(), other.getGroupName()) && Objects.equal(this.getZone(), other.getZone());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("group", getGroupName())
        		.add("zone", getZone())
        		.omitNullValues()
        		.toString();
    }

    @Override
    public Placement clone()
    {
        Object clone;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Placement().setGroupName(groupName_).setZone(zone_);
        }

        return (Placement) clone;
    }
}
