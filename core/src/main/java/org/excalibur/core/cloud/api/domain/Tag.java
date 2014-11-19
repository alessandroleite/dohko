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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tag")
@XmlType(name = "tag", propOrder = { "name_", "value_" })
public class Tag implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 410113864774072859L;

    @XmlElement(name = "name", required = true)
    private String name_;

    @XmlElement(name = "value")
    private String value_;

    public Tag()
    {
        super();
    }

    public Tag(String name, String value)
    {
        setName(name);
        setValue(value);
    }
    
    public static Tag valueOf(String name, String value)
    {
        return new Tag(name, value);
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
    public final Tag setName(String name)
    {
//        this.name_ = name != null && name.matches(Strings2.RFC1035_REGEX_PATTERN) ? name : this.name_;
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
    public final Tag setValue(String value)
    {
//        this.value_ = value != null && value.matches(Strings2.RFC1035_REGEX_PATTERN) ? value : this.value_;
        this.value_ = value;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof Tag))
        {
            return false;
        }

        Tag other = (Tag) obj;
        return Objects.equal(this.getName(), other.getName()) && Objects.equal(this.getValue(), other.getValue());
    }

    @Override
    public String toString()
    {
        return (getName() + "=" + getValue());
    }
    
    @Override
    public Tag clone()
    {
        try
        {
            return (Tag) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            return new Tag(this.getName(), this.getValue());
        }
    }
    
    public final boolean isValid()
    {
        return this.getName()  != null  && //this.getName().matches(Strings2.RFC1035_REGEX_PATTERN) &&
               this.getValue() != null; //  && this.getValue().matches(Strings2.RFC1035_REGEX_PATTERN);  
    }

}
