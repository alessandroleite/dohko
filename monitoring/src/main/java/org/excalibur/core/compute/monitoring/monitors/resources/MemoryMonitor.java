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
package org.excalibur.core.compute.monitoring.monitors.resources;

import org.excalibur.core.compute.monitoring.domain.MemoryState;
import org.excalibur.core.compute.monitoring.resource.SigarFactory;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.excalibur.core.compute.monitoring.domain.MemoryState.builder;

public class MemoryMonitor
{
    private static final Logger LOG = LoggerFactory.getLogger(MemoryMonitor.class.getName());

    public MemoryState[] probe()
    {
        MemoryState[] data = new MemoryState[2];

        try
        {
            Mem mem = SigarFactory.getInstance().getMem();
            Swap swap = SigarFactory.getInstance().getSwap();
            
			data[0] = builder()
					.ram()
					.free(mem.getFree())
					.size(mem.getTotal())
					.build();
			
			data[1] = builder()
					.swap()
					.free(swap.getFree())
					.size(swap.getTotal())
					.used(swap.getUsed())
					.build();
        }
        catch (SigarException exception)
        {
            LOG.error("Failed to gather process info from Sigar: {}", exception.getMessage(), exception);
        }
        
        return data;
    }
}
