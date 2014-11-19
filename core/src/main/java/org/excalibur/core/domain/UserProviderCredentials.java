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
package org.excalibur.core.domain;

import java.io.Serializable;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.domain.Region;

import com.google.common.base.Objects;

public class UserProviderCredentials implements Cloneable, Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization. 
     */
    private static final long serialVersionUID = 6785705481739428292L;
    
    private Integer id_;
    private Integer userId_;
    private ProviderSupport provider_;
    private Region region_;
    private String project_;
    private LoginCredentials loginCredentials_;

    /**
     * @return the id_
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id_
     *            the id_ to set
     */
    public UserProviderCredentials setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the userId_
     */
    public Integer getUserId()
    {
        return userId_;
    }

    /**
     * @param userId_
     *            the userId_ to set
     */
    public UserProviderCredentials setUserId(Integer userId)
    {
        this.userId_ = userId;
        return this;
    }

    /**
     * @return the providerId
     */
    public ProviderSupport getProvider()
    {
        return provider_;
    }

    /**
     * @param providerId
     *            the providerId to set
     */
    public UserProviderCredentials setProvider(ProviderSupport provider)
    {
        this.provider_ = provider;
        return this;
    }

    /**
     * @return the project_
     */
    public String getProject()
    {
        return project_;
    }

    /**
     * @param project_
     *            the project_ to set
     */
    public UserProviderCredentials setProject(String project)
    {
        this.project_ = project;
        return this;
    }

    /**
     * @return the loginCredentials_
     */
    public LoginCredentials getLoginCredentials()
    {
        return loginCredentials_;
    }

    /**
     * @param loginCredentials_
     *            the loginCredentials_ to set
     */
    public UserProviderCredentials setLoginCredentials(LoginCredentials loginCredentials)
    {
        this.loginCredentials_ = loginCredentials;
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
     *            the region to set
     */
    public UserProviderCredentials setRegion(Region region)
    {
        this.region_ = region;
        return this;
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getId(), this.getProvider(), this.getUserId());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof UserProviderCredentials))
        {
            return false;

        }

        UserProviderCredentials other = (UserProviderCredentials) obj;

        return (this.getId() != null && (this.getId().equals(other.getId()) || (Objects.equal(this.userId_, other.userId_) && 
                Objects.equal(this.provider_, other.getProvider()))));
    }
    
    @Override
    public UserProviderCredentials clone() 
    {
        UserProviderCredentials cloned;
        
        try
        {
            cloned = (UserProviderCredentials) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            cloned = new UserProviderCredentials();
        }
        
        cloned.setLoginCredentials(this.loginCredentials_.clone())
                .setProject(this.getProject())
                .setProvider(this.provider_.clone())
                .setRegion(this.getRegion().clone());
        
        return cloned;
    }

}
