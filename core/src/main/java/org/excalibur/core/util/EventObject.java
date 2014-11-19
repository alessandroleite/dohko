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

public abstract class EventObject<T, EventType> extends java.util.EventObject
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 7629157253227982854L;

    private final T value;
    private final EventType type;

    public EventObject(Object source, T value, EventType type)
    {
        super(source);
        this.value = value;
        this.type = type;
    }

    /**
     * Calls an event processing method, passing this {@link EventObject}.
     * 
     * @param listener
     *            The listener to send this {@link EventObject}.
     */
    public abstract void processListener(EventListener listener);

    /**
     * Returns <code>true</code> if the given {@link EventListener} is supported by this event.
     * 
     * @param listener
     *            The {@link EventListener} instance to evaluate.
     * @return <code>true</code> if this {@link EventListener} is a type expected by this event.
     */
    public abstract boolean isAppropriateListener(EventListener listener);

    /**
     * @return the value
     */
    public T getValue()
    {
        return value;
    }

    /**
     * @return the type
     */
    public EventType getType()
    {
        return type;
    }
}
