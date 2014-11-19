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
package org.excalibur.core.deployment.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.google.common.collect.Lists.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "network-rules")
public class NetworkRules implements Iterable<NetworkRule>, List<NetworkRule>
{
    @XmlElement(name = "network-rule")
    private List<NetworkRule> rules_ = newCopyOnWriteArrayList();

    @Override
    public Iterator<NetworkRule> iterator()
    {
        return rules().iterator();
    }

    public List<NetworkRule> rules()
    {
        return Collections.unmodifiableList(this.rules_);
    }

    @Override
    public int size()
    {
        return this.rules_.size();
    }

    @Override
    public boolean isEmpty()
    {
        return this.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return this.rules_.contains(o);
    }

    @Override
    public Object[] toArray()
    {
        return this.rules_.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return this.rules_.toArray(a);
    }

    @Override
    public boolean add(NetworkRule e)
    {
        if (e != null)
        {
            return this.rules_.add(e);
        }
        return false;
    }

    @Override
    public boolean remove(Object o)
    {
        return this.rules_.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return this.rules_.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends NetworkRule> c)
    {
        for (NetworkRule rule: c)
        {
            this.add(rule);
        }
        
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends NetworkRule> c)
    {
        return this.rules_.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return this.rules_.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return this.rules_.retainAll(c);
    }

    @Override
    public void clear()
    {
        this.rules_.clear();
    }

    @Override
    public NetworkRule get(int index)
    {
        return this.rules_.get(index);
    }

    @Override
    public NetworkRule set(int index, NetworkRule element)
    {
        if (element != null)
        {
            this.rules_.add(element);
        }
        return null;
    }

    @Override
    public void add(int index, NetworkRule element)
    {
        if (element != null)
        {
            this.rules_.add(index, element);
        }
    }

    @Override
    public NetworkRule remove(int index)
    {
        return this.rules_.remove(index);
    }

    @Override
    public int indexOf(Object o)
    {
        return this.rules_.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return this.rules_.lastIndexOf(o);
    }

    @Override
    public ListIterator<NetworkRule> listIterator()
    {
        return this.rules().listIterator();
    }

    @Override
    public ListIterator<NetworkRule> listIterator(int index)
    {
        return this.rules().listIterator(index);
    }

    @Override
    public List<NetworkRule> subList(int fromIndex, int toIndex)
    {
        return this.rules_.subList(fromIndex, toIndex);
    }
}
