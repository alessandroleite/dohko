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
package br.cic.unb.chord.data;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "peer")
@XmlType(name = "peer", propOrder = { "host_", "port_" })
public class Peer implements Serializable, Cloneable, Comparable<Peer>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7723794498392876873L;

    /**
     * The opaque ID of the node.
     */
    @XmlAttribute(name = "id", required = true)
    private String id_;

    /**
     * The physical address of the node in the underlying infrastructure.
     */
    @XmlElement(name = "host", required = true)
    private String host_;

    /**
     * The port of the node.
     */
    @XmlElement(name = "port", required = true)
    private Integer port_;

    public Peer()
    {
        super();
    }

    public Peer(String id, String address, Integer port)
    {
        this.id_ = id;
        this.host_ = address;
        this.port_ = port;
    }

    public Peer(Peer that)
    {
        this(that.getId(), that.getHost(), that.getPort());
    }

    public Peer(ID id, URL url)
    {
        this(id.toString(), url.getHost(), url.getPort());
    }

    public static Peer valueOf(ID id, URL url)
    {
        return new Peer(id, url);
    }

    public URL toURL()
    {
        return URL.valueOf(this.getHost(), this.getPort());
    }

    /**
     * @return the nodeID
     */
    public String getId()
    {
        return id_;
    }

    /**
     * @param nodeID
     *            the nodeID to set
     */
    public Peer setId(String id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the address
     */
    public String getHost()
    {
        return host_;
    }

    /**
     * @param address
     *            the address to set
     */
    public Peer setHost(String address)
    {
        this.host_ = address;
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
     * @param port_
     *            the port_ to set
     */
    public Peer setPort(Integer port)
    {
        this.port_ = port;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Peer))
        {
            return false;
        }

        Peer other = (Peer) obj;

        return Objects.equal(this.getId(), other.getId()) && Objects.equal(this.getHost(), other.getHost())
                && Objects.equal(getPort(), other.getPort());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId(), this.getHost());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("ID", this.getId())
        		.add("host", getHost())
        		.add("port", getPort())
        		.omitNullValues()
        		.toString();
    }

    @Override
    public int compareTo(Peer that)
    {
        if (that == null)
        {
            return 1;
        }
        else if (this.getId() == null && that.getId() == null)
        {
            return 0;
        }
        else if (this.getId() != null && that.getId() == null)
        {
            return 1;
        }
        else if (this.getId() == null && that.getId() != null)
        {
            return -1;
        }

        return this.getId().compareTo(that.getId());
    }

    @Override
    public Peer clone()
    {
        Peer clone;

        try
        {
            clone = (Peer) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Peer(this);
        }
        return clone;
    }
}
