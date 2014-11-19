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
package org.excalibur.core.execution.logger.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.excalibur.core.execution.logger.ExecutionLogger;
import org.excalibur.core.execution.logger.LogEntry;

public class MemoryExecutionLogger implements ExecutionLogger
{
    private List<LogEntry>      logEntries_ = new CopyOnWriteArrayList<LogEntry>();
    private final ReadWriteLock lock_ = new ReentrantReadWriteLock();

    @Override
    public List<LogEntry> getLogs()
    {
        lock_.writeLock().lock();
        try
        {
            return Collections.unmodifiableList(logEntries_);
        }
        finally
        {
            lock_.writeLock().unlock();
        }
    }

    @Override
    public boolean addLogEntry(LogEntry log)
    {
        lock_.readLock().lock();
        try
        {
            if (log != null)
            {
                logEntries_.add(log);
            }
            return false;
        }
        finally
        {
            lock_.readLock().unlock();
        }
    }
}
