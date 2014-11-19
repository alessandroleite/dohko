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

public final class ProcessMemoryState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 3854393185798554122L;

    private final long pid;

    private final long resident;

    private final long pageFaults;

    private final long majorFaults;

    private final long share;

    private final long minorFaults;

    private final long size;

    public ProcessMemoryState(long pid, long resident, long pageFaults, long majorFaults, long share, long minorFaults, long size)
    {
        this.pid = pid;
        this.resident = resident;
        this.pageFaults = pageFaults;
        this.majorFaults = majorFaults;
        this.share = share;
        this.minorFaults = minorFaults;
        this.size = size;
    }

    /**
     * @return the pid
     */
    public long getPid()
    {
        return pid;
    }

    /**
     * @return the resident
     */
    public long getResident()
    {
        return resident;
    }

    /**
     * @return the pageFaults
     */
    public long getPageFaults()
    {
        return pageFaults;
    }

    /**
     * @return the majorFaults
     */
    public long getMajorFaults()
    {
        return majorFaults;
    }

    /**
     * @return the share
     */
    public long getShare()
    {
        return share;
    }

    /**
     * @return the minorFaults
     */
    public long getMinorFaults()
    {
        return minorFaults;
    }

    /**
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
