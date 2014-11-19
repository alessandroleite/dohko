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
package org.excalibur.service.compute.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder(value = { "id_", "name_", "privateIp_", "publicIp_", "parent", "location_", "provider_" })
@JsonRootName("node")
@XmlRootElement(name = "node")
@XmlType(propOrder = { "id_", "name_", "privateIp_", "publicIp_", "parent", "location_", "provider_" })
public class NodeMetadata implements Serializable
{
    /**
     * Serial code version <code></code> for serialization.
     */
    private static final long serialVersionUID = -4635838177604585362L;

    @XmlElement(name = "id")
    @JsonProperty("id")
    private Integer id_;

    @JsonProperty("name")
    @XmlElement(name = "name")
    private String name_;

    @JsonProperty("private-ip")
    @XmlElement(name = "private-ip")
    private String privateIp_;

    @JsonProperty("public-ip")
    @XmlElement(name = "public-ip")
    private String publicIp_;

    @XmlElement(name = "parent")
    @JsonProperty("parent")
    private NodeMetadata parent_;

    @JsonProperty("location")
    @XmlElement(name = "location")
    private String location_;

    @JsonProperty("provider-name")
    @XmlElement(name = "provider-name")
    private String provider_;

    @JsonProperty("group-name")
    @XmlElement(name = "group-name")
    private String groupName_;

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
    public NodeMetadata setId(Integer id)
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
    public NodeMetadata setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the privateIp_
     */
    public String getPrivateIp()
    {
        return privateIp_;
    }

    /**
     * @param privateIp_
     *            the privateIp_ to set
     */
    public NodeMetadata setPrivateIp(String privateIp)
    {
        this.privateIp_ = privateIp;
        return this;
    }

    /**
     * @return the publicIp_
     */
    public String getPublicIp()
    {
        return publicIp_;
    }

    /**
     * @param publicIp_
     *            the publicIp_ to set
     */
    public NodeMetadata setPublicIp(String publicIp)
    {
        this.publicIp_ = publicIp;
        return this;
    }

    /**
     * @return the parent_
     */
    public NodeMetadata getParent()
    {
        return parent_;
    }

    /**
     * @param parent_
     *            the parent_ to set
     */
    public NodeMetadata setParent(NodeMetadata parent)
    {
        this.parent_ = parent;
        return this;
    }

    /**
     * @return the location_
     */
    public String getLocation()
    {
        return location_;
    }

    /**
     * @param location_
     *            the location_ to set
     */
    public NodeMetadata setLocation(String location)
    {
        this.location_ = location;
        return this;
    }

    /**
     * @return the provider_
     */
    public String getProvider()
    {
        return provider_;
    }

    /**
     * @param provider_
     *            the provider_ to set
     */
    public NodeMetadata setProvider(String provider)
    {
        this.provider_ = provider;
        return this;
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
    public void setGroupName(String groupName)
    {
        this.groupName_ = groupName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id_ == null) ? 0 : id_.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof NodeMetadata))
        {
            return false;
        }

        NodeMetadata other = (NodeMetadata) obj;
        if (id_ == null)
        {
            if (other.id_ != null)
            {
                return false;
            }
        }
        else if (!id_.equals(other.id_))
        {
            return false;
        }
        return true;
    }
}
