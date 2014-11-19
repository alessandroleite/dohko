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

import static org.excalibur.core.cloud.api.compute.ComputeServiceBuilder.builder;
import static org.excalibur.core.io.utils.IOUtils2.closeQuietly;
import static org.excalibur.core.util.SystemUtils2.getBooleanProperty;

import java.util.List;

import javax.servlet.ServletContextEvent;

import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.VirtualMachineImageRepository;
import org.excalibur.service.manager.Configuration;
import org.springframework.context.ApplicationContext;

public class ImportVirtualMachineImagesHandler extends AbstractApplicationInitializedHandler
{
    @Override
    public void handlerApplicationInitializedEvent(Configuration configuration, ApplicationContext applicationContext, ServletContextEvent sce)
    {
        if (getBooleanProperty("org.excalibur.config.import.vmi", true))
        {
            VirtualMachineImageRepository vmiRepository = applicationContext.getBean(VirtualMachineImageRepository.class);
            List<Region> regions = applicationContext.getBean(RegionRepository.class).listRegionsOfProvider(configuration.getProvider().getId());

            String imagesList = System.getProperty(String.format("org.excalibur.%s.vmi.marketplace.ubuntu", configuration.getProvider().getName()));

            if (null != imagesList)
            {
                String[] imageIds = imagesList.split(",");

                for (Region region : regions)
                {
                    ComputeService compute = builder()
                            .credentials(configuration.getCredentials().setRegion(region))
                            .provider(configuration.getCredentials().getProvider())
                            .build();

                    for (String imageId : imageIds)
                    {
                        List<VirtualMachineImage> images = compute.listImages(imageId.trim());
                        
                        try
                        {
                            if (vmiRepository.findByExactlyName(imageId) == null)
                            {
                                vmiRepository.insertVirtualMachineImages(images);
                            }

                        }
                        catch (Exception ex)
                        {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                    
                    closeQuietly(compute);
                }
            }
        }
    }
}
