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
package org.excalibur.core.util.concurrent;


import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SerialExecutor implements Executor
{
	private final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
	private final Executor executor;
	
	private Runnable active;
	
	public SerialExecutor(Executor executor)
	{
		this.executor = requireNonNull(executor, "executor is null");
	}

	@Override
	public synchronized void execute(final Runnable r) 
	{
		tasks.offer(() -> 
		{
			try
			{
				r.run();
			}
			finally
			{
				scheduleNext();
			}
		});
		
		if (active == null)
		{
			scheduleNext();
		}
	}
	
	protected synchronized void scheduleNext()
	{
		if ((active = tasks.poll()) != null)
		{
			executor.execute(active);
		}
	}
}
