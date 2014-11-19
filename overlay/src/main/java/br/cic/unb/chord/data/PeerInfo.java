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
package br.cic.unb.chord.data;


import java.io.Serializable;

import org.excalibur.core.compute.monitoring.domain.CpuSocketState;
import org.excalibur.core.compute.monitoring.domain.MemoryState;
import org.excalibur.core.compute.monitoring.domain.Uptime;

import static com.google.common.base.Preconditions.checkNotNull;

public final class PeerInfo implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 3298563564170713710L;
    
    private final Peer           peer_;
    private final CpuSocketState cpuState_;
    private final MemoryState[]  memoryState_;
    private final Uptime         uptime_;

    public PeerInfo(Peer peer, CpuSocketState cpuState, MemoryState[] memoryState, Uptime uptime)
    {
        this.peer_ = checkNotNull(peer);
        this.cpuState_ = checkNotNull(cpuState);
        this.memoryState_ = checkNotNull(memoryState);
        this.uptime_ = checkNotNull(uptime);
    }

    /**
     * @return the peer
     */
    public Peer getPeer()
    {
        return this.peer_;
    }

    /**
     * @return the cpuState
     */
    public CpuSocketState getCpuState()
    {
        return this.cpuState_;
    }

    /**
     * @return the memoryState
     */
    public MemoryState[] getMemoryState()
    {
        return this.memoryState_;
    }

    /**
     * @return the uptime
     */
    public Uptime getUptime()
    {
        return this.uptime_;
    }
}
