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
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public abstract class Memory implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 4342012357072520778L;

    /** Total memory size */
    private final long size;

    /** Memory state */
    private volatile MemoryState state;

    /** Memory properties */
    private final Map<Integer, Map<String, String>> propertiesMap = Maps.newHashMap();

    /**
     * Instantiate a memory with a given size.
     * 
     * @param size
     *            The size of the memory. Must be greater than zero.
     */
    public Memory(long size)
    {
        this.size = size;
    }

    public Memory(long size, MemoryState usage)
    {
        this(size);
        this.state = usage;
    }

    public Memory(long size, MemoryState usage, Map<Integer, Map<String, String>> properties)
    {
        this(size);
        this.state = usage;
        this.propertiesMap.putAll(properties);
    }

    /**
     * Return the {@link Memory} type.
     * 
     * @return the {@link Memory} type.
     * @see MemoryType
     */
    public abstract MemoryType getType();

    /**
     * Returns the size of this {@link Memory}
     * 
     * @return The size of this {@link Memory}
     */
    public long size()
    {
        return size;
    }

    /**
     * Update the state of this {@link Memory}.
     * 
     * @param newMemoryState
     *            The new state of this {@link Memory}.
     */
    public MemoryState setState(MemoryState newMemoryState)
    {
        synchronized (this)
        {
            final MemoryState previousMemoryState = this.state;
            this.state = newMemoryState;

            return previousMemoryState;
        }
    }

    /**
     * Returns the current state of this {@link Memory}.
     * 
     * @return the current state of this {@link Memory}.
     * @see MemoryState
     */
    public MemoryState state()
    {
        return this.state;
    }

    /**
     * @return the propertiesMap
     */
    public Map<Integer, Map<String, String>> propertiesMap()
    {
        return Collections.unmodifiableMap(propertiesMap);
    }

    @Override
    public String toString()
    {
        return "Memory " + this.getType().name() + "[" + this.state() + "]";
    }

    public String description()
    {
        StringBuilder description = new StringBuilder();

        if (!this.propertiesMap.isEmpty())
        {
            description.append(propertiesMap.get(1).get("Manufacturer") + " " + 
                               propertiesMap.get(1).get("Fundamental Memory type") + " " +
                               propertiesMap.get(1).get("Maximum module speed") + " " + 
                               propertiesMap.size() + "/" + 
                               propertiesMap.get(1).get("Size"));
        }

        return description.toString();
    }
}
