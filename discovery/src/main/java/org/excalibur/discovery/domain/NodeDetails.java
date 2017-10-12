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
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.cic.unb.chord.data.ID;
import br.cic.unb.chord.data.Peer;
import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.chord.HashFunction;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import static com.google.common.collect.Lists.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "node-info")
@XmlType(name = "node-info", propOrder = { "addresses_", "uptime_", "provider_", "children_" })
public class NodeDetails implements Comparable<NodeDetails>, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -2692925533124262853L;

    private final transient Object lock_ = new Integer(1);

    /**
     * Uptime in minutes.
     */
    @XmlElement(name = "uptime")
    private long uptime_;

    @XmlElement(name = "provider")
    private ProviderDetails provider_;

    @XmlElement(name = "addresses")
    private final Addresses addresses_ = new Addresses();

    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    private final List<NodeDetails> children_ = newArrayList();

    @XmlTransient
    private NodeDetails parent_;

    public NodeDetails()
    {
        super();
    }

    public NodeDetails(Peer externalAddress)
    {
        this.addresses_.setExternal(externalAddress);
    }
    
    public NodeDetails(ProviderDetails provider)
    {
        this.provider_ = provider;
    }

    public NodeDetails addChildren(Iterable<NodeDetails> children)
    {
        synchronized (lock_)
        {
            for (NodeDetails child : children)
            {
                if (!this.equals(child) && child != null && !this.children_.contains(child))
                {
                    child.setParent(this);
                    this.children_.add(child);
                }
            }
        }
        return this;
    }

    public NodeDetails addChild(NodeDetails child)
    {
        return this.addChildren(Collections.singleton(child));
    }

    public NodeDetails removeChild(NodeDetails child)
    {
        synchronized (lock_)
        {
            this.children_.remove(child);
        }
        return this;
    }

    @Override
    public int compareTo(NodeDetails other)
    {
        return this.getAddresses().getExternal() != null ? 
               this.getAddresses().getExternal().compareTo(other.getAddresses().getExternal()) : 
               other != null ? -1 : 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof NodeDetails))
        {
            return false;
        }

        NodeDetails other = (NodeDetails) obj;

        return Objects.equal(this.getAddresses(), other.getAddresses());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.addresses_);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("host", getAddresses())
        		.add("uptime", getUptime())
        		.omitNullValues()
        		.toString();
    }

    public String getId()
    {
        return this.addresses_.getExternal().getId();
    }

    public ID toID()
    {
        return HashFunction.getHashFunction().createID(URL.valueOf(getAddresses().getExternal().getHost(), 
                getAddresses().getExternal().getPort()).toString().getBytes());
    }

    /**
     * @return the uptime_
     */
    public Long getUptime()
    {
        return uptime_;
    }

    /**
     * @param uptime_
     *            the uptime_ to set
     */
    public NodeDetails setUptime(Long uptime)
    {
        this.uptime_ = (uptime == null ? 0 : uptime);
        return this;
    }

    /**
     * @return the provider
     */
    public ProviderDetails getProvider()
    {
        return provider_;
    }

    /**
     * @param provider_
     *            the provider_ to set
     */
    public NodeDetails setProvider(ProviderDetails provider)
    {
        this.provider_ = provider;
        return this;
    }

    /**
     * @return the parent_
     */
    public NodeDetails getParent()
    {
        return parent_;
    }

    /**
     * @param parent_
     *            the parent_ to set
     */
    public NodeDetails setParent(NodeDetails parent)
    {
        synchronized (lock_)
        {
            if (!this.equals(parent) && !this.children_.contains(parent))
            {
                this.parent_ = parent;
            }
        }

        return this;
    }

    /**
     * @return the address
     */
    public Addresses getAddresses()
    {
        return addresses_;
    }

    public List<NodeDetails> getChildren()
    {
        synchronized (lock_)
        {
            return Collections.unmodifiableList(this.children_);
        }
    }
}
