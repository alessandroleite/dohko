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
package org.excalibur.core.task;

/**
 * The state of a {@link TaskResult}.
 */
public enum TaskState
{
    
    /**
     * 
     */
    EXECUTING(100),
    /**
     * 
     */
    ERROR(500),

    /**
     * 
     */
    FAILED(404),

    /**
     * 
     */
    SUCCESS(202);

    private final Integer id;

    private TaskState(Integer id)
    {
        this.id = id;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    public static TaskState valueOf(Integer id)
    {
        for (TaskState state : values())
        {
            if (state.getId().equals(id))
            {
                return state;
            }
        }
        return null;
    }
}
