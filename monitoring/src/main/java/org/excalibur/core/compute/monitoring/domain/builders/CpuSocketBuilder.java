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

import org.excalibur.core.compute.monitoring.domain.CpuState;
import org.excalibur.core.compute.monitoring.domain.CpuSocket;
import org.excalibur.core.compute.monitoring.domain.Frequency;

import com.google.common.base.Preconditions;

public class CpuSocketBuilder
{
    /**
     * The vendor's name.
     */
    private String vendor;

    /**
     * The CPU model.
     */
    private String cpuModel;

    /**
     * The maximum CPU's frequency.
     */
    private long maxFrequency;
    /**
     * The minimum CPU's frequency.
     */
    private long minFrequency;

    /**
     * The CPU's cache size.
     */
    private long cacheSize;

    /**
     * 
     */
    private CpuState[] cores;

    /**
     * The CPU's frequencies.
     */
    private Frequency[] cpuFrequencies;

    public CpuSocket build()
    {
        Preconditions.checkArgument(cores != null && cores.length > 0, "Must have at least one core/cpu per socket.");

//        Preconditions.checkArgument(maxFrequency > 0 || cpuFrequencies != null && cpuFrequencies.length > 0,
//                "CPU maximum frequency must be greater than zero.");

        if (this.cpuFrequencies == null || this.cpuFrequencies.length == 0)
        {
            this.cpuFrequencies = new Frequency[] { Frequency.newFrequencyInMhz(this.minFrequency), Frequency.newFrequencyInMhz(this.maxFrequency) };
        }

        CpuSocket cpuSocket = new CpuSocket(vendor, cpuModel, maxFrequency, cacheSize, this.cores, cpuFrequencies);

        return cpuSocket;
    }

    public CpuSocketBuilder vendor(String vendorName)
    {
        this.vendor = vendorName;
        return this;
    }

    public CpuSocketBuilder model(String model)
    {
        this.cpuModel = model;
        return this;
    }

    public CpuSocketBuilder maxFrequency(int maxFrequencyValue)
    {
        this.maxFrequency = maxFrequencyValue;
        return this;
    }

    public CpuSocketBuilder minFrequency(int minFrequencyValue)
    {
        this.minFrequency = minFrequencyValue;
        return this;
    }

    public CpuSocketBuilder cacheSize(long cacheSizeValue)
    {
        this.cacheSize = cacheSizeValue;
        return this;
    }

    public CpuSocketBuilder cores(CpuState... cpus)
    {
        this.cores = cpus;
        return this;
    }

    public CpuSocketBuilder scalingFrequencies(Frequency... cpuFrequencyValues)
    {
        this.cpuFrequencies = cpuFrequencyValues;
        return this;
    }
}
