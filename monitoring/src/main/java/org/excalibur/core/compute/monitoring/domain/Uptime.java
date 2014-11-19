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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

import javax.measure.DecimalMeasure;
import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;

import com.google.common.base.Objects;

public class Uptime implements Serializable, Comparable<Uptime>, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 4594407701234156940L;
  
    private final Measure<Double, Duration> duration_;

    public Uptime(double durationInMinute)
    {
        checkArgument(durationInMinute >= 0);
        this.duration_ = DecimalMeasure.valueOf(durationInMinute, NonSI.MINUTE);
    }
    
    public static Uptime valueOf(double uptime)
    {
        return new Uptime(uptime);
    }
    
    public static Uptime valueOf(Measure<Double, Duration> duration)
    {
        return new Uptime(duration.to(NonSI.MINUTE).getValue());
    }

    /**
     * @return the value
     */
    public double getUptime()
    {
        return duration_.getValue();
    }

    public Measure<Double, Duration> minutes()
    {
        return this.duration_;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getUptime());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Uptime))
        {
            return false;
        }

        Uptime other = (Uptime) obj;
        return Objects.equal(this.getUptime(), other.getUptime());
    }

    @Override
    public String toString()
    {
        String retval = "";

        int days = (int) getUptime() / (60 * 60 * 24);
        int minutes, hours;

        if (days != 0)
        {
            retval += days + " " + ((days > 1) ? "days" : "day") + ", ";
        }

        minutes = (int) getUptime() / 60;
        hours = minutes / 60;
        hours %= 24;
        minutes %= 60;

        if (hours != 0)
        {
            retval += hours + ":" + minutes;
        }
        else
        {
            retval += minutes + " min";
        }

        return retval;
    }

    @Override
    public int compareTo(Uptime that)
    {
        return this.duration_.compareTo(that.duration_);
    }

    @Override
    public Uptime clone()
    {
        Object clone;

        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Uptime(this.getUptime());
        }

        return (Uptime) clone;
    }
    
}
