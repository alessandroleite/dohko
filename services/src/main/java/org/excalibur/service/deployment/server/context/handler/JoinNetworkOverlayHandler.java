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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.excalibur.core.util.SystemUtils2.*;

import javax.servlet.ServletContextEvent;

import org.excalibur.service.manager.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import br.cic.unb.chord.data.URL;
import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;

public class JoinNetworkOverlayHandler extends AbstractApplicationInitializedHandler
{

    @Override
    public void handlerApplicationInitializedEvent(Configuration configuration, ApplicationContext applicationContext, ServletContextEvent sce)
    {
        boolean isBootstrap = getBooleanProperty("org.excalibur.overlay.is.bootstrap", false);
        
        if (!isBootstrap)
        {
            String bootstrapAddress = getProperty("org.excalibur.overlay.bootstrap.address");
            Integer bootstrapPort = getIntegerProperty("org.excalibur.overlay.bootstrap.port");
            
            checkState(!isNullOrEmpty(bootstrapAddress));
            checkState(bootstrapPort != null);

            URL localURL = URL.valueOf(getProperty("org.excalibur.server.host"), getIntegerProperty("org.excalibur.overlay.port"));
            URL bootstrapURL = URL.valueOf(bootstrapAddress, bootstrapPort);

            checkState(!localURL.equals(bootstrapURL));
            
            String discoveryAddress = getProperty("org.excalibur.discovery.address");
            
            if (isNullOrEmpty(discoveryAddress))
            {
                System.setProperty("org.excalibur.discovery.address", bootstrapAddress.trim());   
            }
            
            try
            {
                applicationContext.getBean(Overlay.class).join(localURL, bootstrapURL);
            }
            catch (BeansException | OverlayException e)
            {
                LOG.error("Error on joining the overlay network");
                System.exit(1);
            }
        }
    }
}
