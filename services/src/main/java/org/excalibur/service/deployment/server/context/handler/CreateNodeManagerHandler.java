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

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static org.excalibur.core.cloud.api.compute.ComputeServiceBuilder.builder;

import java.lang.reflect.Field;
import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.services.InstanceService;
import org.excalibur.service.manager.Configuration;
import org.excalibur.service.manager.NodeManager;
import org.excalibur.service.manager.NodeManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class CreateNodeManagerHandler extends AbstractApplicationInitializedHandler
{

    @Override
    public void handlerApplicationInitializedEvent(Configuration configuration, ApplicationContext context, ServletContextEvent sce)
    {
        
        VirtualMachine thisNode = context.getBean(InstanceService.class).getInstanceByName(configuration.getHostName());
        ComputeService service = builder().credentials(configuration.getCredentials()).provider(configuration.getCredentials().getProvider()).build();

        if (thisNode == null)
        {
            thisNode = service.getInstanceWithName(configuration.getHostName(), configuration.getZone().getName());
        }

        checkNotNull(thisNode, "Node %s not found on region/zone %s/%s of provider %s", 
                configuration.getHostName(), configuration.getZone().getRegion(), configuration.getZone().getName());

        context.getBean(InstanceService.class).insertOrUpdateInstances(Collections.singleton(thisNode));
        NodeManager manager = new NodeManager(configuration, thisNode, service);

        this.configure(manager, context);
        NodeManagerFactory.setManager(manager);
    }

    private void configure(NodeManager manager, ApplicationContext context)
    {
        Mirror mirror = new Mirror();

        for (Field field : manager.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(Autowired.class))
            {
                mirror.on(manager).set().field(field).withValue(context.getBean(field.getType()));
            }
            else if (field.isAnnotationPresent(Resource.class))
            {
                Resource resource = field.getAnnotation(Resource.class);
                if (!isNullOrEmpty(resource.name()))
                {
                    mirror.on(manager).set().field(field).withValue(context.getBean(resource.name()));
                }
                else
                {
                    mirror.on(manager).set().field(field).withValue(context.getBean(field.getType()));
                }
            }
        }
    }
}
