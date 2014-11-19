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

public class CpuStatePerc implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 3301419563966280951L;

    public static class CpuPercStateBuilder
    {
        private int cpuId;
        private double user;
        private double sys;
        private double idle;
        private double wait;
        private double nice;
        private double combined;
        private double irq;
        private double softIrq;
        private double stolen;

        public CpuPercStateBuilder cpu(int id)
        {
            this.cpuId = id;
            return this;
        }

        public CpuPercStateBuilder user()
        {
            return this;
        }

        public CpuStatePerc build()
        {
            return new CpuStatePerc(cpuId, combined, user, sys, idle, wait, nice, irq, softIrq, stolen);
        }

        public CpuPercStateBuilder combined(double combined)
        {
            this.combined = combined;
            return this;
        }

        public CpuPercStateBuilder idle(double idle)
        {
            this.idle = idle;
            return this;
        }

        public CpuPercStateBuilder irq(double irq)
        {
            this.irq = irq;
            return this;
        }

        public CpuPercStateBuilder nice(double nice)
        {
            this.nice = nice;
            return this;
        }

        public CpuPercStateBuilder sys(double sys)
        {
            this.sys = sys;
            return this;
        }

        public CpuPercStateBuilder user(double user)
        {
            this.user = user;
            return this;
        }

        public CpuPercStateBuilder softIrq(double softIrq)
        {
            this.softIrq = softIrq;
            return this;
        }

        public CpuPercStateBuilder stolen(double stolen)
        {
            this.stolen = stolen;
            return this;
        }

        public CpuPercStateBuilder wait(double wait)
        {
            this.wait = wait;
            return this;
        }
    }

    public static CpuPercStateBuilder builder()
    {
        return new CpuPercStateBuilder();
    }

    private final int cpuId;
    private final double user;
    private final double sys;
    private final double idle;
    private final double wait;
    private final double nice;
    private final double combined;
    private final double irq;
    private final double softIrq;
    private final double stolen;

    CpuStatePerc(int cpuId, double combined, double user, double sys, double idle, double wait, double nice, double irq, double softIrq, double stolen)
    {
        this.cpuId = cpuId;
        this.combined = combined;
        this.user = user;
        this.sys = sys;
        this.idle = idle;
        this.wait = wait;
        this.nice = nice;
        this.irq = irq;
        this.softIrq = softIrq;
        this.stolen = stolen;
    }

    @Override
    public String toString()
    {
        // return "states ....: " + format(this.combined) + " combined, " + format(this.user) + " user, " + format(this.sys) + " system, "
        // + format(this.nice) + " nice, " + format(this.wait) + " wait, " + format(this.idle) + " idle] ";
        return super.toString();
    }

    /**
     * @return the cpuId
     */
    public int getCpuId()
    {
        return cpuId;
    }

    /**
     * @return the user
     */
    public double getUser()
    {
        return user;
    }

    /**
     * @return the sys
     */
    public double getSys()
    {
        return sys;
    }

    /**
     * @return the idle
     */
    public double getIdle()
    {
        return idle;
    }

    /**
     * @return the wait
     */
    public double getWait()
    {
        return wait;
    }

    /**
     * @return the nice
     */
    public double getNice()
    {
        return nice;
    }

    /**
     * @return the combined
     */
    public double getCombined()
    {
        return combined;
    }

    /**
     * @return the irq
     */
    public double getIrq()
    {
        return irq;
    }

    /**
     * @return the softIrq
     */
    public double getSoftIrq()
    {
        return softIrq;
    }

    /**
     * @return the stolen
     */
    public double getStolen()
    {
        return stolen;
    }
}
