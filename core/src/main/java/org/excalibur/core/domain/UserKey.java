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
package org.excalibur.core.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static org.excalibur.core.util.SecurityUtils2.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "key")
@XmlType(name = "key")
public class UserKey implements Serializable, Comparable<UserKey>, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -1565104726574174740L;

    @XmlTransient
    private Integer id_;

    @XmlTransient
    private User user_;

    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "private-key-material")
    private String privateKeyMaterial_;

    @XmlElement(name = "public-key-material")
    private String publicKeyMaterial_;
    
    @XmlElement(name = "fingerprint")
    private String fingerPrint_;

    public UserKey()
    {
        super();
    }

    public UserKey(Integer id)
    {
        this.id_ = id;
    }

    protected UserKey(UserKey that)
    {
        this.setId(that.getId()).setName(that.getName())
            .setPrivateKeyMaterial(that.getPrivateKeyMaterial())
            .setPublicKeyMaterial(that.getPublicKeyMaterial())
            .setUser(that.getUser());
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user_;
    }

    /**
     * @param user
     *            the user to set
     */
    public UserKey setUser(User user)
    {
        this.user_ = user;
        return this;
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
    public UserKey setName(String name)
    {
        this.name_ = name;
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
    public UserKey setId(Integer id)
    {
        this.id_ = id;

        return this;
    }

    /**
     * @return the privateKeyMaterial_
     */
    public String getPrivateKeyMaterial()
    {
        return privateKeyMaterial_;
    }

    /**
     * @param privateKeyMaterial_
     *            the privateKeyMaterial_ to set
     */
    public UserKey setPrivateKeyMaterial(String privateKeyMaterial)
    {
        this.privateKeyMaterial_ = privateKeyMaterial;
        return this;
    }

    /**
     * @return the publicKeyMaterial_
     */
    public String getPublicKeyMaterial()
    {
        return publicKeyMaterial_;
    }

    /**
     * @param publicKeyMaterial_
     *            the publicKeyMaterial_ to set
     */
    public UserKey setPublicKeyMaterial(String publicKeyMaterial)
    {
        this.publicKeyMaterial_ = publicKeyMaterial;
        return this;
    }

    /**
     * @return the fingerPrint
     */
    public String getFingerPrint()
    {
        return fingerPrint_;
    }

    /**
     * @param fingerPrint the fingerPrint to set
     */
    public UserKey setFingerPrint(String fingerPrint)
    {
        this.fingerPrint_ = fingerPrint;
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName(), this.getId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof UserKey))
        {
            return false;
        }

        UserKey other = (UserKey) obj;

        return Objects.equal(this.getName(), other.getName()) || Objects.equal(this.getId(), other.getId());
    }

    @Override
    public int compareTo(UserKey that)
    {
        return this.getName().compareTo(that.getName());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("id", getId())
        		.add("name", getName())
        		.omitNullValues()
        		.toString();
    }

    public static UserKey valueOf(KeyPair keyPair)
    {
        return new UserKey().setName(keyPair.getKeyName()).setPrivateKeyMaterial(keyPair.getKeyMaterial());
    }
    
    public static UserKey valueOf(KeyPairs keys)
    {
        UserKey userKey = new UserKey();
        userKey.setPrivateKeyMaterial(keys.getPrivateKey() != null ? keys.getPrivateKey().getKeyMaterial(): null)
               .setPublicKeyMaterial(keys.getPublicKey() != null ? keys.getPublicKey().getKeyMaterial() : null)
               .setName(keys.getName());
        
        return userKey;
    }

    @Override
    public UserKey clone()
    {
        Object clone;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new UserKey(this);
        }

        return (UserKey) clone;
    }

    public boolean isPrivateKeyCipher()
    {
        return this.getPrivateKeyMaterial() != null && !this.getPrivateKeyMaterial().contains(PRIVATE_PKCS8_MARKER);
    }

    public boolean isPublicKeyCipher()
    {
        return this.getPublicKeyMaterial() != null && 
              (!this.getPublicKeyMaterial().contains(PUBLIC_KEY_START) || !this.getPublicKeyMaterial().contains(PUBLIC_KEY_SSH_RSA));
    }
    
    public KeyPairs getKeyPairs()
    {
        KeyPairs keyPairs = new KeyPairs();
        keyPairs.setPrivateKey(new KeyPair().setKeyName(getName()).setKeyMaterial(this.getPrivateKeyMaterial()))
                .setPublicKey(new KeyPair().setKeyName(this.getName()).setKeyMaterial(this.getPublicKeyMaterial()));
        
        return keyPairs;
    }
}
