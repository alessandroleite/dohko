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
package org.excalibur.service.beans.factory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.domain.repository.ProviderRepository;
import org.springframework.beans.factory.FactoryBean;

public class ProviderFactoryBean implements FactoryBean<ProviderSupport>
{
    private ProviderRepository providerRepository_;

    private String name_;

    public ProviderFactoryBean(String name)
    {
        this.name_ = name;
        checkState(!isNullOrEmpty(this.name_));
    }

    public ProviderFactoryBean()
    {
        this(System.getProperty("org.excalibur.provider.name"));
    }

    @Override
    public ProviderSupport getObject() throws Exception
    {
        try
        {
            Provider provider = providerRepository_.findByExactlyProviderName(name_);
            return (ProviderSupport) checkNotNull(provider);
        }
        catch (Exception exception)
        {
            return new ProviderSupport().setName(name_);
        }
    }

    @Override
    public Class<?> getObjectType()
    {
        return Provider.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setProviderRepository(ProviderRepository providerRepository)
    {
        this.providerRepository_ = providerRepository;
    }
}
