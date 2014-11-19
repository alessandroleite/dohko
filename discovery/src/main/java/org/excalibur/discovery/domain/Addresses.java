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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

import br.cic.unb.chord.data.Peer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "addresses")
@XmlType(name = "addresses", propOrder = { "internal_", "external_" })
public class Addresses implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -2206136033202739510L;

    @XmlElement(name = "internal")
    private Peer internal_;

    @XmlElement(name = "external")
    private Peer external_;

    /**
     * @return the internal
     */
    public Peer getInternal()
    {
        return internal_;
    }

    /**
     * @param internal
     *            the internal to set
     */
    public Addresses setInternal(Peer internal)
    {
        this.internal_ = internal;
        return this;
    }

    /**
     * @return the external
     */
    public Peer getExternal()
    {
        return external_;
    }

    /**
     * @param external
     *            the external to set
     */
    public Addresses setExternal(Peer external)
    {
        this.external_ = external;
        return this;
    }

    @Override
    public Addresses clone()
    {
        Addresses clone;

        try
        {
            clone = (Addresses) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Addresses().setExternal(this.external_ != null ? this.external_.clone() : null)
                                   .setInternal(this.internal_ != null ? this.internal_.clone() : null);
        }
        
        return clone;
    }
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("internal", this.getInternal()).add("external", this.getExternal()).omitNullValues().toString();
    }

    
    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getExternal(), this.getInternal());
    }

    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
    
        if (!(obj instanceof Addresses))
        {
            return false;
        }
        
        Addresses other = (Addresses) obj;
        
        return Objects.equal(this.getExternal(), other.getExternal()) && Objects.equal(this.getInternal(), other.getInternal());
    }
    
}
