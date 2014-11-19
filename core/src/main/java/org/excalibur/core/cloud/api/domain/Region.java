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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "region")
@XmlType(name = "region")
public class Region implements Serializable, Comparable<Region>, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -649742839351214230L;

    /**
     * The instance id_.
     */
    @XmlTransient
    private Integer id_;

    /**
     * The region name; ex: us-east-1.
     */
    @XmlElement(name = "name")
    private String name_;

    /**
     * The region's endpoint_.
     */
    @XmlElement(name = "endpoint")
    private String endpoint_;

    /**
     * The status of the region. It can be Up or Down.
     */
    @XmlElement(name = "status")
    private RegionStatus status_ = RegionStatus.UP;

    /**
     * city of the region.
     */
    @XmlElement(name = "city")
    private String city_;

    @XmlElement(name = "geographic-region")
    private GeographicRegion geographicRegion_;

    @XmlElement(name = "zone")
    @XmlElementWrapper(name = "zones")
    private final List<Zone> zones_ = new ArrayList<Zone>();

    /**
     * The services available in this {@link Region}.
     */
    @XmlTransient
    private final Services services_ = new Services();

    /**
     * Creates a new {@link Region} with the given name.
     * 
     * @param name
     *            The region's name. Might not be <code>null</code>.
     */
    public Region(final String name)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        this.name_ = name;
    }

    public Region()
    {
        super();
    }

    public Region(Integer id, String name)
    {
        this(name);
        this.id_ = id;
    }

    public Region addZone(Zone zone)
    {
        if (zone != null)
        {
            this.zones_.add(zone.setRegion(this));
        }

        return this;
    }
    
    public Region addZones(Iterable<Zone> zones)
    {
        for (Zone zone: zones)
        {
            this.addZone(zone);
        }
        
        return this;
    }

    public Region removeZone(Zone zone)
    {
        this.zones_.remove(zone);
        return this;
    }

    /**
     * @return the id_
     */
    public Integer getId()
    {
        return id_;
    }

    public Region setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the status
     */
    public RegionStatus getStatus()
    {
        return status_;
    }

    public Region setStatus(RegionStatus status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    public Region setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the endpoint_
     */
    public String getEndpoint()
    {
        return endpoint_;
    }

    /**
     * @param endpoint_
     *            the endpoint_ to set
     */
    public Region setEndpoint(String endpoint)
    {
        this.endpoint_ = endpoint;
        return this;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return city_;
    }

    /**
     * @param city
     *            the city to set
     */
    public Region setCity(String city)
    {
        this.city_ = city;
        return this;
    }

    /**
     * @return the services
     */
    public Services getServices()
    {
        return services_;
    }
    
    /**
     * @return the geographicRegion
     */
    public GeographicRegion getGeographicRegion()
    {
        return geographicRegion_;
    }

    /**
     * @param geographicRegion the geographicRegion to set
     */
    public Region setGeographicRegion(GeographicRegion geographicRegion)
    {
        this.geographicRegion_ = geographicRegion;
        
        return this;
    }

    /**
     * @return the zones_
     */
    public List<Zone> getZones()
    {
        return Lists.transform(this.zones_, new Function<Zone, Zone>()
        {
            @Override
            @Nullable
            public Zone apply(@Nullable Zone input)
            {
                return input.setRegion(Region.this);
            }
        });
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName(), this.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Region))
        {
            return false;
        }

        Region other = (Region) obj;

        return Objects.equal(this.getName(), other.getName()) || Objects.equal(this.getId(), other.getId());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("id", getId()).add("name", getName()).add("endpoint", this.getEndpoint()).omitNullValues().toString();
    }

    @Override
    public int compareTo(Region that)
    {
        return this.getName().compareTo(that.getName());
    }
    
    @Override
    public Region clone() 
    {
        Region cloned;
        
        try
        {
            cloned = (Region) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new Region()
                    .setCity(this.getCity())
                    .setEndpoint(this.getEndpoint())
                    .setGeographicRegion(this.getGeographicRegion().clone())
                    .setName(this.getName()).setStatus(this.getStatus());
        }
        
        return cloned;
    }
}
