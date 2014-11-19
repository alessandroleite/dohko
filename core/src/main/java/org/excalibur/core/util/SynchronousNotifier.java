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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A default {@link Notifier} which performs the notifications synchronously.
 */
public class SynchronousNotifier implements Notifier
{
    private final List<Runnable> listeners_ = new CopyOnWriteArrayList<Runnable>();

    @Override
    public void add(Runnable listener)
    {
        if (listener != null)
        {
            this.listeners_.add(listener);
        }
    }

    @Override
    public void remove(Runnable listener)
    {
        this.listeners_.remove(listener);
    }

    @Override
    public void run()
    {
        for (Runnable listener : listeners_)
        {
            listener.run();
        }
    }
}
