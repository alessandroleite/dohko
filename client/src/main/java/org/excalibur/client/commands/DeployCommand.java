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
package org.excalibur.client.commands;

import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.excalibur.core.Command;
import org.excalibur.core.cloud.api.Cloud;
import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.Requirements;
import org.excalibur.discovery.ws.ext.YamlMapperProvider;
import org.glassfish.jersey.jackson.JacksonFeature;

import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;

import com.beust.jcommander.ParametersDelegate;
import com.google.common.base.Strings;

public class DeployCommand implements Command
{
    public static final String NAME = "deploy";

    @ParametersDelegate
    private final DeployCommandOptions options_ = new DeployCommandOptions();

    @Override
    public void execute(PrintStream output) throws IOException
    {
        Response response = target().request(APPLICATION_XML_TYPE).put(Entity.entity(getApplicationDescriptor(), APPLICATION_XML_TYPE));
        output.println(response.getStatusInfo());
    }

    private ApplicationDescriptor getApplicationDescriptor()
    {
        ApplicationDescriptor applicationDescriptor = options_.getApplicationDescriptor();
        Requirements requirements = options_.getRequirements();

        if (applicationDescriptor == null)
        {
            applicationDescriptor = new ApplicationDescriptor().setRequirements(requirements);
        }
        else
        {
            // Please, think about another way to write this code. I don't like it.
            if (applicationDescriptor.getRequirements() == null)
            {
                applicationDescriptor.setRequirements(requirements);
            }
            else
            {
                if (requirements.getMaximalCostPerHour() != null)
                {
                    applicationDescriptor.getRequirements().setMaximalCostPerHour(requirements.getMaximalCostPerHour());
                }
                
                if (requirements.getMemorySize() != null)
                {
                    applicationDescriptor.getRequirements().setMemorySize(requirements.getMemorySize());
                }
                
                if (requirements.getNumberOfCpuCores() != null)
                {
                    applicationDescriptor.getRequirements().setNumberOfCpuCores(requirements.getNumberOfCpuCores());
                }
                
                if (requirements.getNumberOfInstancesPerCloud() != null)
                {
                    applicationDescriptor.getRequirements().setNumberOfInstancesPerCloud(requirements.getNumberOfInstancesPerCloud());
                }
            }
            
            for (Application application: applicationDescriptor.getApplications())
            {
            	if (Strings.isNullOrEmpty(application.getId()))
            	{
            		application.setId(UUID.randomUUID().toString());
            	}
            }
        }
        
        if (options_.getClouds() != null)
        {
            for (int i = 0; i < options_.getClouds().length; i++)
            {
                applicationDescriptor.getClouds().add(new Cloud().setName(options_.getClouds()[i].getProvider()));
            }
        }

        return applicationDescriptor;
    }

    protected WebTarget target()
    {
        Client client = ClientBuilder.newClient().register(YamlMapperProvider.class).register(JacksonFeature.class);
        return client.target(String.format("http://%s:%s/application", options_.getHostAndPort().getHost(), options_.getHostAndPort().getPort()));
    }

    @Override
    public String getName()
    {
        return NAME;
    }
    
    public DeployCommandOptions getOptions()
    {
        return this.options_;
    }
}
