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
package org.excalibur.core.compute.monitoring.domain.net;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import static java.util.Objects.requireNonNull;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class NetworkInterfaces implements Serializable, Iterable<NetworkInterface>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -3644436448837299947L;

    /**
     * The {@link Map} with the network interface. The key if the interface's name.
     */
    private Map<String, NetworkInterface> interfaces_ = Maps.newHashMap();

    /**
     * The reference to the primary interface. This reference is just to avoid the search in the map every time.
     */
    private volatile NetworkInterface primary;

    /**
     * Creates a {@link NetworkInterface}'s instance.
     */
    public NetworkInterfaces()
    {
        super();
    }

    /**
     * Creates a {@link NetworkInterface}'s instance with the given interfaces.
     * 
     * @param netInterfaces
     *            The network interfaces.
     */
    public NetworkInterfaces(NetworkInterface... netInterfaces)
    {
        if (netInterfaces != null)
        {
            for (NetworkInterface ni : netInterfaces)
            {
                this.add(ni);
            }
        }
    }

    /**
     * Factory method to creates a {@link NetworkInterface}.
     * 
     * @param interfaces
     *            The network interfaces.
     * @return An instance of this class.
     */
    public static NetworkInterfaces valueOf(NetworkInterface... interfaces)
    {

        return new NetworkInterfaces(interfaces);
    }

    /**
     * Add a not <code>null</code> {@link NetworkInterface}.
     * 
     * @param ni
     *            The {@link NetworkInterface} to be added.
     * @return Return <code>null</code> if there was no {@link NetworkInterface} equals to the given {@link NetworkInterface}.
     */
    public NetworkInterface add(NetworkInterface ni)
    {
        if (requireNonNull(ni).isPrimary())
        {
            this.primary = ni;
        }
        return this.interfaces_.put(ni.id(), ni);
    }

    /**
     * Removes a given {@link NetworkInterface}.
     * 
     * @param ni
     *            The {@link NetworkInterface} to be removed.
     * @return the {@link NetworkInterface} removed.
     */
    public NetworkInterface remove(NetworkInterface ni)
    {
        return this.remove(requireNonNull(ni).id());
    }

    /**
     * Removes a {@link NetworkInterface} that has a given id.
     * 
     * @param id
     *            The id of the {@link NetworkInterface} that must be removed.
     * @return the {@link NetworkInterface} removed.
     */
    public NetworkInterface remove(String id)
    {
        NetworkInterface removed = this.interfaces_.remove(id);

        if (primary != null && primary.equals(removed))
        {
            Map<String, NetworkInterface> primaries = Maps.filterValues(this.interfaces_, new Predicate<NetworkInterface>()
            {
                @Override
                public boolean apply(NetworkInterface input)
                {
                    return input.isPrimary() || input.isActive();
                }
            });
            this.primary = (primaries.isEmpty()) ? null : primaries.values().iterator().next();
        }
        return removed;
    }

    /**
     * Returns an unmodifiable {@link Collection} with the network interfaces of an machine.
     * 
     * @return An read-only {@link Collection} with the network interfaces of an machine
     */
    public Collection<NetworkInterface> interfaces()
    {
        return Collections.unmodifiableCollection(this.interfaces_.values());
    }

    /**
     * @return the primary
     */
    public NetworkInterface primary()
    {
        return primary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<NetworkInterface> iterator()
    {
        return this.interfaces().iterator();
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
