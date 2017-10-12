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
package org.excalibur.discovery.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@JsonRootName("resource-info")
@JsonPropertyOrder(value = { "name", "type", "payload" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "resource")
@XmlType(name = "resource-info", propOrder = { "name_", "type_", "payload_" })
public class ResourceDetails implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 2357385843548658359L;

    @XmlAttribute(name = "name", required = true)
    @JsonProperty("name")
    private String name_;

    @XmlElement(name = "payload-type", required = true)
    @JsonProperty("payload-type")
    private Class<?> type_;

    @XmlElement(name = "payload", required = true)
    @JsonProperty("payload")
    private String payload_;

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
    public ResourceDetails setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the type_
     */
    public Class<?> getType()
    {
        return type_;
    }

    /**
     * @param type_
     *            the type_ to set
     */
    public ResourceDetails setType(Class<?> type)
    {
        this.type_ = type;
        return this;
    }

    /**
     * @return the payload_
     */
    public String getPayload()
    {
        return payload_;
    }

    /**
     * @param payload_
     *            the payload_ to set
     */
    public ResourceDetails setPayload(String payload)
    {
        this.payload_ = payload;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ResourceDetails))
        {
            return false;
        }

        ResourceDetails other = (ResourceDetails) obj;

        return Objects.equal(this.getName(), other.getName());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("name", getName())
        		.add("type", getType())
        		.add("payload", getPayload())
        		.toString();
    }
}
