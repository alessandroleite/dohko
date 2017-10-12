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
package org.excalibur.core.execution.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.util.YesNoEnum;

import com.google.common.base.MoreObjects;

import static com.google.common.base.Objects.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "file")
@XmlType(name = "file", propOrder = { "name_", "path_", "generated_" })
public class AppData implements Serializable, Comparable<AppData>, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6486674085608485490L;

    @XmlElement(name = "name", required = true, nillable = false)
    private String name_;

    @XmlElement(name = "path", required = true, nillable = false)
    private String path_;

    @XmlElement(name = "generated", required = true, nillable = false)
    private YesNoEnum generated_ = YesNoEnum.NO;

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
    public AppData setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the path
     */
    public String getPath()
    {
        return path_;
    }

    /**
     * @param path
     *            the path to set
     */
    public AppData setPath(String path)
    {
        this.path_ = path;
        return this;
    }

    /**
     * @return the generated
     */
    public YesNoEnum getGenerated()
    {
        return generated_;
    }

    /**
     * @param generated
     *            the generated to set
     */
    public AppData setGenerated(YesNoEnum generated)
    {
        this.generated_ = generated;
        return this;
    }

    @Override
    public int compareTo(AppData other)
    {
        return this.getName().compareTo(other.getName());
    }
    
    
    
    @Override
    public int hashCode()
    {
        return Objects.hash(this.getName(), this.getPath(), this.getGenerated());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof AppData))
        {
            return false;
        }
        
        AppData other = (AppData) obj;
        
        return equal(this.getName(), other.getName()) && equal(this.getPath(), other.getPath()) && equal(this.getGenerated(), other.getGenerated());
    }

    @Override
    public AppData clone() 
    {
        Object cloned;
        
        try
        {
            cloned = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new AppData().setGenerated(this.getGenerated()).setName(this.getName()).setPath(this.getPath());
        }
        
        return (AppData) cloned;
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", this.getName())
                .add("path", this.getPath())
                .add("isGenerated", this.getGenerated())
                .omitNullValues()
                .toString();
    }
}
