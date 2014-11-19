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
package org.excalibur.core.exec;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "host-and-port")
public class HostAndPort implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = -5237716621768456458L;

    @XmlAttribute(name = "port", required = true)
    private Integer port_;
    
    @XmlAttribute(name = "host", required = true)
    private String host_;
    
    /**
     * The provider's name. Might not be <code>null</code>.
     */
    @XmlAttribute(name = "provider", required = true)
    private String provider_;
    
    public HostAndPort()
    {
        super();
    }

    public HostAndPort(String host, int port, String provider)
    {
        this.host_ = host;
        this.port_ = port;
        this.provider_ = provider;
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
    public HostAndPort setPort(Integer port)
    {
        this.port_ = port;
        return this;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return host_;
    }

    /**
     * @param host
     *            the host to set
     */
    public HostAndPort setHost(String host)
    {
        this.host_ = host;
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
     * @param provider the provider to set
     */
    public HostAndPort setProvider(String provider)
    {
        this.provider_ = provider;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getHost(), this.getPort());
    }

    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        

        if (!(obj instanceof HostAndPort))
        {
            return false;
        }
        
        HostAndPort other = this.getClass().cast(obj);
        return equal(this.getHost(), other.getHost()) && equal(this.getPort(), other.getPort());
    }
    
    @Override
    public String toString()
    {
        return toStringHelper(this).add("host", this.getHost()).add("port", this.getPort()).omitNullValues().toString();
    }
    
    @Override
    public HostAndPort clone()
    {
        Object clone;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new HostAndPort(this.host_, this.port_, this.provider_);
        }
        
        return (HostAndPort) clone;
    }
}
