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

import java.util.Map;

public final class RamMemory extends Memory
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = -6584658155333662116L;

    public RamMemory(long size)
    {
        super(size);
    }

    public RamMemory(long size, MemoryState usage)
    {
        super(size, usage);
    }

    public RamMemory(long size, MemoryState usage, Map<Integer, Map<String, String>> properties)
    {
        super(size, usage, properties);
    }

    @Override
    public MemoryType getType()
    {
        return MemoryType.RAM;
    }
}
