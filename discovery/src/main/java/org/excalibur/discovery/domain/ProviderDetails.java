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

import org.excalibur.core.cloud.api.domain.Endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.MoreObjects;

@JsonRootName("provider-info")
@JsonPropertyOrder(value = { "name", "endpoint" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "provider-info")
@XmlType(name = "provider-info", propOrder = { "name_", "endpoint_" })
public class ProviderDetails implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3352118689762951056L;

    /**
     * Provider's name.
     */
    @XmlAttribute(name = "name", required = true)
    @JsonProperty("name")
    private String name_;

    /**
     * Provider's endpoint.
     */
    @XmlElement(name = "endpoint", required = true)
    @JsonProperty("endpoint")
    private Endpoint endpoint_;

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
    public ProviderDetails setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the endpoint_
     */
    public Endpoint getEndpoint()
    {
        return endpoint_;
    }

    /**
     * @param endpoint_
     *            the endpoint_ to set
     */
    public ProviderDetails setEndpoint(Endpoint endpoint)
    {
        this.endpoint_ = endpoint;
        return this;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("name", getName())
        		.add("endpoint", getEndpoint())
        		.omitNullValues()
        		.toString();
    }
}
