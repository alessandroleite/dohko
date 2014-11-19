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
package org.excalibur.core.cloud.api.compute;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.domain.UserProviderCredentials;

public class ComputeServiceBuilder
{
    private ProviderSupport provider_;
    
    private UserProviderCredentials credentials_;
    
    public static ComputeServiceBuilder builder()
    {
        return new ComputeServiceBuilder();
    }
    
    public ComputeServiceBuilder provider(ProviderSupport provider)
    {
        this.provider_ = provider;
        return this;
    }
    
    public ComputeServiceBuilder credentials(UserProviderCredentials credentials)
    {
        this.credentials_ = credentials;
        
        if (credentials != null && credentials.getProvider() != null)
        {
            this.provider_ = credentials.getProvider();
        }
        
        return this;
    }
    
    public ComputeService build()
    {
        if (provider_ == null || credentials_ == null)
        {
            return null;
        }
        
        return (ComputeService) new Mirror().on(provider_.getServiceClass()).invoke().constructor().withArgs(credentials_);
    }
}
