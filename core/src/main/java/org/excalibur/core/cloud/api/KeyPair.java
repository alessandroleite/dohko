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
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "keypair")
@XmlType(name = "keypair", propOrder = { "keyName_", "keyFingerprint_", "keyMaterial_" })
public class KeyPair implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 6319374712503366823L;

    /**
     * The name of the key pair.
     */
    @XmlElement(name = "name", required = true)
    private String keyName_;

    /**
     * The SHA-1 digest of the DER encoded private key.
     */
    @XmlElement(name = "fingerprint")
    private String keyFingerprint_;

    /**
     * The ciphered PEM encoded RSA private key.
     */
    @XmlElement(name = "material")
    private String keyMaterial_;
    
    public KeyPair()
    {
        super();
    }

    public KeyPair(String keyName)
    {
        this.keyName_ = keyName;
    }

    public KeyPair(String keyName, String keyFingerprint, String keyMaterial)
    {
        this(keyName);
        this.keyFingerprint_ = keyFingerprint;
        this.keyMaterial_ = keyMaterial;
    }

    public KeyPair withKeyName(String name)
    {
        this.keyName_ = name;
        return this;
    }

    public KeyPair withKeyMaterial(String material)
    {
        this.keyMaterial_ = material;
        return this;
    }

    public KeyPair withKeyFingerprint(String figerprint)
    {
        this.keyFingerprint_ = figerprint;
        return this;
    }

    /**
     * @return the keyName
     */
    public String getKeyName()
    {
        return keyName_;
    }

    /**
     * @param keyName
     *            the keyName to set
     */
    public KeyPair setKeyName(String keyName)
    {
        this.keyName_ = keyName;
        return this;
    }

    /**
     * @return the keyFingerprint
     */
    public String getKeyFingerprint()
    {
        return keyFingerprint_;
    }

    /**
     * @param keyFingerprint
     *            the keyFingerprint to set
     */
    public KeyPair setKeyFingerprint(String keyFingerprint)
    {
        this.keyFingerprint_ = keyFingerprint;
        return this;
    }

    /**
     * @return the keyMaterial
     */
    public String getKeyMaterial()
    {
        return keyMaterial_;
    }

    /**
     * @param keyMaterial
     *            the keyMaterial to set
     */
    public KeyPair setKeyMaterial(String keyMaterial)
    {
        this.keyMaterial_ = keyMaterial;
        return this;
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getKeyName(), this.getKeyMaterial());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof KeyPair))
        {
            return false;
        }
        
        KeyPair other = (KeyPair) obj;
        
        return Objects.equal(this.getKeyName(), other.getKeyName()) && 
               Objects.equal(this.getKeyMaterial(), other.getKeyMaterial());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this.getClass())
                .add("name", this.getKeyName())
                .add("fingerprint", this.getKeyFingerprint())
                .add("material", this.getKeyMaterial())
                .omitNullValues()
                .toString();
    }
    
    @Override
    public KeyPair clone() 
    {
        KeyPair clone;
        try
        {
            clone = (KeyPair) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new KeyPair().setKeyFingerprint(this.getKeyFingerprint()).setKeyMaterial(this.getKeyMaterial()).setKeyName(this.getKeyName());
        }
        
        return clone;
    }
}
