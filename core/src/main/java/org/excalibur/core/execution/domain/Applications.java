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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.collect.Lists.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "applications")
public class Applications implements Iterable<Application>, Serializable, Cloneable
{
    /**
     *  Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6374024874372518109L;
    
    @XmlElement(name = "application")
    private final List<Application> applications_ = newCopyOnWriteArrayList();

    public Applications add(Application application)
    {
        if (application != null)
        {
            this.applications_.add(application);
        }
        return this;
    }

    public Applications addAll(Application... applications)
    {
        if (applications != null)
        {
            for (Application application : applications)
            {
                this.add(application);
            }
        }
        return this;
    }

    public Applications remove(Application application)
    {
        this.applications_.remove(application);
        return this;
    }

    public List<Application> getApplications()
    {
        return Collections.unmodifiableList(this.applications_);
    }

    @Override
    public Iterator<Application> iterator()
    {
        return this.getApplications().iterator();
    }

    public int size()
    {
        return this.applications_.size();
    }

    public boolean isEmpty()
    {
        return this.applications_.isEmpty();
    }
    
    public void clear()
    {
        this.applications_.clear();
    }
    
    @Override
    public Applications clone() 
    {
        Applications cloned;
        
        try
        {
            cloned = (Applications) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new Applications();
        }
        
        cloned.clear();
        
        for (Application application: this.applications_)
        {
            cloned.add(application.clone());
        }
        
        return cloned;
    }
    
    @Override
    public String toString()
    {
        return this.applications_.toString();
    }
}
