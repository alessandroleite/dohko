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
package org.excalibur.core.cloud.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "keypairs")
public class KeyPairs implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 5393000319931044190L;

    private final KeyPair[] keys_ = new KeyPair[2];

    public KeyPairs()
    {
        super();
    }

    protected KeyPairs(KeyPairs that)
    {
        for (int i = 0; i < keys_.length; i++)
        {
            this.keys_[i] = that.keys_[i];
        }
    }

    @XmlElement(name = "public-key")
    public KeyPair getPublicKey()
    {
        return this.keys_[0];
    }

    public KeyPairs setPublicKey(KeyPair key)
    {
        this.keys_[0] = key;
        return this;
    }

    @XmlElement(name = "private-key")
    public KeyPair getPrivateKey()
    {
        return this.keys_[1];
    }

    public KeyPairs setPrivateKey(KeyPair key)
    {
        this.keys_[1] = key;
        return this;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("public-key", getPublicKey())
                .add("private-key", getPrivateKey())
                .omitNullValues()
                .toString();
    }

    @Override
    public KeyPairs clone()
    {
        KeyPairs clone;

        try
        {
            clone = (KeyPairs) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new KeyPairs(this);
        }
        
        for (int i = 0; i < keys_.length; i++)
        {
            clone.keys_[i] = keys_[i].clone();
        }

        return clone;
    }

    /**
     * Returns the name of the keys. 
     * @return The key's name.
     * @throws IllegalStateException If the name of the keys are different.
     */
    public String getName()
    {
        String pvtKeyName = this.getPrivateKey() != null ? this.getPrivateKey().getKeyName() : null;
        String pbKeyName = this.getPublicKey() != null ? this.getPublicKey().getKeyName() : null;
        Preconditions.checkState(pvtKeyName != null && pbKeyName != null && pvtKeyName.equals(pbKeyName));
        
        return pvtKeyName != null ? pvtKeyName : pbKeyName;
    }
}
