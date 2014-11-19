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

public final class ProcessTime implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3482462959894458820L;

    private final long pid;

    private final long user;

    private final long startTime;

    private final long total;

    private final long sys;

    public ProcessTime(long pid, long user, long startTime, long total, long sys)
    {
        this.pid = pid;
        this.user = user;
        this.startTime = startTime;
        this.total = total;
        this.sys = sys;
    }

    /**
     * @return the pid
     */
    public long getPid()
    {
        return pid;
    }

    /**
     * @return the user
     */
    public long getUser()
    {
        return user;
    }

    /**
     * @return the startTime
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @return the total
     */
    public long getTotal()
    {
        return total;
    }

    /**
     * @return the sys
     */
    public long getSys()
    {
        return sys;
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
