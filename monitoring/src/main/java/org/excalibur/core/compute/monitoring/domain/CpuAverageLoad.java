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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * <p>
 * This class represents the average system load over a period of time.
 * 
 * <p>
 * One (1.0) in a single core cpu represents 100% utilization. Note that loads can exceed 1.0, if means that processes have to wait for the cpu. 4.0
 * on a quad core represents 100% utilization. Anything under a 4.0 load average for a quad-core is ok as the load is distributed over the 4 cores.
 */
public final class CpuAverageLoad implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -5274637554416912441L;

    /**
     * Average values. The first index is the one minute load, the second is the five minutes load,
     * and finally the fifteen minutes load. 
     */
    private final double[] average_;

    public CpuAverageLoad(double[] average)
    {
        this.average_ = checkNotNull(average);
        checkState(average.length == 3);
    }

    public double getOneMinute()
    {
        return this.average_[0];
    }

    public double getFiveMinutes()
    {
        return this.average_[1];
    }

    public double getFifteenMinutes()
    {
        return this.average_[2];
    }

    public Map<Integer, Double> toMap()
    {
        Map<Integer, Double> values = Maps.newHashMap();

        values.put(1, getOneMinute());
        values.put(5, getFifteenMinutes());
        values.put(15, getFifteenMinutes());

        return values;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this).add("one minute", getOneMinute()).add("Five minutes", getFiveMinutes())
                .add("Fifteen minutes", getFifteenMinutes()).toString();
    }
}
