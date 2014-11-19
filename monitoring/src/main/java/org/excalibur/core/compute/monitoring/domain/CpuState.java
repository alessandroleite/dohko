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

import com.google.common.base.Objects;

public class CpuState implements Comparable<CpuState>, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2725754504183483440L;
   
    private volatile CpuSocket cpuSocket;
    private volatile CpuStatePerc statePerc;

    private final Integer id;
    private final long idle;
    private final long irq;
    private final long nice;
    private final long softIrq;
    private final long stolen;
    private final long sys;
    private final long total;
    private final long user;
    private final long wait;

    public CpuState(Integer id, long idle, long irq, long nice, long softIrq, long stolen, long sys, long total, long user, long wait)
    {
        this.id = id;
        this.idle = idle;
        this.irq = irq;
        this.nice = nice;
        this.softIrq = softIrq;
        this.stolen = stolen;
        this.sys = sys;
        this.total = total;
        this.user = user;
        this.wait = wait;
    }

    public CpuState(CpuSocket cpuSocket, Integer id, long idle, long irq, long nice, long softIrq, long stolen, long sys, long total, long user, long wait)
    {
        this(id, idle, irq, nice, softIrq, stolen, sys, total, user, wait);
        this.cpuSocket = cpuSocket;
    }

    /**
     * Returns CPU id.
     * 
     * @return this CPU id.
     */
    public Integer id()
    {
        return this.id;
    }

    /**
     * Return the load state of the CPU.
     * 
     * @return the load state of the CPU
     */
    public double load()
    {
        return this.state().getCombined() * 100.0D;
    }

    @Override
    public int compareTo(CpuState o)
    {
        return this.id.compareTo(o.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getCpuSocket(), this.name());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CpuState))
        {
            return false;
        }

        CpuState other = (CpuState) obj;

        return Objects.equal(this.getCpuSocket(), other.getCpuSocket()) && Objects.equal(this.name(), other.name());

    }

    @Override
    public String toString()
    {
        return "CPU " + (this.id) + " .... state: " + (this.state() == null ? "UNKNOWN" : this.state());
    }

    public CpuStatePerc setState(CpuStatePerc state)
    {
        synchronized (this)
        {
            CpuStatePerc old_state = this.statePerc;
            this.statePerc = state;
            return old_state;
        }
    }

    /**
     * @return the state
     */
    public CpuStatePerc state()
    {
        return statePerc;
    }

    /**
     * @return the name
     */
    public String name()
    {
        return String.valueOf(id);
    }

    /**
     * @return the cpuSocket
     */
    public CpuSocket getCpuSocket()
    {
        return cpuSocket;
    }

    /**
     * @param cpuSocket
     *            the cpuSocket to set
     */
    public CpuState setCpuSocket(CpuSocket cpuSocket)
    {
        this.cpuSocket = cpuSocket;
        return this;
    }

    /**
     * @return the statePerc
     */
    public CpuStatePerc getStatePerc()
    {
        return statePerc;
    }

    /**
     * @param statePerc
     *            the statePerc to set
     */
    public CpuState setStatePerc(CpuStatePerc statePerc)
    {
        this.statePerc = statePerc;
        return this;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @return the idle
     */
    public long getIdle()
    {
        return idle;
    }

    /**
     * @return the irq
     */
    public long getIrq()
    {
        return irq;
    }

    /**
     * @return the nice
     */
    public long getNice()
    {
        return nice;
    }

    /**
     * @return the softIrq
     */
    public long getSoftIrq()
    {
        return softIrq;
    }

    /**
     * @return the stolen
     */
    public long getStolen()
    {
        return stolen;
    }

    /**
     * @return the sys
     */
    public long getSys()
    {
        return sys;
    }

    /**
     * @return the total
     */
    public long getTotal()
    {
        return total;
    }

    /**
     * @return the user
     */
    public long getUser()
    {
        return user;
    }

    /**
     * @return the wait
     */
    public long getWait()
    {
        return wait;
    }

}
