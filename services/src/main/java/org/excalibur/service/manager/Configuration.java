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
package org.excalibur.service.manager;

import static com.google.common.base.Preconditions.checkNotNull;

import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserProviderCredentials;

public class Configuration implements Cloneable
{
    private ProviderSupport provider_;
    
    private User user_;
    
    private Region region_;
    
    private Zone zone_;
    
    private UserProviderCredentials credentials_;
    
    private String hostName;

    /**
     * @return the provider
     */
    public ProviderSupport getProvider()
    {
        return provider_;
    }

    /**
     * @param provider the provider to set
     */
    public Configuration setProvider(ProviderSupport provider)
    {
        this.provider_ = checkNotNull(provider);
        return this;
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user_;
    }

    /**
     * @param user the user to set
     */
    public Configuration setUser(User user)
    {
        this.user_ = checkNotNull(user);
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
     * @param region the region to set
     */
    public Configuration setRegion(Region region)
    {
        this.region_ = checkNotNull(region);
        return this;
    }

    /**
     * @return the zone
     */
    public Zone getZone()
    {
        return zone_;
    }

    /**
     * @param zone the zone to set
     */
    public Configuration setZone(Zone zone)
    {
        this.zone_ = checkNotNull(zone);
        return this;
    }

    /**
     * @return the credentials
     */
    public UserProviderCredentials getCredentials()
    {
        return credentials_;
    }

    /**
     * @param credentials the credentials to set
     */
    public Configuration setCredentials(UserProviderCredentials credentials)
    {
        this.credentials_ = checkNotNull(credentials);
        return this;
    }
    
    /**
     * @return the hostName
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public Configuration setHostName(String hostName)
    {
        this.hostName = hostName;
        return this;
    }

    @Override
    public Configuration clone() 
    {
        Configuration configuration;
        
        try
        {
            configuration = (Configuration) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            configuration = new Configuration();
        }
        
        configuration.setCredentials(this.getCredentials().clone())
                .setProvider(this.getProvider().clone())
                .setRegion(this.getRegion().clone())
                .setUser(this.getUser().clone())
                .setZone(this.getZone().clone());
        
        return configuration;
    }
}
