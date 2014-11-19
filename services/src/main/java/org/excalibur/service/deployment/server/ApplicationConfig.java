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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.apache.curator.x.discovery.UriSpec;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig extends ResourceConfig
{
    @Autowired
    private Environment environment;
    
    private final List<Class<?>> RESOURCES;
    

    /**
     * Register JAX-RS application components.
     */
    public ApplicationConfig()
    {
        Reflections reflections = new Reflections("org.excalibur");
        RESOURCES = Collections.unmodifiableList(new ArrayList<Class<?>>(reflections.getTypesAnnotatedWith(Path.class)));
        
        register(RequestContextFilter.class);
        registerResources(RESOURCES);
        registerResources(reflections.getTypesAnnotatedWith(Provider.class));
        
        
    }

    public UriSpec getUriSpec(final String servicePath)
    {
        return new UriSpec(
                String.format("{scheme}://%s:{port}%s%s%s", 
                        environment.getProperty("org.excalibur.server.host", "localhost"),
                        environment.getProperty("org.excalibur.service.context.path", ""), 
                        environment.getProperty("org.excalibur.service.application.path", ""),
                        servicePath
                        )
                );
    }

    void registerResources(Collection<Class<?>> resources)
    {
        if (resources != null)
        {
            for (Class<?> klass : resources)
            {
                register(klass);
            }
        }
    }
    
    public List<Class<?>> getAvailableResources()
    {
        return this.RESOURCES;
    }
}
