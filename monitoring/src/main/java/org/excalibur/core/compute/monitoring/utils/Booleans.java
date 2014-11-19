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
package org.excalibur.core.compute.monitoring.utils;

import java.math.BigInteger;

/**
 * Static utility methods pertaining to {@code boolean} primitives, that are not already found in either {@link Boolean}.
 */
public final class Booleans
{
    /**
     * Private constructor since this is a utility class.
     */
    private Booleans()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Convert a boolean wrapper value to a primitive value considering that a <code>null</code> is a <code>false</code> primitive value.
     * 
     * @param value
     *            The wrapper' value to be converted to a primitive value.
     * @return The wrapper value converted to a primitive boolean value.
     */
    public static boolean valueOf(Boolean value)
    {
        return value == null ? false : value;
    }

    /**
     * Converts a {@link String} value such as on, yes, 1 (one) to <code>true</code> and off, no and zero (0) to <code>false</code>.
     * 
     * @param value
     *            The {@link String} value to be converted to {@link Boolean}.
     * @return An boolean that represents the {@link String}.
     */
    public static boolean valueOf(String value)
    {
        switch (value)
        {
        case "on":
        case "yes":
        case "1":
        case "true":
        case "v":
        case "V":
            return true;
        case "off":
        case "no":
        case "0":
        case "false":
        case "f":
        case "F":
        default:
            try
            {
                return valueOf(new BigInteger(value.trim()));
            }
            catch (NumberFormatException exception)
            {
                return false;
            }
        }
    }

    /**
     * Converts a {@link Number} value to a {@link Boolean} value. The conversion follows the convention of the C language, where any number greater
     * than zero is <code>true</code> or false otherwise.
     * 
     * @param value
     *            The value to be converted to a {@link Boolean} value. It'll be <code>true</code> only if the given value is greater than zero.
     * @return The given value converted to a {@link Boolean} value.
     */
    public static boolean valueOf(Number value)
    {
        return value != null && value.intValue() > 0;
    }

    /**
     * Converts a given value to {@link Boolean}.
     * 
     * @param value
     *            The value to be converted to {@link Boolean}.
     * @param <E>
     *            The type of the given value
     * @return The value converted to <code>null</code>. If <code>null</code>, the return is always <code>false</code>.
     * @see #valueOf(Boolean)
     * @see #valueOf(Number)
     * @see #valueOf(String)
     */
    public static <E> boolean valueOf(E value)
    {
        if (value == null)
        {
            return false;
        }
        else
        {
            if (value instanceof Number)
            {
                return valueOf((Number) value);
            }
            else if (value instanceof Boolean)
            {
                return (Boolean) value;
            }
            else
            {
                return valueOf(value.toString());
            }
        }
    }
}
