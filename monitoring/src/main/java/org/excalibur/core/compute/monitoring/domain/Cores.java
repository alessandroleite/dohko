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
package org.excalibur.core.compute.monitoring.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Cores implements Iterable<CpuState>, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -1723954007026235680L;

    private transient final Object lock_ = new Integer(1);

    /**
     * {@link Map} with the cpus that represents the cores where the key is the id of the core/cpu.
     */
    private final Map<Integer, CpuState> cpus_ = Maps.newHashMap();

    /**
     * The socket of the cpus.
     */
    private final CpuSocket cpuSocket;

    /**
     * Create an instance of this class with a given {@link CpuSocket}.
     * 
     * @param socket
     *            The socket of the cores. May not be <code>null</code>.
     */
    public Cores(CpuSocket socket)
    {
        this.cpuSocket = checkNotNull(socket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<CpuState> iterator()
    {
        return this.get().iterator();
    }

    /**
     * Add a given {@link CpuState} to this {@link Cores} {@link List}.
     * 
     * @param cpu
     *            The {@link CpuState} instance to be added. May not be <code>null</code>.
     * @return The old instance of the given {@link CpuState} or <code>null</code>.
     */
    public CpuState add(CpuState cpu)
    {
        synchronized (lock_)
        {
            return this.cpus_.put(cpu.id(), cpu);
        }
    }

    /**
     * Return an unmodifiable {@link Collection} with the current view of this {@link List}.
     * 
     * @return Return an unmodifiable {@link Collection} with the current view of this {@link List}.
     * @see Collections#unmodifiableCollection(Collection)
     */
    public Collection<CpuState> get()
    {
        List<CpuState> list = new ArrayList<CpuState>();

        synchronized (lock_)
        {
            list.addAll(this.cpus_.values());
        }

        Collections.sort(list);
        return Collections.unmodifiableCollection(list);
    }

    public Map<Integer, CpuState> getMap()
    {
        synchronized (lock_)
        {
            return Collections.unmodifiableMap(this.cpus_);
        }
    }

    /**
     * Return the {@link CpuState} instance that has the given name.
     * 
     * @param name
     *            The name of {@link CpuState} to be returned.
     * @return The {@link CpuState} instance that has the given name or <code>null</code> if it doesn't exist.
     */
    public CpuState get(Integer id)
    {
        synchronized (lock_)
        {
            return this.cpus_.get(id);
        }
    }

    /**
     * @return the cpuSocket
     */
    public CpuSocket getCpuSocket()
    {
        return cpuSocket;
    }

    /**
     * Remove a given {@link CpuState}.
     * 
     * @param cpu
     *            A {@link CpuState} instance to be removed.
     * @return The {@link CpuState} removed.
     */
    public CpuState remove(CpuState cpu)
    {
        synchronized (lock_)
        {
            return this.cpus_.remove(cpu.name());
        }
    }

    /**
     * Return the number of cores.
     * 
     * @return Return the number of cores.
     */
    public int size()
    {
        synchronized (lock_)
        {
            return this.cpus_.size();
        }
    }
}
