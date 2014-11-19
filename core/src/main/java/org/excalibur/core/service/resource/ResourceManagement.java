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
package org.excalibur.core.service.resource;

import java.util.List;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.domain.User;

public interface ResourceManagement
{
    /**
     * Returns a non-<code>null</code>{@link List} with the available providers.
     * 
     * @return A non-null {@link List} with the available providers.
     */
    List<Provider> getProviders();

    /**
     * Returns all available resources of user in a given provider.
     * 
     * @param User owner The users who the resources belong to.
     * @param provider The provider to returns its resources.
     * @return A non-null {@link List} with the available resources.
     */
    List<VirtualMachine> getOnlineInstancesOfProvider(User owner, Provider provider);
    
    List<VirtualMachine> getInstancesOfProviderInState(User owner, Provider provider, InstanceStateType state);
    
    /**
     * 
     * @param provider
     * @return
     */
    List<InstanceType> getInstanceTypes(Provider provider);
}
