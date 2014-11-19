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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

public class EventInfoMap
{
    /**
     * The with the reference to method of the listener to be executed for a given event.
     */
    private final Map<Class<?>, Method> eventListenerMap_ = new WeakHashMap<Class<?>, Method>();
    
    
    /**
     * 
     * @param event
     *            The reference for the {@link WattsUpEvent}. Might not be <code>null</code>.
     * @param listener
     *            The reference to the {@link WattsUpListener} to find the method to be executed for the {@code event}. Might not be
     *            <code>null</code>.
     * @param <T>
     *            The event's data type.
     * @return The {@link Method} of {@code listener} that has just {@code event} as parameter. The method is configured to be accessible.
     */
    public <T, EventType> Method getEventMethodFor(final EventObject<T, EventType> event, final EventListener listener)
    {
        Method method = eventListenerMap_.get(listener.getClass());

        if (method == null)
        {
            for (Method meth : listener.getClass().getDeclaredMethods())
            {
                if (meth.getParameterTypes() != null && meth.getParameterTypes().length == 1 && 
                    meth.getParameterTypes()[0].equals(event.getClass()))
                {
                    meth.setAccessible(true);
                    eventListenerMap_.put(listener.getClass(), meth);
                    method = meth;
                    break;
                }
            }
        }
        return method;
    }
}
