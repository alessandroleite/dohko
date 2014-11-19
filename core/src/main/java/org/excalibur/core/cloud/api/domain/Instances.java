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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.util.UnmodifiableIterator;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "instances")
@XmlType(name = "instances")
public final class Instances implements Serializable, Iterable<VirtualMachine>
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 3818228691298040620L;

    // private final Map<String, VirtualMachine> instancesMap_ = new HashMap<String, VirtualMachine>();

    @XmlElement(name = "instance")
    private final List<VirtualMachine> instances_ = Lists.newCopyOnWriteArrayList();

    public Instances()
    {
        super();
    }

    public Instances(Iterable<VirtualMachine> instances)
    {
        this.addInstances(instances);
    }

    public Instances(VirtualMachine... instances)
    {
        addInstances(instances);
    }

    public Instances addInstance(VirtualMachine instance)
    {
        if (instance != null && !this.instances_.contains(instance))
        {
            this.instances_.add(instance);
        }
        return this;
    }

    public Instances addInstances(final Iterable<VirtualMachine> instances)
    {
        for (VirtualMachine instance : instances)
        {
            this.addInstance(instance);
        }

        return this;
    }

    public Instances addInstances(VirtualMachine... instances)
    {
        for (VirtualMachine instance : instances)
        {
            this.addInstance(instance);
        }

        return this;
    }

    // public ImmutableList<VirtualMachine> removeInstances(String... instanceIds)
    // {
    // List<VirtualMachine> removedInstances = new ArrayList<VirtualMachine>();
    //
    // for (String instance : instanceIds)
    // {
    // VirtualMachine removedInstance = this.instancesMap_.remove(instance);
    //
    // if (removedInstance != null)
    // {
    // removedInstances.add(removedInstance);
    // }
    // }
    //
    // return ImmutableList.copyOf(removedInstances);
    // }

    /**
     * Returns an immutable {@link List} with the available instances.
     * 
     * @return A non <code>null</code> {@link List} with the instances.
     */
    public List<VirtualMachine> getInstances()
    {
        return ImmutableList.copyOf(instances_);
    }

    @Override
    public Iterator<VirtualMachine> iterator()
    {
        return new UnmodifiableIterator<VirtualMachine>(this.instances_.iterator());
    }

    public Optional<VirtualMachine> first()
    {
        return this.instances_.isEmpty() ? Optional.<VirtualMachine> absent() : Optional.of(this.instances_.get(0));
    }

    public int size()
    {
        return this.instances_.size();
    }

    public boolean isEmpty()
    {
        return this.instances_.isEmpty();
    }

    public Iterable<String> getInstanceNames()
    {
        return Iterables.transform(this, new Function<VirtualMachine, String>()
        {
            @Override
            @Nullable
            public String apply(@Nullable VirtualMachine input)
            {
                return input.getName();
            }
        });
    }
    
    public String[] instancesName()
    {
        return new Function<Instances, String[]>()
        {
            @Override
            public String[] apply(Instances input)
            {
                String names[] = new String[input.size()];
                
                for (int i = 0; i < names.length; i++)
                {
                    names[i] = input.get(i).getName();
                }
                return names;
            }

        }.apply(this);
    }

    protected VirtualMachine get(int index)
    {
        checkElementIndex(index, this.instances_.size());
        return this.instances_.get(index);
    }
}
