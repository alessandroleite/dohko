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
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "instance-types")
@XmlType(name = "instance-types")
public class InstanceTypes implements Serializable, Iterable<InstanceType>
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 8354916041635602562L;

    private final transient Object lock_ = new Integer(1);

    @XmlElement(name = "instance-type")
    private final List<InstanceType> instanceTypes_ = newCopyOnWriteArrayList();

    public InstanceTypes()
    {
        super();
    }

    public InstanceTypes(Iterable<InstanceType> types)
    {
        this.addAll(types);
    }

    public InstanceTypes addAll(Iterable<InstanceType> types)
    {
        synchronized (lock_)
        {
            if (null != types)
            {
                for (InstanceType type : types)
                {
                    if (null != type)
                    {
                        this.instanceTypes_.add(type);
                    }
                }
            }
        }
        return this;
    }
    
    public InstanceType get(int i)
    {
        synchronized (lock_)
        {
            checkElementIndex(i, this.instanceTypes_.size());
            return this.instanceTypes_.get(i);
        }
    }

    /**
     * 
     * @param instanceTypes
     * @return
     */
    public static InstanceTypes valueOf(List<InstanceType> instanceTypes)
    {
        return new InstanceTypes(instanceTypes);
    }
    
    public static InstanceTypes newInstanceTypes()
    {
        return new InstanceTypes();
    }


    public InstanceTypes add(InstanceType type)
    {
        synchronized (lock_)
        {
            if (null != type)
            {
                this.instanceTypes_.add(type);
            }
        }

        return this;
    }

    public InstanceType remove(InstanceType instanceType)
    {
        InstanceType removed = null;

        synchronized (lock_)
        {
            if (null != instanceType && !this.instanceTypes_.isEmpty())
            {
                int index = this.instanceTypes_.indexOf(instanceType);

                if (-1 != index)
                {
                    removed = this.instanceTypes_.remove(index);
                }
            }
        }

        return removed;
    }

    public List<InstanceType> getTypes()
    {
        synchronized (lock_)
        {
            return ImmutableList.copyOf(instanceTypes_);
        }
    }

    @Override
    public Iterator<InstanceType> iterator()
    {
        return getTypes().iterator();
    }

    public Optional<InstanceType> first()
    {
        synchronized (lock_)
        {
            return this.instanceTypes_.isEmpty() ? Optional.<InstanceType> absent() : Optional.of(this.instanceTypes_.get(0));
        }
    }

    public int size()
    {
        synchronized (lock_)
        {
            return this.instanceTypes_.size();
        }
    }

    public boolean isEmpty()
    {
        synchronized (lock_)
        {
            return this.instanceTypes_.isEmpty();
        }
    }
    
    @Override
    public String toString()
    {
        return this.instanceTypes_.toString();
    }
}
