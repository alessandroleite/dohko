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
import java.math.BigInteger;


public class Storage implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6844864477297248767L;

    /**
     * The storage's id.
     */
    private final String id;

    /**
     * The storage's name.
     */
    private final String name;

    /**
     * The storage's size.
     */
    private final BigInteger size;

    /**
     * The storage's info.
     */
    private final StorageInfo info;

    /**
     * 
     * @param storageId The storage's id. Might not be <code>null</code>.
     * @param storageName The storage's name. Might not be <code>null</code> or empty.
     * @param storageSize The storage's size. Might not be <code>null</code> or zero.
     * @param storageInfo The storage's info. Might not be <code>null</code>.
     */
    public Storage(String storageId, String storageName, BigInteger storageSize, StorageInfo storageInfo)
    {
        super();
        this.id = storageId;
        this.name = storageName;
        this.size = storageSize;
        this.info = storageInfo;
    }

    /**
     * @return the id
     */
    public String id()
    {
        return id;
    }

    /**
     * @return the name
     */
    public String name()
    {
        return name;
    }

    /**
     * @return the size
     */
    public BigInteger size()
    {
        return size;
    }

    /**
     * @return the info
     */
    public StorageInfo info()
    {
        return info;
    }
}
