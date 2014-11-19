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
package org.excalibur.core.services;

import java.util.List;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypes;
import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.domain.GeographicRegion;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.domain.repository.GeographicRegionRepository;
import org.excalibur.core.domain.repository.InstanceTypeRepository;
import org.excalibur.core.domain.repository.ProviderRepository;
import org.excalibur.core.domain.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;

@Service
public class ProviderService
{
        
    @Autowired
    private RegionRepository regionRepository_;

    @Autowired
    private ProviderRepository providerRepository_;

    @Autowired
    private InstanceTypeRepository instanceTypeRepository_;
    
    @Autowired
    private GeographicRegionRepository geographicRegionRepository_;
    
    public Provider get(Integer id)
    {
        checkArgument(id != null);
        return this.providerRepository_.findProviderById(id);
    }
    
    public Provider get(String name)
    {
        checkArgument(!isNullOrEmpty(name));
        
        return this.providerRepository_.findByExactlyProviderName(name);
    }
    
    public List<Provider> providers()
    {
        return checkNotNull(this.providerRepository_.getAllProviders());
    }
    
    public List<GeographicRegion> allRegions()
    {
        return this.geographicRegionRepository_.listAll();
    }
    
    public List<InstanceType> getInstanceAllInstanceTypesOfProvider(Provider provider)
    {
        List<InstanceType> types = newArrayList();
        
        for (Region region: this.getRegions(provider.getId()))
        {
            types.addAll(this.getInstanceTypes(provider, region));
        }
        
        return types;
    }

    /**
     * Returns all instance types of a {@link Provider} in a given {@link Region}.
     * 
     * @param provider The provider to get its instance types. Might not be <code>null</code>.
     * @param region The {@link Region} where the instance types are offered. Might not be <code>null</code>.
     * @return A non-null {@link List} with the available instance types.
     */
    public List<InstanceType> getInstanceTypes(Provider provider, Region region)
    {
        checkArgument(provider != null);
        checkArgument(region != null);
        
        return this.getInstanceTypes(provider.getId(), region.getId());
    }
    
    public List<InstanceType> getInstanceTypes(Integer providerId, Integer regionId)
    {
        checkArgument(providerId != null);
        checkArgument(regionId != null);
        
        return this.instanceTypeRepository_.getProviderInstanceTypesOnRegion(providerId, regionId);
    }
    
    public List<InstanceType> getInstanceTypesAvailableOnRegion(Integer providerId, GeographicRegion region)
    {
        checkArgument(providerId != null);
        checkArgument(region != null && region.getId() != null);
        
        return this.instanceTypeRepository_.getProviderInstanceTypesOnGeographicRegion(providerId, region.getId());
    }
    
    public List<InstanceType> getInstanceTypesAvailableOnRegion(GeographicRegion region)
    {
        checkArgument(region != null && region.getId() != null);
        
        List<InstanceType> types = newArrayList();
        
        for (Provider provider: this.providers())
        {
            types.addAll(this.getInstanceTypesAvailableOnRegion(provider.getId(), region));
        }
        return unmodifiableList(types);
    }
    
    /**
     * Returns the instance type of all regions and of all providers.
     *  
     * @return
     */
    public List<InstanceType> getAllInstanceTypesOfAllRegions()
    {
        List<InstanceType> types = newArrayList();
        
        for (GeographicRegion region: geographicRegionRepository_.listAll())
        {
            types.addAll(this.instanceTypeRepository_.listAllInstanceTypesOfGeographicRegion(region.getId()));
        }
        
        return unmodifiableList(types);
    }
    
    public List<Region> getRegions(Provider provider)
    {
        checkArgument(provider != null);
        return this.getRegions(provider.getId());
    }
    
    public List<Region> getRegions(Integer providerId)
    {
        checkArgument(providerId != null);
        return this.regionRepository_.getRegionsOfProvider(providerId);
    }

    public InstanceTypes getInstanceTypes(Provider provider, GeographicRegion region)
    {
        InstanceTypes types;
        
        if (provider == null && region == null)
        {
            types = new InstanceTypes(this.getAllInstanceTypesOfAllRegions());
        }
        else if (provider == null && region != null)
        {
            types = new InstanceTypes(this.getInstanceTypesAvailableOnRegion(region));
        }
        else if (provider != null && region == null)
        {
            types = new InstanceTypes(this.getInstanceAllInstanceTypesOfProvider(provider));
        }
        else
        {
            types = new InstanceTypes(this.getInstanceTypesAvailableOnRegion(provider.getId(), region));
        }
        
        return types;
    }
}
