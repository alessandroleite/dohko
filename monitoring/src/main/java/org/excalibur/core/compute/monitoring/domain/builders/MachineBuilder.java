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

import org.excalibur.core.compute.monitoring.domain.CpuSocket;
import org.excalibur.core.compute.monitoring.domain.Machine;
import org.excalibur.core.compute.monitoring.domain.Memory;
import org.excalibur.core.compute.monitoring.domain.io.Storage;
import org.excalibur.core.compute.monitoring.domain.io.Storages;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterfaces;

public class MachineBuilder
{
    private String machineId;
    private String name;
    private CpuSocket[] cpus;
    private Memory[] memories;
    private NetworkInterfaces networkInterfaces;
    private final Storages storages = new Storages();

    public Machine build()
    {
        Machine machine = new Machine(machineId, name, memories, storages, networkInterfaces, cpus);
        return machine;
    }

    public MachineBuilder id(String id)
    {
        this.machineId = id;
        return this;
    }

    public MachineBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public MachineBuilder cpus(CpuSocket... cpuSockets)
    {
        this.cpus = cpuSockets;
        return this;
    }

    public MachineBuilder memories(Memory... memories)
    {
        this.memories = memories;
        return this;
    }

    public MachineBuilder storages(Storage... storages)
    {
        if (storages != null)
        {
            for (Storage storage : storages)
            {
                this.storages.add(storage);
            }
        }
        return this;
    }
    
    public MachineBuilder storages(Storages storages)
    {
        this.storages.copyOf(storages);
        return this;
    }

    public MachineBuilder netInterfaces(NetworkInterfaces networkInterfaces)
    {
        this.networkInterfaces = networkInterfaces;
        return this;
    }
}
