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
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class CpuSocketState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 855910027176700012L;
    
    private final CpuSocket socket_;
    private final Frequency currentFrequency_;
    private final CpuAverageLoad averageLoad_;

    public CpuSocketState(CpuSocket socket, Frequency frequency, CpuAverageLoad averageLoad)
    {
        this.socket_ = checkNotNull(socket);
        this.currentFrequency_ = frequency;
        this.averageLoad_ = checkNotNull(averageLoad);
    }

    public CpuStatePerc[] value()
    {
        Collection<CpuState> cores = this.socket_.cores().get();

        CpuStatePerc[] states = new CpuStatePerc[cores.size()];

        int i = 0;
        
        for (CpuState core : cores)
        {
            states[i++] = core.state();
        }
        return states;
    }

    /**
     * @return the currentFrequency
     */
    public Frequency frequency()
    {
        return currentFrequency_;
    }
    
    public CpuAverageLoad averageLoad()
    {
        return this.averageLoad_;
    }
}
