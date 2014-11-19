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
package org.excalibur.core.compute.monitoring.domain.builders;

import java.util.HashMap;
import java.util.Map;

import org.excalibur.core.compute.monitoring.domain.Memory;
import org.excalibur.core.compute.monitoring.domain.MemoryState;
import org.excalibur.core.compute.monitoring.domain.MemoryType;
import org.excalibur.core.compute.monitoring.domain.RamMemory;

public class MemoryBuilder
{
    /**
     * The type of the memory. The default is {@link MemoryType#RAM}.
     */
    private MemoryType memoryType = MemoryType.RAM;

    /**
     * The size of the memory.
     */
    private long size;

    /**
     * The properties of the memory.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * The state of the memory (percentage of memory used and memory available).
     */
    private MemoryState memoryUsage;

    public Memory build()
    {
        Memory mem = null;

        if (memoryType == null)
        {
            this.memoryType = MemoryType.RAM;
        }

        switch (this.memoryType)
        {
        case RAM:
            mem = new RamMemory(size, memoryUsage);
            break;
        default:
            mem = new Memory(this.size)
            {
                /**
                 * Serial code version <code>serialVersionUID</code> for serialization.
                 */
                private static final long serialVersionUID = -8952374020496422223L;

                @Override
                public MemoryType getType()
                {
                    return memoryType;
                }
            };

        }
        return mem;
    }

    public MemoryBuilder ofType(MemoryType type)
    {
        this.memoryType = type;
        return this;
    }

    public MemoryBuilder size(long memorySize)
    {
        this.size = memorySize;
        return this;
    }

    public MemoryBuilder properties(Map<String, String> memoryProperties)
    {
        this.properties.putAll(memoryProperties);
        return this;
    }

    public MemoryBuilder property(String propertyName, String propertyValue)
    {
        this.properties.put(propertyName, propertyValue);
        return this;
    }
}
