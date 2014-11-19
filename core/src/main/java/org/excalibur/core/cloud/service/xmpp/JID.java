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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Object representing a single Jabber's ID.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "account-id", namespace = "http://www.excalibur.org/types/xmpp")
@XmlType(name = "account-id", namespace = "http://www.excalibur.org/types/xmpp")
public final class JID implements Serializable, Cloneable, Comparable<JID>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 5036884140947980972L;

    @XmlElement(name = "name")
    private String id_;

    public JID(String id)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        this.id_ = id;
    }

    JID()
    {
        super();
    }

    /**
     * @return the id_
     */
    public String getId()
    {
        return id_;
    }
    
    public String getName()
    {
        int at = this.getId().indexOf('@');
        
        return at > -1 ? this.getId().substring(0, at) : this.getId();
    }

    @Override
    public String toString()
    {
        return "<" + this.getId() + ">";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof JID))
        {
            return false;
        }

        JID other = (JID) obj;

        return Objects.equal(this.getId(), other.getId());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId());
    }

    @Override
    public JID clone()
    {
        JID clone;

        try
        {
            clone = (JID) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new JID(this.getId());
        }

        return clone;
    }

    @Override
    public int compareTo(JID other)
    {
        if (other == null)
        {
            return 1;
        }

        return this.equals(other) ? 0 : (this.getId() != null ? this.getId().compareTo(other.getId()) : other.getId() != null ? -1 : 0);
    }
}
