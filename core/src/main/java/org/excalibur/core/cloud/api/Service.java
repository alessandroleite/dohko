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
package org.excalibur.core.cloud.api;

import java.util.List;

import org.excalibur.core.cloud.api.domain.Region;

public interface Service
{
    /**
     * Returns this {@link Service} id. 
     * @return The service id. It's never <code>null</code>.
     */
    Integer getId();
    
    /**
     * Returns the service name/id.
     * 
     * @return the service name/id. It's never <code>null</code>.
     */
    String getName();

    /**
     * Returns the service description.
     * 
     * @return The service description.
     */
    String getDescription();

    /**
     * Returns the regions where this service is available.
     * 
     * @return A read-only list with the regions where this service is available.
     */
    List<Region> listRegions();

}
