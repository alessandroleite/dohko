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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.base.Preconditions;

/**
 * A {@link Notifier} which performs the notifications asynchronously. It can either use a given {@link Executor} or create new.
 */
public class AsynchronousNotifier implements Notifier
{
    private final Executor executor_;
    private final Notifier notifier_;

    public AsynchronousNotifier()
    {
        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), new SynchronousNotifier());
    }

    public AsynchronousNotifier(Executor executor, Notifier delegate)
    {
        this.executor_ = Preconditions.checkNotNull(executor);
        this.notifier_ = Preconditions.checkNotNull(delegate);
    }

    @Override
    public void run()
    {
        this.executor_.execute(notifier_);
    }

    @Override
    public void add(Runnable listener)
    {
        this.notifier_.add(listener);
    }

    @Override
    public void remove(Runnable listener)
    {
        this.notifier_.remove(listener);
    }
}
