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

import javax.measure.DecimalMeasure;
import javax.measure.Measure;
import javax.measure.unit.Unit;

import static javax.measure.unit.SI.GIGA;
import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.MEGA;

public final class Frequency implements Comparable<Frequency>, Cloneable, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3741482765054359810L;

    /**
     * This instance represents a <code>null</code> frequency. In that case, the frequency value is zero.
     */
    public static final Frequency NULL_FREQUENCY = newFrequencyInMhz(0L);

    /**
     * The {@link Frequency}'s value.
     */
    private final Measure<Long, javax.measure.quantity.Frequency> value;

    /**
     * Creates a {@link Frequency} instance with a given value in MHz.
     * 
     * @param valueInMhz
     *            The frequency's value in MHz.
     */
    public Frequency(long valueInMhz)
    {
        this(valueInMhz, MEGA(HERTZ));
    }

    /**
     * Factory method to create an instance with a given value.
     * 
     * @param frequencyValue
     *            The frequency's value.
     * @param valueUnit
     *            The unit of the value.
     */
    public Frequency(long frequencyValue, Unit<javax.measure.quantity.Frequency> valueUnit)
    {
        this.value = DecimalMeasure.valueOf(Long.valueOf(frequencyValue), valueUnit);
    }

    /**
     * Factory method that creates an instance with a given value in MHz.
     * 
     * @param valueInMhz
     *            The frequency's value in MHz.
     * @return An instance of {@link Frequency} with the given value.
     */
    public static Frequency newFrequencyInMhz(long valueInMhz)
    {
        return new Frequency(valueInMhz);
    }

    /**
     * Factory method that creates an instance with a given value in GHz.
     * 
     * @param valueInGhz
     *            The frequency's value in GHz.
     * @return An instance of {@link Frequency} with the given value.
     */
    public static Frequency newFrequencyInGhz(long valueInGhz)
    {
        return new Frequency(valueInGhz, GIGA(HERTZ));
    }

    /**
     * Factory method that creates an instance with a given value in Hertz.
     * 
     * @param valueInHertz
     *            The frequency's value in hertz.
     * @return An instance of {@link Frequency} with the given value.
     */
    public static Frequency newFrequencyInHertz(long valueInHertz)
    {
        return new Frequency(valueInHertz, HERTZ);
    }

    public Long value()
    {
        return this.value.longValue(MEGA(HERTZ));
    }

    /**
     * Returns the frequency value in GHz.
     * 
     * @return The frequency value in GHz.
     */
    public double inGhz()
    {
        return this.value.doubleValue(GIGA(HERTZ));
    }

    /**
     * Returns the frequency value in MHz.
     * 
     * @return The frequency value in MHz.
     */
    public double inMhz()
    {
        return this.value.doubleValue(MEGA(HERTZ));
    }

    @Override
    public int compareTo(Frequency other)
    {
        return this.value().compareTo(other.value());
    }

    @Override
    public String toString()
    {
        return this.value().toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        Frequency other = (Frequency) obj;
        if (value == null)
        {
            if (other.value != null)
            {
                return false;
            }
        }
        else if (!value.equals(other.value))
        {
            return false;
        }
        return true;
    }

    @Override
    public Frequency clone()
    {
        Frequency frequency;
        try
        {
            frequency = (Frequency) super.clone();
        }
        catch (CloneNotSupportedException exception)
        {
            frequency = Frequency.newFrequencyInMhz(this.value());
        }
        return frequency;
    }
}
