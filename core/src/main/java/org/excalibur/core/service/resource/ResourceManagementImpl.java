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

import java.util.Collections;
import java.util.List;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.InstanceRepository;
import org.excalibur.core.domain.repository.InstanceTypeRepository;
import org.excalibur.core.domain.repository.ProviderRepository;

public class ResourceManagementImpl implements ResourceManagement
{
    private ProviderRepository providerRepository;
    private InstanceRepository instanceRepository;
    private InstanceTypeRepository instanceTypeRepository;

    @Override
    public List<Provider> getProviders()
    {
        List<Provider> providers = providerRepository.getAllProviders();
        return Collections.unmodifiableList(providers);
    }

    @Override
    public List<VirtualMachine> getOnlineInstancesOfProvider(User owner, Provider provider)
    {
        return getInstancesOfProviderInState(owner, provider, InstanceStateType.RUNNING);
    }
    
    @Override
    public List<VirtualMachine> getInstancesOfProviderInState(User owner, Provider provider, InstanceStateType state)
    {
        return instanceRepository.getInstancesInState(owner.getId(), provider.getId(), state);
    }

    @Override
    public List<InstanceType> getInstanceTypes(Provider provider)
    {
        return instanceTypeRepository.getInstanceTypesOfProvider(provider.getId());
    }
}
