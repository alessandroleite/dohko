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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public final class ProcessCpuState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3650641388239350382L;
    
    private final long processId;
    private final long user;
    private final long lastTime;
    private final double percentUsage;
    private final long startTime;
    private final long cpuTime;
    private final long kernelTime;

    public ProcessCpuState(long processId, long user, long lastTime, double percent, long startTime, long total, long sys)
    {
        this.processId = processId;
        this.user = user;
        this.lastTime = lastTime;
        this.percentUsage = percent;
        this.startTime = startTime;
        this.cpuTime = total;
        this.kernelTime = sys;
    }

    /**
     * @return the processId
     */
    public long getProcessId()
    {
        return processId;
    }

    /**
     * @return the user
     */
    public long getUser()
    {
        return user;
    }

    /**
     * @return the lastTime
     */
    public long getLastTime()
    {
        return lastTime;
    }

    /**
     * @return the percentUsage
     */
    public double getPercentUsage()
    {
        return percentUsage;
    }

    /**
     * @return the startTime
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @return the cpuTime
     */
    public long getCpuTime()
    {
        return cpuTime;
    }

    /**
     * @return the kernelTime
     */
    public long getKernelTime()
    {
        return kernelTime;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
