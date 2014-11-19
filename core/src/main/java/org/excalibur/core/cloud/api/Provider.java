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

import org.excalibur.core.cloud.api.domain.Services;

public interface Provider
{
    /**
     * Returns the provider ID. It's never <code>null</code>. 
     * @return The provider ID.
     */
    Integer getId();
    
    /**
     * Returns the cloud provider's name.
     * 
     * @return The name of the cloud provider. It's never <code>null</code>.
     */
    String getName();

    /**
     * Returns a description of the cloud provider.
     * 
     * @return A description of the cloud provider.
     */
    String getDescription();
    
    /**
     * Returns the limit of resources allowed per resource type. 
     * @return
     */
    Integer getLimitOfResourcesPerType();

    /**
     * Returns the services available on this provider.
     * 
     * @return The services available on this provider. It's always not <code>null</code>.
     */
    Services getServices();

}
