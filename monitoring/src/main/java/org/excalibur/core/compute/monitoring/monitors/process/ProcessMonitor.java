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

import java.util.HashMap;
import java.util.Map;

import org.excalibur.core.compute.monitoring.domain.ProcessCpuState;
import org.excalibur.core.compute.monitoring.domain.ProcessMemoryState;
import org.excalibur.core.compute.monitoring.domain.ProcessState;
import org.excalibur.core.compute.monitoring.domain.ProcessTime;
import org.excalibur.core.compute.monitoring.resource.SigarFactory;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcFd;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.ProcTime;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class ProcessMonitor
{
    private final SigarProxy sigar = SigarFactory.getInstance();

    private final Logger logger = LoggerFactory.getLogger(ProcessMonitor.class.getName());

    public ImmutableMap<Long, ProcessState> probe(long... processIds)
    {
        Map<Long, ProcessState> processes = new HashMap<Long, ProcessState>();

        for (long pid : processIds)
        {
            try
            {
                ProcState procState = sigar.getProcState(pid);
                ProcCpu cpuState = sigar.getProcCpu(pid);
                ProcMem mem = sigar.getProcMem(pid);
                ProcTime time = sigar.getProcTime(pid);
                ProcFd procFd = sigar.getProcFd(pid);

                ProcessMemoryState memoryState = new ProcessMemoryState(pid, mem.getResident(), mem.getPageFaults(),
                        mem.getMajorFaults(), mem.getShare(), mem.getMinorFaults(), mem.getSize());

                ProcessCpuState processCpuState = new ProcessCpuState(pid, cpuState.getUser(), cpuState.getLastTime(), cpuState.getPercent(),
                        cpuState.getStartTime(), cpuState.getTotal(), cpuState.getSys());

                ProcessTime processTime = new ProcessTime(pid, time.getUser(), time.getStartTime(), time.getTotal(), time.getSys());

                ProcessState data = new ProcessState(pid, procState.getThreads(), procState.getTty(), procState.getProcessor(),
                        procState.getPriority(), procState.getNice(), procFd.getTotal(), procState.getPriority(),
                        ProcessState.ProcState.valueOf(procState.getState()), processCpuState, memoryState, processTime);

//                System.out.println(CpuPerc.format(cpuState.getPercent()));
//                System.out.println((cpuState.getPercent() * 100) / sigar.getCpuList().length);
//                System.out.println(Sigar.formatSize(mem.getSize()));

                processes.put(pid, data);
                
            }
            catch (SigarException exception)
            {
                logger.error("Failed to gather process info from Sigar: {}", exception.getMessage(), exception);
            }
        }
        return ImmutableMap.copyOf(processes);
    }
}
