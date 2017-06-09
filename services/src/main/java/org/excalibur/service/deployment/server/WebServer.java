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
package org.excalibur.service.deployment.server;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.service.deployment.server.context.ApplicationServletContextListener;
import org.excalibur.service.manager.NodeManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import static com.google.common.base.Preconditions.checkArgument;

public final class WebServer implements Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger(WebServer.class.getName());

    private final Server server;
    private final String contextPath;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public WebServer(String contextPath)
    {
        checkArgument(contextPath != null);

        int port = SystemUtils2.getIntegerProperty("org.excalibur.server.port", 8080);
        this.contextPath = contextPath;
        server = new Server(port);
    }

    /**
     * Starts the this server and its services.
     **/
    public void start() throws Exception
    {
        if (this.started.compareAndSet(false, true))
        {
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            context.addEventListener(new ContextLoaderListener());
            context.setInitParameter("contextConfigLocation", "classpath*:META-INF/applicationContext.xml");
            context.setContextPath(contextPath);

            ServletHolder sh = new ServletHolder(new org.glassfish.jersey.servlet.ServletContainer());
            sh.setInitParameter("javax.ws.rs.Application", ApplicationConfig.class.getName());
            sh.setInitOrder(1);
            context.addServlet(sh, "/*");

            context.addEventListener(new ApplicationServletContextListener());

            server.setHandler(context);
            server.start();
            
            NodeManagerFactory.getManagerReference().start();
            server.join();
        }

        LOG.debug("Server already started!");
    }

    @Override
    public void close() throws IOException
    {
        if (started.compareAndSet(true, false))
        {
            try
            {
                server.stop();
            }
            catch (Exception e)
            {
                LOG.error("Error on stopping the server. Error message: {}", e.getMessage());
            }
        }
    }

}
