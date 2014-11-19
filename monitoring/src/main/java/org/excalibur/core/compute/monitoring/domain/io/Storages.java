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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;
import static java.util.Objects.requireNonNull;

public class Storages implements Iterable<Storage>, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3591513324010018187L;
    
    /**
     * The {@link Map} with the storages. The keys must be not null.
     */
    private final Map<String, Storage> storages_ = Maps.newHashMap();

    @Override
    public Iterator<Storage> iterator()
    {
        return values().iterator();
    }

    /**
     * Returns the storages in a unmodifiable {@link Collection}.
     * 
     * @return The storages in a unmodifiable {@link Collection}.
     */
    public Collection<Storage> values()
    {
        return Collections.unmodifiableCollection(storages_.values());
    }

    /**
     * @param storage
     *            The disk to be added (attached).
     * @return the previous disk state.
     */
    public Storage add(Storage storage)
    {
        if (storage != null)
        {
            return this.storages_.put(requireNonNull(storage.id()), storage);
        }
        return storage;
    }

    public Storages copyOf(Storages storages)
    {
        if (storages != null)
        {
            for (Storage storage : storages)
            {
                this.add(storage);
            }
        }
        return this;
    }

    /**
     * Returns a storage that has a given id.
     * 
     * @param id
     *            The id of the {@link Storage} to be returned.
     * @return The storage that has a given id.
     */
    public Storage get(String id)
    {
        return this.storages_.get(id);
    }

    /**
	 * 
	 */
    public Storage remove(Storage storage)
    {
        if (storage != null)
        {
            return this.storages_.remove(storage.id());
        }

        return storage;
    }
}
