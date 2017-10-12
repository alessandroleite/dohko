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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "geographic-region")
public class GeographicRegion implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 3442474442341767990L;
    
    /**
     * Utility enumeration with the available geographic regions. It's important to notice that this type was created just to increase the readability of the code. 
     */
    public static enum GeographicRegions
    {
        /**
         * 
         */
        AFRICA(1, "Africa"),
        
        /**
         * 
         */
        ASIA (2, "Asia"),
        
        /**
         * 
         */
        CENTRAL_AMERICA(3, "Central America"),
        
        /**
         * 
         */
        EUROPEAN(4, "European"), 
        
        /**
         * 
         */
        NORTH_AMERICA(5, "North America"),
        
        /**
         * 
         */
        SOUTH_AMERICA(6, "South America");
        
        private final int id_;
        private final String name_;
        
        private GeographicRegions(int id, String name)
        {
            this.id_ = id;
            this.name_ = name;
        }

        /**
         * @return the id
         */
        public int getId()
        {
            return id_;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name_;
        }
        
        public GeographicRegion toType()
        {
            return new GeographicRegion(this.getId(), this.getName());
        }
    }

    @XmlAttribute(name = "id", required = true)
    private Integer id_;

    @XmlAttribute(name = "name")
    private String name_;

    public GeographicRegion()
    {
        super();
    }

    public GeographicRegion(Integer id)
    {
        this.id_ = id;
    }

    public GeographicRegion(Integer id, String name)
    {
        this(id);
        this.name_ = name;
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
    public GeographicRegion setId(Integer id)
    {
        this.id_ = id;
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
    public GeographicRegion setName(String name)
    {
        this.name_ = name;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof GeographicRegion))
        {
            return false;
        }

        GeographicRegion other = (GeographicRegion) obj;

        return equal(this.getId(), other.getId()) || equal(this.getName(), other.getName());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(getId(), getName());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("id", getId())
        		.add("name", getName())
        		.omitNullValues()
        		.toString();
    }

    @Override
    protected GeographicRegion clone()
    {
        Object clone;
        
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new GeographicRegion(this.getId(), this.getName());
        }
        
        return (GeographicRegion) clone;
    }
}
