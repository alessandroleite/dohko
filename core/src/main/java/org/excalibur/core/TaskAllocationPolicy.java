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
package org.excalibur.core;


public interface TaskAllocationPolicy<R>
{
    /**
     * Allocates a {@link Task} for a given {@code resource}.
     * 
     * @param task The task to be allocated. Might not be <code>null</code>.
     * @param resource The resource to allocate the task. Might not be <code>null</code>.
     * @return <code>true</code> if the task was allocated for the given resource.
     */
   // boolean allocation(Task task, R resource);
}
