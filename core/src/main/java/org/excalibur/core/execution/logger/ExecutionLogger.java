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
package org.excalibur.core.execution.logger;

import java.util.List;

public interface ExecutionLogger
{
    /**
     * Returns an unmodified {@link List} with the available logs.
     * 
     * @return A read-only view of the execution logs.
     */
    List<LogEntry> getLogs();

    /**
     * Adds a new {@link LogEntry} and returns <code>true</code> if succeed.
     * 
     * @param log
     *            The entry to add. Null value is ignored.
     * @return <code>true</code> if the log was added.
     */
    boolean addLogEntry(LogEntry log);
}
