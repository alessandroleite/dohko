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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.Path;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.discovery.domain.ServiceDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;

public final class ApplicationServer implements Closeable
{
    private static final Logger                          LOG = LoggerFactory.getLogger(ApplicationServer.class.getName());
    
    private final ServiceDiscovery<ServiceDetails>      serviceDiscovery;
    private final ServiceInstance<ServiceDetails>       thisInstance;
    private final Server                                 server;
    private final String                                 contextPath;
    private final AtomicBoolean                          started = new AtomicBoolean(false);
    private final List<ServiceInstance<ServiceDetails>> services = new ArrayList<ServiceInstance<ServiceDetails>>();
    
    public ApplicationServer(CuratorFramework client, String host, String contextPath, String basePath, String serviceName) throws Exception
    {
        Preconditions.checkState(!Strings.isNullOrEmpty(host));
        UriSpec uriSpec = new UriSpec(String.format("{scheme}://%s:{port}%s", host, this.contextPath = contextPath));
        
        int port = SystemUtils2.getIntegerProperty("org.excalibur.server.port", 8080);
        
        thisInstance = ServiceInstance.<ServiceDetails>builder()
                .name(serviceName)
                .payload(new ServiceDetails())
                .port(port)
                .uriSpec(uriSpec)
                .build();
        
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetails.class)
                .client(client)
                .basePath(basePath)
                .thisInstance(thisInstance)
                .build();
        
        server = new Server(port);
    }

    public ServiceInstance<ServiceDetails> getThisInstance()
    {
        return this.thisInstance;
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
            context.setInitParameter("contextConfigLocation", "classpath*:META-INF/context.xml");
            context.setContextPath(contextPath);

            ServletHolder sh = new ServletHolder(new org.glassfish.jersey.servlet.ServletContainer());
            sh.setInitParameter("javax.ws.rs.Application", ApplicationConfig.class.getName());
            sh.setInitOrder(1);
            context.addServlet(sh, "/*");

            server.setHandler(context);
            server.start();

            ApplicationContext applicationContext = (ApplicationContext) context.getServletContext().getAttribute(
                    WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

            Environment environment = applicationContext.getEnvironment();
            ApplicationConfig application = applicationContext.getBean(ApplicationConfig.class);

            for (Class<?> klass : application.getAvailableResources())
            {
                registerThisInstanceServices(klass, environment, application);
            }

            this.serviceDiscovery.start();
            server.join();
        }
    }
    
    @Override
    public void close() throws IOException
    {
        if (started.get())
        {
            try
            {
                this.unregisterThisInstanceServices();
                Closeables.close(serviceDiscovery, true);
                server.stop();
            }
            catch (Exception exception)
            {
                LOG.warn("Error in stopping the server. Cause {}", exception.getMessage());
            }
            started.set(false);
        }
    }
    
    protected void registerThisInstanceServices(final Class<?> serviceClass, final Environment environment, ApplicationConfig application) throws Exception
    {
        final String servicePath = serviceClass.getAnnotation(Path.class).value();
        
        ServiceInstance<ServiceDetails> instance = ServiceInstance.<ServiceDetails>builder()
                .name(thisInstance.getName() + servicePath)
                .payload(new ServiceDetails())
                .port(environment.getProperty("org.excalibur.server.port", Integer.class, 8080))
                .uriSpec(application.getUriSpec(servicePath.substring(1)))
                .build();
        
        services.add(instance);
        
        this.serviceDiscovery.registerService(instance);
    }
    
    protected void unregisterThisInstanceServices() throws Exception
    {
        for(ServiceInstance<ServiceDetails> instance: this.services)
        {
            this.serviceDiscovery.unregisterService(instance);
        }
    }
}
