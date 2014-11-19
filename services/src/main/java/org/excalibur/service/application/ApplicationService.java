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
package org.excalibur.service.application;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.discovery.domain.ResourceDetails;
import org.excalibur.discovery.service.DiscoveryService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService
{
    public static final String RESOURCE_URI = "/users/%s/applications/%s";
    
    public static final JAXBContextFactory<ApplicationDescriptor> APPLICATION_JAXB_FACTORY;
    
    static 
    {
        try
        {
            APPLICATION_JAXB_FACTORY = new JAXBContextFactory<ApplicationDescriptor>(ApplicationDescriptor.class);
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Resource
    private DiscoveryService discoveryService_;
    

    public void register(ApplicationDescriptor deployment) throws Exception
    {
        ResourceDetails resource = new ResourceDetails()
                .setName(String.format(RESOURCE_URI, deployment.getUser().getUsername(), deployment.getId()))
                .setPayload(APPLICATION_JAXB_FACTORY.marshal(deployment)).setType(ApplicationDescriptor.class);

        discoveryService_.registerResource(resource);
    }
}
