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
package org.excalibur.bio.tools.infernal.data;

import java.io.Serializable;

import com.google.common.base.Preconditions;

public final class Field<T extends Serializable> implements Serializable, Cloneable, Comparable<Field<T>>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -1819540663191789897L;

    /**
     * Field's name.
     */
    private final String name_;

    /**
     * Field's value.
     */
    private final T value_;

    /**
     * Creates a new {@link Field} with the given name and value.
     * 
     * @param name
     *            The field's name. Might not be <code>null</code>.
     * @param value
     *            The field's value.
     */
    public Field(String name, T value)
    {
        this.name_ = Preconditions.checkNotNull(name);
        this.value_ = value;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @return the value
     */
    public T getValue()
    {
        return value_;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name_ == null) ? 0 : name_.hashCode());
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

        Field<?> other = (Field<?>) obj;

        if (name_ == null)
        {
            if (other.name_ != null)
            {
                return false;
            }
        }
        else if (!name_.equals(other.name_))
        {
            return false;
        }
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Field<T> clone()
    {
        Object clone;

        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Field(this.name_, this.value_);
        }
        return (Field<T>) clone;
    }

    @Override
    public int compareTo(Field<T> that)
    {
        return this.name_.compareTo(that.name_);
    }

    @Override
    public String toString()
    {
        return String.format("name = %s value = %s", this.name_, this.value_);
    }
}
