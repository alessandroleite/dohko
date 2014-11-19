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
package org.excalibur.service.deployment.server.context.handler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import javax.servlet.ServletContextEvent;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.service.manager.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

public abstract class AbstractApplicationInitializedHandler implements ApplicationInitializedHandler
{
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private ApplicationInitializedHandler next_;
    
    @Override
    public ApplicationInitializedHandler setNextHandler(ApplicationInitializedHandler handler)
    {
        this.next_ = handler;
        
        return this;
    }

    @Override
    public final void handlerApplicationInitialized(ServletContextEvent sce)
    {
        ApplicationContext context = (ApplicationContext) sce.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        
        String hostName = System.getProperty("org.excalibur.instance.hostname");
        checkState(!isNullOrEmpty(hostName));
        
        String regionName = System.getProperty("org.excalibur.provider.region.name");
        checkState(!isNullOrEmpty(regionName));
        
        String zoneName = System.getProperty("org.excalibur.provider.region.zone.name");
        checkState(!isNullOrEmpty(zoneName));
        
        String username = System.getProperty("org.excalibur.user.name");
        checkState(!isNullOrEmpty(username));
        
        String keyname = System.getProperty("org.excalibur.user.keyname");
        checkState(!isNullOrEmpty(keyname));
        
        Configuration configuration = new Configuration()
                     .setProvider((ProviderSupport) context.getBean(Provider.class))
                     .setRegion(context.getBean(RegionRepository.class).findByName(regionName))
                     .setZone(context.getBean(RegionRepository.class).findZoneByName(zoneName))
                     .setUser(context.getBean(UserRepository.class).findUserByUsername(username))
                     .setHostName(hostName);

        UserProviderCredentials credentials = context.getBean(UserRepository.class)
                .findLoginCredentialsOfUserForProvider
                (
                        configuration.getUser().getId(), 
                        configuration.getProvider().getName()
                );
        
        checkNotNull(credentials).setRegion(configuration.getRegion());

        credentials.setLoginCredentials(credentials.getLoginCredentials()
                .toBuilder()
                .credentialName(keyname)
                .build());
        
        configuration.setCredentials(credentials);
        
        handlerApplicationInitializedEvent(configuration, context, sce);
        
        if (this.next_ != null)
        {
            this.next_.handlerApplicationInitialized(sce);
        }
    }
    
    public abstract void handlerApplicationInitializedEvent(Configuration configuration, ApplicationContext applicationContext,
            ServletContextEvent sce);
    
}
