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
package org.excalibur.core.executor.task;

import java.io.Serializable;
import java.util.List;

import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.util.concurrent.FutureListener;
import org.excalibur.core.util.concurrent.ListenableFuture;

import com.google.common.util.concurrent.FutureCallback;

/**
 * Interface to shield the caller from the various platform-dependent implementations.
 */
public interface TaskExecutionService
{
    <T extends Serializable,V> ListenableFuture<V> schedule(TaskType<T> task, TaskContext context);
    
    <T extends Serializable,V> ListenableFuture<V> schedule(TaskType<T> task, TaskContext context, FutureListener<V> listener);

    void invokeAllAndWait(List<TaskContext> tasks);

    List<com.google.common.util.concurrent.ListenableFuture<TaskResult<Serializable>>> invokeAll(List<TaskContext> tasks);

    List<com.google.common.util.concurrent.ListenableFuture<TaskResult<Serializable>>> invokeAll(List<TaskContext> tasks,
            FutureCallback<TaskResult<Serializable>> callback);
    
    List<com.google.common.util.concurrent.ListenableFuture<TaskResult<Serializable>>> invokeAll(List<TaskContext> tasks,
            FutureCallback<TaskResult<Serializable>>[] callbacks);

    void invokeAllAndWait(List<TaskContext> contexts, FutureCallback<TaskResult<Serializable>> callback);

}
