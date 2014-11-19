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
@XmlRootElement(name = "instance-type-reqs")
@XmlType(name = "instance-type-reqs")
public class InstanceTypeReqs implements Serializable, Iterable<InstanceTypeReq>
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 8354916041635602562L;

    private final transient Object lock_ = new Integer(1);

    @XmlElement(name = "instance-type")
    private final List<InstanceTypeReq> instanceTypes_ = newCopyOnWriteArrayList();

    public InstanceTypeReqs()
    {
        super();
    }

    public InstanceTypeReqs(Iterable<InstanceTypeReq> types)
    {
        this.addAll(types);
    }

    public InstanceTypeReqs addAll(Iterable<InstanceTypeReq> types)
    {
        if (null != types)
        {
            for (InstanceTypeReq type : types)
            {
                if (null != type)
                {
                    this.instanceTypes_.add(type);
                }
            }
        }
        return this;
    }

    public InstanceTypeReq get(int i)
    {
        checkElementIndex(i, this.instanceTypes_.size());
        return this.instanceTypes_.get(i);
    }

    /**
     * 
     * @param instanceTypes
     * @return
     */
    public static InstanceTypeReqs valueOf(List<InstanceTypeReq> instanceTypes)
    {
        return new InstanceTypeReqs(instanceTypes);
    }

    public static InstanceTypeReqs newInstanceTypes()
    {
        return new InstanceTypeReqs();
    }

    public InstanceTypeReqs add(InstanceTypeReq type)
    {
        if (null != type)
        {
            this.instanceTypes_.add(type);
        }

        return this;
    }

    public InstanceTypeReq remove(InstanceTypeReq instanceType)
    {
        InstanceTypeReq removed = null;

        if (null != instanceType && !this.instanceTypes_.isEmpty())
        {
            int index = this.instanceTypes_.indexOf(instanceType);

            if (-1 != index)
            {
                removed = this.instanceTypes_.remove(index);
            }
        }

        return removed;
    }

    public List<InstanceTypeReq> getTypes()
    {
        synchronized (lock_)
        {
            return ImmutableList.copyOf(instanceTypes_);
        }
    }

    @Override
    public Iterator<InstanceTypeReq> iterator()
    {
        return getTypes().iterator();
    }

    public Optional<InstanceTypeReq> first()
    {
        return this.instanceTypes_.isEmpty() ? Optional.<InstanceTypeReq> absent() : Optional.of(this.instanceTypes_.get(0));
    }

    public int size()
    {
        return this.instanceTypes_.size();
    }

    public boolean isEmpty()
    {
        return this.instanceTypes_.isEmpty();
    }

    public void clear()
    {
        this.instanceTypes_.clear();
    }
}
