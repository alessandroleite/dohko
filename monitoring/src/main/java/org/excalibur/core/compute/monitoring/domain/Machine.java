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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.excalibur.core.compute.monitoring.domain.io.Storages;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterfaces;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class Machine implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2205480968617538219L;

    /**
     * MAC address of the primary interface.
     */
    private final String id;

    /** The machine's name. */
    private final String name;

    /** The CPUs. */
    private final CpuSocket[] cpus;

    /** Memory system. */
    private final Memory[] memories;

    /**
     * Network interfaces.
     */
    private final NetworkInterfaces networkInterfaces;

    /**
     * The disks of that {@link Machine}.
     */
    private final Storages storages;

    /**
     * Creates an {@link Machine}.
     * 
     * @param machineId
     *            The id of the machine. Might not be <code>null</code>.
     * @param machineName
     *            The name of the machine. Might not be <code>null</code>.
     * @param machineMemories
     *            The memory of the machine. Might not be <code>null</code>.
     * @param machineStorages
     *            The storages of the machine. Might not be <code>null</code>.
     * @param machineNetInterfaces
     *            The network interface.
     * @param sockets
     *            The CPU socket(s) of the machine. Might not be <code>null</code>.
     * @param machineOs
     *            The operating system of the machine. Might not be <code>null</code>.
     */
    public Machine(String machineId, String machineName, Memory[] machineMemories, Storages machineStorages, NetworkInterfaces machineNetInterfaces,
            CpuSocket[] sockets)
    {

        this.id = requireNonNull(machineId);
        this.name = requireNonNull(machineName);
        this.memories = requireNonNull(machineMemories);
        this.storages = requireNonNull(machineStorages);
        this.networkInterfaces = machineNetInterfaces;
        this.cpus = requireNonNull(sockets);
    }

    /**
     * Returns the combined CPU load of this {@link Machine}.
     * 
     * @return The combined CPU load of this {@link Machine}.
     */
    public Double combinedCpuLoad()
    {
        return this.cpus[0].getCombinedLoad();
    }

    /**
     * @return the memory
     */
    public Memory[] memories()
    {
        return memories;
    }

    /**
     * Returns the reference to RAM memory system.
     * 
     * @return Returns the reference to RAM memory system.
     */
    public Memory ram()
    {
        return filterMemoryBy(MemoryType.RAM)[0];
    }

    /**
     * Returns the instance of the swap {@link Memory}.
     * 
     * @return The swap memory of this {@link Machine}.
     */
    public Memory swap()
    {
        return filterMemoryBy(MemoryType.SWAP)[0];
    }

    /**
     * @return the name.
     */
    public String name()
    {
        return name;
    }

    /**
     * @return the cpus.
     */
    public CpuSocket[] cpus()
    {
        return cpus;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the networkInterfaces
     */
    public NetworkInterfaces getNetworkInterfaces()
    {
        return networkInterfaces;
    }

    /**
     * @return the storages
     */
    public Storages getStorages()
    {
        return storages;
    }

    /**
     * Filter and returns the {@link Memory} of the given type.
     * 
     * @param type
     *            The type of the memory to be returned.
     * @return A not-null array with the memory found or empty if there isn't any memory of the given type.
     */
    private Memory[] filterMemoryBy(final MemoryType type)
    {
        Collection<Memory> filtered = Collections2.filter(Lists.newArrayList(this.memories), new Predicate<Memory>()
        {
            @Override
            public boolean apply(Memory input)
            {
                return type.equals(input.getType());
            }
        });

        return filtered.toArray(new Memory[filtered.size()]);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        Machine other = (Machine) obj;
        if (id == null)
        {
            if (other.id != null)
            {
                return false;
            }
        }
        else if (!id.equals(other.id))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
