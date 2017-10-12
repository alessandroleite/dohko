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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attribute")
public class Attribute implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8604732233614306314L;

    /**
     * The attribute name. Might not be <code>null</code>.
     */
    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlAttribute(name = "value")
    private String value_;

    protected Attribute()
    {
        super();
    }
    
    public Attribute(String name)
    {
        setName(name);
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
    protected final Attribute setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value_;
    }

    /**
     * @param value
     *            the value to set
     */
    public Attribute setValue(String value)
    {
        this.value_ = value;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName(), this.getValue());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Attribute))
        {
            return false;
        }

        Attribute other = (Attribute) obj;

        if (!Objects.equal(this.getName(), other.getName()))
        {
            return false;
        }

        return Objects.equal(this.getValue(), other.getValue());
    }

    @Override
    public Attribute clone()
    {
        Object clone;

        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            clone = new Attribute().setName(this.getName()).setValue(this.getValue());
        }
        return (Attribute) clone;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		          .add("name", this.getName())
        		          .add("value", this.getValue())
        		          .omitNullValues()
        		          .toString();
    }
}
