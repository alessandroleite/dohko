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
package org.excalibur.core.compute.monitoring.domain.io;

import java.io.Serializable;

public final class StorageInfo implements Serializable
{
    /**
     * Serial code version code <code>serialVersionUID</code>.
     */
    private static final long serialVersionUID = 8219928715433679802L;

    /**
     * The number of heads.
     */
    private final Integer heads;

    /**
     * The number of sectors.
     */
    private final Integer sectors;

    /**
     * The number of cylinders.
     */
    private final Integer cylinders;

    /**
     * Total number of sectors.
     */
    private final Integer totalOfSectors;

    /**
     * Create a {@link StorageInfo} instance.
     * 
     * @param numberOfHeads
     *            The number of heads.
     * @param numberOfSectors
     *            The number of sectors available.
     * @param numberOfCylinders
     *            The number of cylinders.
     * @param totalNumberOfSectors
     *            The sum sectors available.
     */
    public StorageInfo(Integer numberOfHeads, Integer numberOfSectors, Integer numberOfCylinders, Integer totalNumberOfSectors)
    {

        this.heads = numberOfHeads;
        this.sectors = numberOfSectors;
        this.cylinders = numberOfCylinders;
        this.totalOfSectors = totalNumberOfSectors;
    }

    /**
     * @return the heads
     */
    public Integer heads()
    {
        return heads;
    }

    /**
     * @return the sectors
     */
    public Integer sectors()
    {
        return sectors;
    }

    /**
     * @return the cylinders
     */
    public Integer cylinders()
    {
        return cylinders;
    }

    /**
     * @return the totalSectors
     */
    public Integer totalOfSectors()
    {
        return totalOfSectors;
    }
}
