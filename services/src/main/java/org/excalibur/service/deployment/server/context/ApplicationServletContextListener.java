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
package org.excalibur.service.deployment.server.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.excalibur.service.deployment.server.context.handler.ContextInitializedChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import br.cic.unb.overlay.Overlay;
import br.cic.unb.overlay.OverlayException;

public class ApplicationServletContextListener implements ServletContextListener
{
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationServletContextListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        new ContextInitializedChain().process(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        ApplicationContext applicationContext = (ApplicationContext) sce.getServletContext().getAttribute(
                WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        
        try
        {
            applicationContext.getBean(Overlay.class).leave();
        }
        catch (BeansException | OverlayException e)
        {
            LOG.error("Error on leaving the overlay [{}]", e.getMessage(), e);
        }
    }
}
