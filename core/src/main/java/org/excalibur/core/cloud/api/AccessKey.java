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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cloud-access-key")
public class AccessKey implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -8871240544790045076L;

    @XmlAttribute(name = "access-key", required = true)
    private String accessKey_;

    @XmlAttribute(name = "secret-key", required = true)
    private String secretKey_;

    public AccessKey()
    {
        super();
    }

    public AccessKey(String accessKey, String secretKey)
    {
        this.accessKey_ = accessKey;
        this.secretKey_ = secretKey;
    }

    /**
     * @return the accessKey
     */
    public String getAccessKey()
    {
        return accessKey_;
    }

    /**
     * @param accessKey
     *            the accessKey to set
     */
    public AccessKey setAccessKey(String accessKey)
    {
        this.accessKey_ = accessKey;
        return this;
    }

    /**
     * @return the secretKey
     */
    public String getSecretKey()
    {
        return secretKey_;
    }

    /**
     * @param secretKey
     *            the secretKey to set
     */
    public AccessKey setSecretKey(String secretKey)
    {
        this.secretKey_ = secretKey;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof AccessKey))
        {
            return false;
        }

        AccessKey other = (AccessKey) obj;

        return Objects.equal(this.getAccessKey(), other.getAccessKey()) && Objects.equal(this.getSecretKey(), other.getSecretKey());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getAccessKey(), this.getSecretKey());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("accessKey", this.getAccessKey())
        		.add("secretKey", getSecretKey())
        		.omitNullValues()
        		.toString();
    }
    
    @Override
    public AccessKey clone() 
    {
        AccessKey clone;
        
        try
        {
            clone = (AccessKey) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new AccessKey(this.accessKey_, this.secretKey_);
        }
        
        return clone;
    }
}
