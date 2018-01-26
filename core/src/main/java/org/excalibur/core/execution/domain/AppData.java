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
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.util.YesNoEnum;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "file")
@XmlType(name = "file", propOrder = { "name_", "source_", "dest_", "checksum_", "generated_"})
public class AppData implements Serializable, Comparable<AppData>, Cloneable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = 2108053982461520523L;

    @XmlElement(name = "name", required = true, nillable = false)
    private String name_;
    
    @XmlElement(name = "source")
    private String source_;
    
    @XmlElement(name = "dest", required = true, nillable = false)
    private String dest_;
    
    @XmlElement(name = "checksum")
    private String checksum_;

    @XmlElement(name = "generated", required = true, nillable = false)
    private YesNoEnum generated_ = YesNoEnum.NO;
    
    public AppData()
    {
    	super();
    }
    
    public static AppData newAppData()
    {
    	return new AppData();
    }
    
    public static AppData newAppData(String name, String source, String dest)
    {
    	return new AppData()
    			.setName(name)
    			.setSource(source)
    			.setPath(dest);
    }
    
    public Optional<URI> getSourceURI()
    {
    	return Strings.isNullOrEmpty(source_) ? Optional.empty() : Optional.of(URI.create(source_));
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }
    
    /**
     * Returns the name of the file
     * @return name of the file
     * @see #getName()
     */
    public String name()
    {
    	return getName();
    }

    /**
     * @param name the name to set
     */
    public AppData setName(String name)
    {
        this.name_ = name;
        return this;
    }
    

    /**
	 * @return the dest
	 */
	public String getDest() 
	{
		return dest_;
	}
	
	public String dest()
	{
		return getDest();
	}

	/**
	 * @param dest the dest to set
	 */
	public AppData setDest(String dest) 
	{
		this.dest_ = dest;
		return this;
	}

	/**
     * @return the path
     * @deprecated replaced by {@link #getDest()}
     */
    @Deprecated
    public String getPath()
    {
        return getDest();
    }

    /**
     * @param path the path to set
     * @deprecated replaced by {@link #setDest(String)}
     */
    @Deprecated
    public AppData setPath(String path)
    {
        setDest(path);
        return this;
    }
    

    /**
	 * @return the source
	 */
	public String getSource() 
	{
		return source_;
	}
	
	public String source()
	{
		return getSource();
	}

	/**
	 * @param source the source to set
	 */
	public AppData setSource(String source) 
	{
		this.source_ = source;
		return this;
	}
	

	/**
	 * @return the checksum
	 */
	public String getChecksum() 
	{
		return checksum_;
	}
	
	public String checksum() 
	{
		return getChecksum();
	}

	/**
	 * @param checksum the checksum to set
	 */
	public AppData setChecksum(String checksum) 
	{
		this.checksum_ = checksum;
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
     * @param generated the generated to set
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
        return Objects.hash(this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        
        AppData other = (AppData) obj;
        
        return Objects.equals(this.getName(), other.getName());
    }

    @Override
    public AppData clone() 
    {
    	AppData clone;
        
        try
        {
            clone = (AppData) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new AppData()
            		.setChecksum(getChecksum())
            		.setGenerated(getGenerated())
            		.setName(getName())
            		.setDest(getDest())
            		.setSource(getSource());
        }
        
        return clone;
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("source", getSource())
                .add("dest", getDest())
                .add("checksum", getChecksum())
                .add("isGenerated", getGenerated())
                .omitNullValues()
                .toString();
    }
}
