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
package org.excalibur.core.cloud.service.xmpp;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

//@XmlJavaTypeAdapter(AccountAdapter.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "account", namespace = "http://www.excalibur.org/types/xmpp")
@XmlType(name = "account", namespace = "http://www.excalibur.org/types/xmpp")
public final class Account implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6314781728472022282L;

    @XmlElement(name = "account-id", required = true)
    private JID id_;

    @XmlElement(name = "account-domain", required = true)
    private String domain_;

    @XmlElement(name = "account-password", required = true)
    private String password_;

    @XmlElement(name = "account-resource", required = true)
    private String resource_;

    @XmlElement(name = "owner-id")
    private Integer ownerId_;

    @XmlElement(name = "created-in")
    private Date createIn_;

    @XmlElement(name = "last-status-time")
    private Date dateLastStatus_;

//    @XmlElement(name = "attributes")
    @XmlTransient
    private final Map<String, String> attributes_ = new HashMap<String, String>();

    Account(String id, String domain, String password, String resource, Integer ownerId, Map<String, String> attributes, Date createIn,
            Date dateLastStatus)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(domain));

        this.id_ = new JID(String.format("%s@%s", id, domain));
        this.password_ = password;
        this.domain_ = domain;
        this.resource_ = resource;
        this.ownerId_ = ownerId;

        if (attributes != null)
        {
            for (String attr : attributes.keySet())
            {
                if (attr != null)
                {
                    this.attributes_.put(attr, attributes.get(attr));
                }
            }
        }

        this.createIn_ = createIn;
        this.dateLastStatus_ = dateLastStatus;
    }
    
    Account()
    {
        super();
    }

    /**
     * Returns the value of a given attribute. <code>null</code> means either the attribute does not exists or its value is <code>null</code>
     * 
     * @param attribute
     *            The name of the attribute to return its value.
     * @return The value of the attribute if it exists.
     */
    public String getAttribute(String attribute)
    {
        return this.attributes_.get(attribute);
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(attributes_);
    }

    /**
     * @return the id
     */
    public JID getId()
    {
        return id_;
    }

    /**
     * @return the domain
     */
    public String getDomain()
    {
        return domain_;
    }

    /**
     * Returns the user's name.
     * 
     * @return The user's name.
     */
    public String getName()
    {
        return this.getId().getId();
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password_;
    }

    /**
     * @return the resource_
     */
    public String getResource()
    {
        return resource_;
    }

    /**
     * @return the ownerId
     */
    public Integer getOwnerId()
    {
        return ownerId_;
    }

    /**
     * @return the createIn
     */
    public Date getCreateIn()
    {
        return createIn_;
    }

    /**
     * @return the dateLastStatus
     */
    public Date getDateLastStatus()
    {
        return dateLastStatus_;
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof Account))
        {
            return false;
        }
        
        Account other = (Account) obj;
        return Objects.equal(this.getId(), other.getId());
    }
}
