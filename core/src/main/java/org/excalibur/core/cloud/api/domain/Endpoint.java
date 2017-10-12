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

import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "endpoint")
@XmlType(name = "endpoint")
public class Endpoint implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 4758518905035393752L;

    /**
     * The endpoint address.
     */
    @XmlElement(name = "uri")
    private String uri_;

    /**
     * The port number.
     */
    @XmlElement(name = "port")
    private Integer port_;

    /**
     * Default constructor. The port and address must be configured calling the setters' method or the fluent setters.
     */
    public Endpoint()
    {
        super();
    }

    /**
     * Creates a new {@link Endpoint} with the address and port.
     * 
     * @param uri
     *            The endpoint's address. Might not be <code>null</code>.
     * @param port
     *            The endpoint's port. Might not be <code>null</code>.
     */
    public Endpoint(String uri, Integer port)
    {
        setUri(uri);
        setPort(port);
    }
    

    public static Endpoint valueOf(String uri, Integer port)
    {
        return new Endpoint(uri, port);
    }

    /**
     * @return the uri
     */
    public String getUri()
    {
        return uri_;
    }
    
    /**
     * Returns the URI of the endpoint.
     * @return the uri.
     * @deprecated replaced by {@link #getUri()}.
     */
    @Deprecated
    public String getAddress()
    {
        return this.getUri();
    }

    public Endpoint setUri(String address)
    {
        checkArgument(!isNullOrEmpty(address));
        this.uri_ = address;
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
     * Assign a new port for this {@link Endpoint}.
     * 
     * @param port
     *            The new port to be assigned. Might not be <code>null</code>.
     * @return A reference to this object with the port value updated.
     */
    public Endpoint setPort(Integer port)
    {
        checkArgument(port != null && port > 0 && port < 65536);
        this.port_ = port;
        return this;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + uri_.hashCode();
        result = prime * result + port_.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        Endpoint other = (Endpoint) obj;
        return this.getPort().equals(other.getPort()) && this.getAddress().equals(other.getPort());
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("uri", getUri())
        		.add("port", getPort())
        		.omitNullValues()
        		.toString();
    }
}
