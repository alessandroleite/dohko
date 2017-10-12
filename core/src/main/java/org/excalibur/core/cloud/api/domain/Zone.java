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
package org.excalibur.core.cloud.api.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "zone")
@XmlType(name = "zone", propOrder = { "name_", "status_" })
public class Zone implements Serializable, Cloneable, Comparable<Zone>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7668878135589160634L;

//    @XmlAttribute(name = "id")
    @XmlTransient
    private Integer id_;

    @XmlElement(name = "name")
    private String name_;

    @XmlElement(name = "status")
    private String status_;

    @XmlTransient
    private Region region_;

    public Zone()
    {
        super();
    }

    public Zone(Zone that)
    {
        this.setId(that.getId())
            .setName(that.getName())
            .setRegion(that.getRegion())
            .setStatus(that.getStatus());
    }

    /**
     * @return the id_
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id_
     *            the id_ to set
     */
    public Zone setId(Integer id)
    {
        this.id_ = id;

        return this;
    }

    /**
     * @return the name_
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name_
     *            the name_ to set
     */
    public Zone setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the status_
     */
    public String getStatus()
    {
        return status_;
    }

    /**
     * @param status_
     *            the status_ to set
     */
    public Zone setStatus(String status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the region_
     */
    public Region getRegion()
    {
        return region_;
    }

    /**
     * @param region_
     *            the region_ to set
     */
    public Zone setRegion(Region region)
    {
        this.region_ = region;
        return this;
    }

    @Override
    public Zone clone()
    {
        Zone clone;

        try
        {
            clone = (Zone) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Zone(this);
        }
        return clone;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId(), this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Zone))
        {
            return false;
        }

        Zone other = (Zone) obj;

        return Objects.equal(this.getId(), other.getId()) || Objects.equal(this.getName(), other.getName());

    }

    @Override
    public int compareTo(Zone that)
    {
        return this.getName().compareTo(that.getName());
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("id", this.getId())
                .add("name", this.getName())
                .add("status", this.getStatus())
                .add("region", this.getRegion())
                .omitNullValues()
                .toString();
    }
}
