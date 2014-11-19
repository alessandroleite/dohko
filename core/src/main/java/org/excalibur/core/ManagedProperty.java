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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "managed-property")
public class ManagedProperty implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 8993978567998458259L;

    /**
     * The name of the property. Might not be <code>null</code> or empty.
     */
    @XmlTransient
    private final String name;

    @XmlTransient
    private final Map<String, Comparable<?>> values;

    public ManagedProperty()
    {
        this(ManagedProperty.class.getName());
    }

    public ManagedProperty(final String name)
    {
        checkState(!isNullOrEmpty(name));
        this.name = name;
        this.values = new HashMap<String, Comparable<?>>();
    }

    private ManagedProperty(ManagedProperty that)
    {
        this.name = that.name;
        this.values = new HashMap<String, Comparable<?>>(that.values);
    }

    /**
     * Returns the property name.
     * 
     * @return the property name. It's never <code>null</code>.
     */
    public final String getName()
    {
        return name;
    }

    /**
     * Updates the value of a property name and returns its previous value.
     * 
     * @param key
     *            The property name. Might not be <code>null</code> of empty.
     * @param newValue
     *            The new value.
     * @return The previous value of the given property. May be <code>null</code> if the property was not defined or its value was <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    public final <T> Comparable<T> updateValue(String key, Comparable<T> newValue)
    {
        checkState(!isNullOrEmpty(key));
        return (Comparable<T>) this.values.put(key, newValue);
    }

    /**
     * Returns the current value of a property.
     * 
     * @param name
     *            The property name.
     * @param <T>
     *            The return type.
     * @return the current property value. May be <code>null</code>.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Comparable<?>> T getValue(final String name)
    {
        return (T) this.values.get(name);
    }

    /**
     * Returns the current value of a property.
     * 
     * @param name
     *            Property name. It should not be <code>null</code>.
     * @param defaultValue
     *            The value that must be return when the property value is <code>null</code>.
     * @param <T>
     *            The return type.
     * @return the current property value if it is not <code>null</code> or {@code defaultValue}.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends Comparable<?>> T getValue(final String name, T defaultValue)
    {
        T value = (T) this.getValue(name);
        return value == null ? defaultValue : value;
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hashCode(this.getName());

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ManagedProperty))
        {
            return false;
        }

        ManagedProperty other = (ManagedProperty) obj;

        if (!equal(this.getName(), other.getName()))
        {
            return false;
        }

        for (String key : values.keySet())
        {
            if (!other.values.containsKey(key))
            {
                return false;
            }

            if (!equal(this.values.get(key), other.values.get(key)))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString()
    {
        return this.toString("");
    }

    public String toString(String... ignores)
    {
        if (ignores != null)
        {
            Arrays.sort(ignores);
        }

        StringBuffer buffer = new StringBuffer(getName()).append("[");

        for (String key : this.values.keySet())
        {
            if (Arrays.binarySearch(ignores, key) < 0)
            {
                Object value = this.values.get(key);
                buffer.append(key).append("=").append(value != null ? value.toString() : "").append(",");
            }
        }

        buffer.replace(buffer.length() - 1, buffer.length(), "]");
        return buffer.toString();
    }

    @Override
    protected ManagedProperty clone()
    {
        ManagedProperty clone = null;
        try
        {
            clone = (ManagedProperty) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new ManagedProperty(this);
        }

        return clone;
    }
}
