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

import static org.excalibur.core.compute.monitoring.domain.CpuStatePerc.builder;
import lshw.types.Capabilities;

import org.excalibur.core.compute.monitoring.domain.CpuState;
import org.excalibur.core.compute.monitoring.domain.CpuSocket;
import org.excalibur.core.compute.monitoring.domain.Machine;
import org.excalibur.core.compute.monitoring.domain.Memory;
import org.excalibur.core.compute.monitoring.domain.MemoryType;
import org.excalibur.core.compute.monitoring.domain.CpuStatePerc.CpuPercStateBuilder;
import org.excalibur.core.compute.monitoring.domain.io.Storages;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterface;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterfaceInfo;
import org.excalibur.core.compute.monitoring.domain.net.NetworkInterfaces;
import org.excalibur.core.compute.monitoring.resource.SigarFactory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarProxy;
import org.hyperic.sigar.Swap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardwareBuilder
{
    private static final Logger LOG = LoggerFactory.getLogger(HardwareBuilder.class.getName());
    
    public static Machine machine()
    {
        // checkArgument(os != null);

        try
        {
            return new MachineBuilder()
                    .id(physicalMachineId())
                    .name(physicalMachineId())
                    .netInterfaces(netInterfaces())
                    .cpus(cpus())
                    .memories(memories())
                    .storages(storages())
                    .build();
        }
        catch (Throwable e)
        {
            LOG.error("Error on building the machine. Error message [{}]", e.getMessage());   
        }
        
        return null;
    }

    public static String physicalMachineId() throws SigarException
    {
        return hypervisor().getNetInterfaceConfig().getHwaddr();
    }

    public static Memory[] memories() throws SigarException
    {
        Mem mem = hypervisor().getMem();
        Swap swap = hypervisor().getSwap();

        return new Memory[] 
                { 
                  new MemoryBuilder().size(mem.getTotal()).ofType(MemoryType.RAM).build(),
                  new MemoryBuilder().size(swap.getTotal()).ofType(MemoryType.SWAP).build() 
                };
    }

    public static CpuSocket[] cpus() throws SigarException
    {
        CpuPerc[] cpuPercs = hypervisor().getCpuPercList();
        org.hyperic.sigar.Cpu[] cpuList = hypervisor().getCpuList();
        
        CpuState[] states = new CpuState[cpuList.length];
        
        for (int i = 0; i < cpuList.length; i++)
        {
            states[i] = new CpuState(i, cpuList[i].getIdle(), cpuList[i].getIrq(), cpuList[i].getNice(), cpuList[i].getSoftIrq(), cpuList[i].getStolen(),
                              cpuList[i].getSys(), cpuList[i].getTotal(), cpuList[i].getUser(), cpuList[i].getWait());
            
            CpuPercStateBuilder builder = builder().cpu(i)
                    .combined(cpuPercs[i].getCombined())
                    .idle(cpuPercs[i].getIdle())
                    .irq(cpuPercs[i].getIrq())
                    .nice(cpuPercs[i].getNice())
                    .softIrq(cpuPercs[i].getSoftIrq())
                    .sys(cpuPercs[i].getSys())
                    .stolen(cpuPercs[i].getStolen())
                    .user(cpuPercs[i].getUser())
                    .wait(cpuPercs[i].getWait());
            
            states[i].setStatePerc(builder.build());
        }

        org.hyperic.sigar.CpuInfo[] cpuInfos = hypervisor().getCpuInfoList();
        CpuSocket[] sockets;
        
        if (cpuInfos != null && cpuInfos.length > 0)
        {
            sockets = new CpuSocket[cpuInfos[0].getTotalSockets()];

            for (int i = 0; i < sockets.length; i++)
            {
                sockets[i] = new CpuSocketBuilder()
                        .model(cpuInfos[i].getModel())
                        .vendor(cpuInfos[i].getVendor())
                        .cores(states)
                        .scalingFrequencies()
                        .minFrequency(cpuInfos[i].getMhzMin())
                        .maxFrequency(cpuInfos[i].getMhzMax())
                        .cacheSize(cpuInfos[i].getCacheSize())
                        .build();
            }
        } 
        else 
        {
            sockets = new CpuSocket[]{new CpuSocketBuilder().cores(states).maxFrequency(1).minFrequency(1).build()};
        }
        
        return sockets;
    }

    public static NetworkInterfaces netInterfaces() throws SigarException
    {
        String[] interfacesId = hypervisor().getNetInterfaceList();
        NetInterfaceConfig primaryInterface = hypervisor().getNetInterfaceConfig();

        NetworkInterfaces interfaces = new NetworkInterfaces();

        for (int i = 0; i < interfacesId.length; i++)
        {
            NetInterfaceConfig interfaceConfig = hypervisor().getNetInterfaceConfig(interfacesId[i]);

            NetInfo netInfo = hypervisor().getNetInfo();

            NetworkInterface ni = new NetworkInterfaceBuilder()
                    .id(interfaceConfig.getHwaddr().toLowerCase())
                    .logicalName(interfaceConfig.getName())
                    .capabilities(new Capabilities())
                    .ofType(interfaceConfig.getType())
                    .isPrimary(primaryInterface.getHwaddr().equalsIgnoreCase(interfaceConfig.getHwaddr()))
                    .networkInfo(
                            new NetworkInterfaceInfo(netInfo.getDefaultGateway(), 
                                    netInfo.getPrimaryDns(), 
                                    netInfo.getSecondaryDns(), 
                                    netInfo.getDomainName(), 
                                    netInfo.getHostName(), 
                                    interfaceConfig.getAddress6(), 
                                    interfaceConfig.getNetmask(),
                                    interfaceConfig.getBroadcast(), 
                                    interfaceConfig.getAddress(), 
                                    interfaceConfig.getDestination()))
                    .build();
            interfaces.add(ni);
        }

        return interfaces;
    }
    
    public static Storages storages() throws SigarException
    {
        Storages storages = new Storages();
//        FileSystem[] fileSystemList = hypervisor().getFileSystemList();
        return storages;
    }

    public static SigarProxy hypervisor()
    {
        return SigarFactory.getInstance();
    }
}
