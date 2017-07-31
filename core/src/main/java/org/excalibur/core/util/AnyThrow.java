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
package org.excalibur.core.util;

/**
 * http://blog.ragozin.info/2011/10/java-how-to-throw-undeclared-checked.html.
 */
public final class AnyThrow
{
    private AnyThrow()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Wraps an exception with an {@link Exception} as {@link RuntimeException} without creating a new {@link RuntimeException}. This is useful to
     * avoid polluting the log with unnecessary stack traces.
     * 
     * @param exception The exception to be wrapped as an unchecked exception.
     */
    public static void throwUncheked(Throwable exception)
    {
        AnyThrow.<RuntimeException> throwAny(exception);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAny(Throwable e) throws E
    {
        throw (E) e;
    }
}
