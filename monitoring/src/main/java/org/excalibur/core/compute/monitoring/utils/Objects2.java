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

import net.vidageek.mirror.dsl.Mirror;

public final class Objects2
{
    /**
     * Private default constructor that it's never called.
     */
    private Objects2()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Invoke the clone method of a {@link Cloneable} type.
     * 
     * @param object
     *            The {@link Cloneable} instance to execute the method clone.
     * @param <T>
     *            The {@link Cloneable} type.
     * @return The return of the clone method.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Cloneable> T clone(T object)
    {
        if (object != null)
        {
            return (T) new Mirror().on(object).invoke().method("clone").withoutArgs();
        }
        return null;
    }
}
