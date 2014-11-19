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
package org.excalibur.core.events;

import org.excalibur.core.events.handlers.DeadEventLoggingHandler;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;

public class EventBusFactory
{
    public static AsyncEventBus createAsyncEventBus(ListeningExecutorService executor, DeadEventLoggingHandler deadEventLoggingHandler)
    {
        return createAsyncEventBus("default-async-event-bus", executor, deadEventLoggingHandler);
    }

    public static AsyncEventBus createAsyncEventBus(String name, ListeningExecutorService executor, DeadEventLoggingHandler deadEventLoggingHandler)
    {
        AsyncEventBus asyncEventBus = new AsyncEventBus(name, executor);
        asyncEventBus.register(deadEventLoggingHandler);
        return asyncEventBus;
    }

    public static EventBus createSyncEventBus(DeadEventLoggingHandler deadEventLoggingHandler)
    {
        return createSyncEventBus("default-event-bus", deadEventLoggingHandler);
    }

    public static EventBus createSyncEventBus(String name, DeadEventLoggingHandler deadEventLoggingHandler)
    {
        EventBus eventBus = new EventBus(name);
        eventBus.register(deadEventLoggingHandler);
        return eventBus;
    }
}
