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
package org.excalibur.core.cloud.api.domain;

public enum RegionStatus
{
    DOWN(0), UP(1);

    private final Integer id;

    private RegionStatus(Integer id)
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

    public static RegionStatus valueOf(Integer statusId)
    {
        for (RegionStatus value : values())
        {
            if (value.getId().equals(statusId))
            {
                return value;
            }
        }
        throw new IllegalStateException("Invalid status id:" + statusId);
    }
}
