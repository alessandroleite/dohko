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

import static org.excalibur.core.compute.monitoring.domain.CpuStatePerc.builder;

import java.util.Map;

import org.excalibur.core.compute.monitoring.domain.CpuState;
import org.excalibur.core.compute.monitoring.domain.CpuAverageLoad;
import org.excalibur.core.compute.monitoring.domain.CpuSocket;
import org.excalibur.core.compute.monitoring.domain.CpuSocketState;
import org.excalibur.core.compute.monitoring.domain.CpuStatePerc.CpuPercStateBuilder;
import org.excalibur.core.compute.monitoring.domain.Frequency;
import org.excalibur.core.compute.monitoring.domain.Uptime;
import org.excalibur.core.compute.monitoring.resource.SigarFactory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpuMonitor
{
    private static final Logger LOG = LoggerFactory.getLogger(CpuMonitor.class.getName());

    public CpuSocketState probe(CpuSocket socket)
    {
        Map<Integer, CpuState> cores = socket.cores().getMap();
        
        try
        {
            CpuPerc[] cpuPercList = SigarFactory.getInstance().getCpuPercList();

            int i = 0;
            for (CpuPerc perc : cpuPercList)
            {
                CpuPercStateBuilder builder = builder().cpu(i)
                        .combined(perc.getCombined())
                        .idle(perc.getIdle())
                        .irq(perc.getIrq())
                        .nice(perc.getNice())
                        .softIrq(perc.getSoftIrq())
                        .sys(perc.getSys())
                        .stolen(perc.getStolen())
                        .user(perc.getUser())
                        .wait(perc.getWait());

                if (SigarLoader.IS_LINUX)
                {
                    builder.softIrq(perc.getSoftIrq()).stolen(perc.getStolen());
                }
                
                cores.get(i++).setState(builder.build());
            }
        }
        catch (SigarException exception)
        {
            LOG.error("Failed to gather process info from Sigar: {}", exception.getMessage(), exception);
        }
        
        return new CpuSocketState(socket, Frequency.NULL_FREQUENCY, getAverageLoad());
    }

    public CpuAverageLoad getAverageLoad()
    {
        try
        {
            return new CpuAverageLoad(SigarFactory.getInstance().getLoadAverage());
        }
        catch (SigarException e)
        {
            return new CpuAverageLoad(new double[3]);
        }
    }

    public Uptime getUptime()
    {
        try
        {
            return Uptime.valueOf(SigarFactory.getInstance().getUptime().getUptime());
        }
        catch (SigarException e)
        {
            return new Uptime(0);
        }
    }
}
