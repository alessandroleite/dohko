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
package org.excalibur.core.cloud.service.domain;

import java.io.Serializable;

public class Service implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -8015528166636864060L;

    private Integer id_;
    private String name_;
    private String uri_;
    private Protocol protocol_;
    private String mediaType_;

    public Service()
    {
        super();
    }

    public Service(Integer id)
    {
        this.id_ = id;
    }

    public Service withId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    public Service withName(String name)
    {
        this.name_ = name;
        return this;
    }

    public Service withURI(String uri)
    {
        this.uri_ = uri;
        return this;
    }

    public Service withProtocol(Protocol protocol)
    {
        this.protocol_ = protocol;
        return this;
    }

    public Service withMediaType(String mediaType)
    {
        this.mediaType_ = mediaType;
        return this;
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
    public void setId(Integer id)
    {
        this.id_ = id;
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
    public void setName(String name)
    {
        this.name_ = name;
    }

    /**
     * @return the uri
     */
    public String getUri()
    {
        return uri_;
    }

    /**
     * @param uri
     *            the uri to set
     */
    public void setUri(String uri)
    {
        this.uri_ = uri;
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol()
    {
        return protocol_;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(Protocol protocol)
    {
        this.protocol_ = protocol;
    }

    /**
     * @return the mediaType
     */
    public String getMediaType()
    {
        return mediaType_;
    }

    /**
     * @param mediaType
     *            the mediaType to set
     */
    public void setMediaType(String mediaType)
    {
        this.mediaType_ = mediaType;
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
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Service other = (Service) obj;
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
