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
package org.excalibur.core;

import java.io.Serializable;
import java.util.UUID;

public final class ID implements Comparable<ID>, Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = -3059182935823256421L;

    /**
     * 
     */
    private final UUID value_;

    /**
     * The value of the hash code of this class, since this class is immutable.
     */
    private final int hashCode_;

    /**
     * Creates a new {@link ID}
     * 
     * @param value
     *            The bytes that represents this ID. Might not be <code>null</code>.
     */
    public ID(UUID value)
    {
        this.value_ = value;
        this.hashCode_ = value.hashCode();
    }

    /**
     * Creates a new ID with the same state of other ID.
     * 
     * @param other
     *            The ID to be copied. Might not be <code>null</code>.
     */
    public ID(ID other)
    {
        this(other.value_);
    }

    /**
     * Returns the length of this ID measured in bits. The length is determined by the length of the length of the array.
     * 
     * @return The length of this ID measured in bits.
     */
    public int length()
    {
        return 0;
    }

    @Override
    public int compareTo(ID other)
    {
        return this.value_.compareTo(other.value_);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof ID))
        {
            return false;
        }

        return this.compareTo((ID) obj) == 0;
    }

    @Override
    public int hashCode()
    {
        return this.hashCode_;
    }

    @Override
    public ID clone()
    {
        Object cloned;
        try
        {
            cloned = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new ID(this.value_);
        }

        return (ID) cloned;
    }
}
