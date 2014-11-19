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
package org.excalibur.core.task.executor;

import java.io.Serializable;

import org.excalibur.core.execution.launcher.TaskExecutionException;
import org.excalibur.core.task.TaskResult;

public interface ExecutionHandler
{
    /**
     * The asynchronous execution completed.
     * 
     * @param result
     *            The {@link TaskResult} value of execution.
     */
    <T extends Serializable> void onComplete(TaskResult<T> result);

    /**
     * The asynchronous execution failed.
     * 
     * @param exception
     *            the <code>TaskExecutionException</code> containing the root cause.
     */
    void onFailed(TaskExecutionException exception);
}
