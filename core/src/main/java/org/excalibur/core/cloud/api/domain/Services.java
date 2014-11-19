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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.excalibur.core.cloud.api.Service;

import com.google.common.base.Preconditions;

public final class Services implements Iterable<Service>
{
    private final Map<String, Service> services_ = new ConcurrentHashMap<String, Service>();

    @Override
    public Iterator<Service> iterator()
    {
        return values().iterator();
    }

    /**
     * Returns the service that has a given name or <code>null</code> if it does not exist.
     * 
     * @param name
     *            The name of the service to return.
     * @return The service with the given name or <code>null</code> if it does not exist.
     */
    public Service get(String name)
    {
        return this.services_.get(name);
    }

    /**
     * Adds a not-null service to this {@link Map} replacing the previously version.
     * 
     * @param service
     *            The service to add. Only not null service is added. The service's name might not be <code>null</code>.
     * @return The previous service instance or <code>null</code> if there was no service if the given name.
     */
    public Service put(Service service)
    {
        Service previous = null;

        if (service != null)
        {
            previous = this.services_.put(Preconditions.checkNotNull(service.getName()), service);
        }

        return previous;
    }

    public Service remove(String name)
    {
        return this.services_.remove(name);
    }

    /**
     * Removes a given service and returns the removed instance. The return is <code>null</code> only with there was no service with the given name.
     * 
     * @param service
     *            The service to remove.
     * @return The service removed or <code>null</code> if there was no service with the name.
     */
    public Service remove(Service service)
    {
        return service == null ? service : this.remove(service.getName());
    }

    /**
     * Returns the number of services in this {@link Map}. If it's greater than {@link Integer#MAX_VALUE} returns {@link Integer#MAX_VALUE}.
     * 
     * @return The number of services in this {@link Map}.
     */
    public int size()
    {
        return this.services_.size();
    }

    /**
     * A read-only view of the services available in this {@link Map}.
     * 
     * @return A read-only {@link Collection} with the services available in this {@link Map}.
     */
    public Collection<Service> values()
    {
        return Collections.unmodifiableCollection(this.services_.values());
    }

    /**
     * Returns a service of the given type.
     * @param serviceType The type of the {@link Service} to return. Might not be <code>null</code>.
     * @return The {@link Service} of the given type or <code>null</code> if it was not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T getServiceType(Class<T> serviceType)
    {
        Preconditions.checkState(serviceType != null);
        
        Service service = null;
        Iterator<Service> it = services_.values().iterator();
        
        while (it.hasNext() && ! serviceType.isAssignableFrom((service = it.next()).getClass()))
        {
            service = null;
        }
        
        return (T) service;
    }
}
