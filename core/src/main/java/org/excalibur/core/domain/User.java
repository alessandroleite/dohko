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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.KeyPairs;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user")
@XmlType(name = "user", propOrder = { "username_", "keys_" })
public class User implements Serializable, Comparable<User>, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6533258601447162040L;

    @XmlTransient
    private Integer id_;

    @XmlElement(name = "username")
    private String username_;

    @XmlTransient
    private String password_;

    @XmlElement(name = "keys")
    private final UserKeys keys_ = new UserKeys();

    public User()
    {
        super();
    }

    public User(Integer id)
    {
        this.id_ = id;
    }

    public User addKeys(Iterable<UserKey> keys)
    {
        synchronized (this.keys_)
        {
            for (UserKey key : keys)
            {
                this.addKey(key);
            }
        }
        return this;
    }

    public User addKey(UserKey key)
    {
        synchronized (this.keys_)
        {
            if (key != null && !keys_.contains(key))
            {
                keys_.add(key.setUser(this));
            }
        }

        return this;
    }

    public UserKey getKey(final String keyName)
    {
        Iterable<UserKey> keys = Iterables.filter(this.keys_, new Predicate<UserKey>()
        {
            @Override
            public boolean apply(UserKey input)
            {
                return input != null && input.getName().equals(keyName);
            }
        });

        return keys == null || !keys.iterator().hasNext() ? null : keys.iterator().next();
    }

    public KeyPairs getKeyPairs(final String keyname)
    {
        UserKey key = getKey(keyname);
        if (key != null)
        {
            return new KeyPairs().setPrivateKey(new KeyPair().setKeyMaterial(key.getPrivateKeyMaterial()).setKeyName(key.getName())).setPublicKey(
                    new KeyPair().setKeyMaterial(key.getPublicKeyMaterial()).setKeyName(key.getName()));
        }

        return null;

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
    public User setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username_;
    }

    /**
     * @param username
     *            the username to set
     */
    public User setUsername(String username)
    {
        this.username_ = username;
        return this;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password_;
    }

    /**
     * @param password
     *            the password to set
     */
    public User setPassword(String password)
    {
        this.password_ = password;
        return this;
    }

    /**
     * @return the keys
     */
    public UserKeys getKeys()
    {
        return keys_;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof User))
        {
            return false;
        }

        User other = (User) obj;

        return Objects.equal(this.getId(), other.getId()) || Objects.equal(this.getUsername(), other.getUsername());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId(), this.getUsername());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("id", this.getId()).add("username", this.getUsername()).omitNullValues().toString();
    }

    @Override
    public int compareTo(User other)
    {
        return this.getUsername().compareTo(other.getUsername());
    }

    @Override
    public User clone()
    {
        User clone;

        try
        {
            clone = (User) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new User().setPassword(this.getPassword()).setUsername(this.getUsername());
        }

        for (UserKey userKey : this.keys_)
        {
            clone.addKey(userKey.clone());
        }

        return clone;
    }
}
