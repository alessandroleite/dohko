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
package org.excalibur.core.cloud.api.domain;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tags")
@XmlType(name = "tags")
public class Tags implements Serializable, Cloneable, Iterable<Tag>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -4221209315107107889L;

    @XmlElement(name="tag")
    private final List<Tag> tags_ = Lists.newArrayList();

    public Tags()
    {
        super();
    }

    public Tags(Tag... tags)
    {
        add(tags);
    }

    public Tags(Tags that)
    {
        add(that.tags_.toArray(new Tag[that.tags_.size()]));
    }

    public static Tags newTags(Tag... tags)
    {
        return new Tags(tags);
    }

    public static Tags newTags(List<Tag> tags)
    {
        return tags != null ? newTags(tags.toArray(new Tag[tags.size()])) : newTags();
    }

    public void add(Tag... tags)
    {
        if (tags != null)
        {
            for (Tag tag : tags)
            {
                if (tags != null && tag.isValid())
                {
                    this.tags_.add(tag);
                }
            }
        }
    }

    public Tags copyFrom(Iterable<Tag> tags)
    {
        if (tags != null)
        {
            for (Tag tag : tags)
            {
                this.add(tag);
            }
        }

        return this;
    }
    

    public void remove(String... values)
    {
        if (values != null)
        {
            for (String tag : values)
            {
                this.tags_.remove(tag);
            }
        }
    }

    public List<Tag> getTags()
    {
        return Lists.newArrayList(tags_);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Tag tag : this.tags_)
        {
            sb.append(tag).append(File.pathSeparator);
        }

        return sb.substring(0, sb.length() - 1);
    }

    @Override
    protected Tags clone()
    {
        Tags clone = null;

        try
        {
            clone = (Tags) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Tags();
        }
        
        for (Tag tag: this.tags_)
        {
            clone.add(tag.clone());
        }

        return clone;
    }

    @Override
    public Iterator<Tag> iterator()
    {
        return this.getTags().iterator();
    }
    
    public int size()
    {
        return this.tags_.size();
    }
    
    public boolean isEmpty()
    {
        return this.tags_.isEmpty();
    }

    public boolean contains(Tag tag)
    {
        return this.tags_.contains(tag);
    }
    
    public boolean containsTagWithKey(String name)
    {
        for (Tag tag: this)
        {
            if (tag.getName().equals(name))
            {
                return true;
            }
        }
        
        return false;
    }
}
