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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.excalibur.core.domain.UserKey;

import static com.google.common.collect.Lists.*;
import static com.google.common.base.Preconditions.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "keys")
public class UserKeys implements Iterable<UserKey>, Cloneable, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization. 
     */
    private static final long serialVersionUID = 6925483955433110421L;
    
    @XmlElement(name = "key")
    private final List<UserKey> keys_ = newCopyOnWriteArrayList();
    
    public UserKeys add(UserKey key)
    {
        if (key != null)
        {
            this.keys_.add(key);
        }
        
        return this;
    }
    
    public UserKeys addAll(Iterable<UserKey> keys)
    {
        if (keys != null)
        {
            for (UserKey key: keys)
            {
                this.add(key);
            }
        }
        
        return this;
    }
    
    public UserKey remove(UserKey key)
    {
        return this.remove(this.keys_.indexOf(key));
    }

    public UserKey remove(int index)
    {
        UserKey removed = null;
        
        if (index > -1 && index < this.keys_.size())
        {
            removed  = this.keys_.remove(index);
        }
        
        return removed;
    }
    
    public boolean contains(UserKey key)
    {
        return this.keys_.contains(key);
    }

    @Override
    public Iterator<UserKey> iterator()
    {
        return getKeys().iterator();
    }
    
    public List<UserKey> getKeys()
    {
        return Collections.unmodifiableList(keys_);
    }
    
    public boolean isEmpty()
    {
        return this.keys_.isEmpty();
    }
    
    public int size()
    {
        return this.keys_.size();
    }
    
    public UserKey get(int index)
    {
        synchronized (keys_)
        {
            checkPositionIndex(index, this.keys_.size());
            return this.keys_.get(index);
        }
    }
    
    @Override
    public UserKeys clone()
    {
        UserKeys clone;
        
        try
        {
            clone = (UserKeys) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new UserKeys();
        }
        
        for (UserKey key: this.keys_)
        {
            clone.add(key.clone());
        }
        
        return clone;
    }
}
