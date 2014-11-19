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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "service-info")
@JsonRootName("service-info")
public class ServiceDetails implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7737699284740180111L;

    /**
     * Service's id.
     */
    @XmlAttribute(name = "id")
    @JsonProperty("id")
    private String id_;

    @XmlElement(name = "name")
    @JsonProperty("name")
    private String name_;

    /**
     * Service's description.
     */
    @XmlElement(name = "description")
    @JsonProperty("description")
    private String description_;

    /**
     * Service's version.
     */
    @XmlElement(name = "version")
    @JsonProperty("version")
    private String version_;

    /**
     * The name of the provider that the service belongs to.
     */
    @XmlElement(name = "provider-name")
    @JsonProperty("provider-name")
    private String provider_;

    /**
     * The service's endpoint.
     */
    @XmlElement(name = "endpoint")
    @JsonProperty("endpoint")
    private String endpoint_;

    @XmlElement(name = "port")
    @JsonProperty("port")
    private Integer port_;

    @XmlElement(name = "type")
    private ServiceType type_;

    /**
     * Medias type supported by this service.
     */
    @XmlElement(name = "media-types")
    @JsonProperty("media-types")
    private final List<String> mediaTypes_ = new ArrayList<String>();

    /**
     * @return the id
     */
    public String getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public ServiceDetails setId(String id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @param description
     *            the description to set
     */
    public ServiceDetails setDescription(String description)
    {
        this.description_ = description;
        return this;
    }

    /**
     * @return the mediaTypes
     */
    public List<String> getMediaTypes()
    {
        return mediaTypes_;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version_;
    }

    /**
     * @param version
     *            the version to set
     */
    public ServiceDetails setVersion(String version)
    {
        this.version_ = version;
        return this;
    }

    /**
     * @return the provider
     */
    public String getProvider()
    {
        return provider_;
    }

    /**
     * @param provider
     *            the provider to set
     */
    public ServiceDetails setProvider(String provider)
    {
        this.provider_ = provider;
        return this;
    }

    /**
     * @return the endpoint
     */
    public String getEndpoint()
    {
        return endpoint_;
    }

    /**
     * @param endpoint
     *            the endpoint to set
     */
    public ServiceDetails setEndpoint(String endpoint)
    {
        this.endpoint_ = endpoint;
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
    public ServiceDetails setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the port
     */
    public Integer getPort()
    {
        return port_;
    }

    /**
     * @param port
     *            the port to set
     */
    public ServiceDetails setPort(Integer port)
    {
        this.port_ = port;
        return this;
    }

    /**
     * @return the type
     */
    public ServiceType getType()
    {
        return type_;
    }

    /**
     * @param type
     *            the type to set
     */
    public ServiceDetails setType(ServiceType type)
    {
        this.type_ = type;
        return this;
    }
}
