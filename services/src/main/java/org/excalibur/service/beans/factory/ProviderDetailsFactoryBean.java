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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.domain.Endpoint;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.discovery.domain.ProviderDetails;
import org.springframework.beans.factory.FactoryBean;

public class ProviderDetailsFactoryBean implements FactoryBean<ProviderDetails>
{
    private Provider provider_;

    @Override
    public ProviderDetails getObject() throws Exception
    {
        String host = System.getProperty("org.excalibur.server.host");
        Integer port = SystemUtils2.getIntegerProperty("org.excalibur.server.port", 8080); 

        String regionName = System.getProperty("org.excalibur.provider.region.name");
        checkState(!isNullOrEmpty(regionName));

        String name = String.format("%s-%s", provider_.getName(), regionName);
        return new ProviderDetails().setEndpoint(Endpoint.valueOf(host, port)).setName(name);
    }

    @Override
    public Class<?> getObjectType()
    {
        return ProviderDetails.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setProvider(Provider provider)
    {
        this.provider_ = provider;
    }
}
