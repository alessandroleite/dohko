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
package org.excalibur.core.compute.monitoring.monitors.process;


import org.excalibur.core.compute.monitoring.domain.ProcessMemoryState;
import org.excalibur.core.compute.monitoring.resource.SigarFactory;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

public class ProcessMemoryWatch implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(ProcessMemoryWatch.class.getName());

    private final SigarProxy sigar = SigarFactory.getInstance();
    private final EventBus eventBus;
    private final long pid;

    public ProcessMemoryWatch(long pid, EventBus bus)
    {
        this.pid = pid;
        this.eventBus = bus;
    }

    @Override
    public void run()
    {
        try
        {
            // long lastTime = System.currentTimeMillis();
            ProcMem lastMem = sigar.getProcMem(pid);

            while (true)
            {
                ProcMem curMem = sigar.getProcMem(pid);
                boolean changed = (curMem.getSize() - lastMem.getSize()) > 0 || (curMem.getResident() - lastMem.getSize() > 0)
                        || (curMem.getShare() - lastMem.getShare() > 0);

                if (changed)
                {
                    // long curTime = System.currentTimeMillis();
                    // long timeDiff = curTime - lastTime;
                    // lastTime = curTime;

                    ProcessMemoryState memoryState = new ProcessMemoryState(pid, curMem.getResident(), curMem.getPageFaults(),
                            curMem.getMajorFaults(), curMem.getShare(), curMem.getMinorFaults(), curMem.getSize());

                    eventBus.post(memoryState);
                }

                lastMem = curMem;
                try
                {
                    Thread.sleep(10 * 1000);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
        catch (SigarException exception)
        {
            logger.error("Failed to gather process info from Sigar: {}", exception.getMessage(), exception);
        }
    }
}
