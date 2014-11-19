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


public final class ServiceEndpoint extends Endpoint
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 3592633009102816669L;

    /**
     * The region of this {@link Endpoint}.
     */
    private Region region_;

    /**
     * Default constructor.
     */
    public ServiceEndpoint()
    {
    }

    /**
     * Creates a new {@link ServiceEndpoint} with the given address and port.
     * 
     * @param address
     *            The endpoint's address. Might not be <code>null</code>.
     * @param port
     *            The endpoint's port. Might not be <code>null</code>.
     */
    public ServiceEndpoint(String address, Integer port)
    {
        super(address, port);
    }

    /**
     * Creates a new {@link ServiceEndpoint} with the given address and port.
     * 
     * @param address
     *            The endpoint's address. Might not be <code>null</code>.
     * @param port
     *            The endpoint's port. Might not be <code>null</code>.
     * @param region
     *            The endpoint's region. <code>null</code> means that the endpoint is the same for all regions.
     */
    public ServiceEndpoint(String address, Integer port, Region region)
    {
        this(address, port);
        this.region_ = region;
    }

    /**
     * Assign a region to this endpoint and returns the reference updated.
     * 
     * @param region
     * @return This reference with the region updated.
     */
    public ServiceEndpoint withRegion(Region region)
    {
        this.setRegion(region);
        return this;
    }

    /**
     * @return the region
     */
    public Region getRegion()
    {
        return region_;
    }

    /**
     * @param region
     *            the region_ to set
     */
    public void setRegion(Region region)
    {
        this.region_ = region;
    }

}
