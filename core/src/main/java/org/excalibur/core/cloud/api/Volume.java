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
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "volume")
@XmlType(name = "volume", propOrder = {"owner_", "name_", "type_", "sizeGb_", "iops_", "zone_", "createdIn_", "deletedIn_" })
public class Volume implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6841017759523204037L;

    @XmlAttribute(name = "id")
    private Integer id_;
    
    @XmlElement(name = "owner")
    private User owner_;

    @XmlElement(name = "name", required = true)
    private String name_;

    @XmlElement(name = "type")
    private VolumeType type_;

    @XmlElement(name = "size-gb")
    private Integer sizeGb_;

    @XmlElement(name = "iops")
    private Integer iops_;

    @XmlElement(name = "zone")
    private Zone zone_;
    
    @XmlElement(name = "created-in", nillable = false)
    private Date createdIn_;

    @XmlElement(name = "deleted-in")
    private Date deletedIn_;

    public Volume()
    {
        super();
    }

    /**
     * @return the createIn
     */
    public Date getCreatedIn()
    {
        return createdIn_;
    }

    /**
     * @param createdIn
     *            the createdIn to set
     */
    public Volume setCreatedIn(Date createdIn)
    {
        this.createdIn_ = createdIn;
        return this;
    }

    /**
     * @return the deletedIn
     */
    public Date getDeletedIn()
    {
        return deletedIn_;
    }

    /**
     * @param deletedIn
     *            the deletedIn to set
     */
    public Volume setDeletedIn(Date deletedIn)
    {
        this.deletedIn_ = deletedIn;
        return this;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public Volume setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the iops
     */
    public Integer getIops()
    {
        return iops_;
    }

    /**
     * @param iops
     *            the iops to set
     */
    public Volume setIops(Integer iops)
    {
        this.iops_ = iops;
        return this;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public Volume setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the type
     */
    public VolumeType getType()
    {
        return type_;
    }

    /**
     * @param type
     *            the type to set
     */
    public Volume setType(VolumeType type)
    {
        this.type_ = type;
        return this;
    }

    /**
     * @return the size
     */
    public Integer getSizeGb()
    {
        return sizeGb_;
    }

    /**
     * @param size
     *            the size to set
     */
    public Volume setSizeGb(Integer size)
    {
        this.sizeGb_ = size;
        return this;
    }
    

    /**
     * @return the owner
     */
    public User getOwner()
    {
        return owner_;
    }

    /**
     * @param owner the owner to set
     */
    public Volume setOwner(User owner)
    {
        this.owner_ = owner;
        return this;
    }

    /**
     * @return the zone
     */
    public Zone getZone()
    {
        return zone_;
    }

    /**
     * @param zone
     *            the zone to set
     */
    public Volume setZone(Zone zone)
    {
        this.zone_ = zone;
        return this;
    }
    
    

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Volume))
        {
            return false;
        }

        Volume other = (Volume) obj;
        return (this.getId() != null && Objects.equal(this.getId(), other.getId())) ||
                Objects.equal(this.getName(), other.getName());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("id", this.getId())
                .add("iops", this.getIops())
                .add("name", getName())
                .add("size(GiB)", getSizeGb())
                .add("type", getType())
                .add("zone", getZone())
                .toString();
    }

}
