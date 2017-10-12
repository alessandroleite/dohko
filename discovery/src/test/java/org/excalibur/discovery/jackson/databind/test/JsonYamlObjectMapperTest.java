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
package org.excalibur.discovery.jackson.databind.test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.excalibur.core.cloud.api.*;
import org.excalibur.core.cloud.api.domain.*;
import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.*;
import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.core.util.YesNoEnum;
import org.excalibur.jackson.databind.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class JsonYamlObjectMapperTest
{
    
    private static final Logger LOG = LoggerFactory.getLogger(JsonYamlObjectMapperTest.class.getName());
    
    private final JsonYamlObjectMapper mapper_ = new JsonYamlObjectMapper();
    
    final String dir = "/Users/alessandro/projetos/unb/biocloud/database/sequences/";

//    @Test
    public void must_create_an_valid_yaml() throws Exception
    {
        ApplicationDescriptor description = new ApplicationDescriptor(UUID.randomUUID().toString()).setName("sw");
        
        description.setUser(new User().setUsername("user"));
        description.setRequirements(new Requirements().setMaximalCostPerHour(BigDecimal.ONE).setMemorySize(4).setNumberOfCpuCores(4).setPlatform(Platform.LINUX));
        
        description.addCloud(new Cloud().setName("ec2").setProvider(new ProviderSupport().setName("amazon")).setAccessKey(new AccessKey("a", "bc"))
                             .addRegion(new Region().setName("us-east-1").addZone(new Zone().setName("us-east-1a")))
                             .addAllInstanceTypes(new InstanceTypeReq().setName("m3.medium").setNumberOfInstances(1)))
                   .addCloud(new Cloud().setName("gce").setProvider(new ProviderSupport().setName("google")).setAccessKey(new AccessKey("b", "de"))
                             .addRegion(new Region().setName("us").addZone(new Zone().setName("us-central-1a"))));
        
        marshallAndUnmarshall(description);
    }
    
//    @Test
    public void must_create_an_valid_yaml_only_with_applications() throws Exception
    {
        ApplicationDescriptor description = new ApplicationDescriptor(UUID.randomUUID().toString()).setName("sw");
        description.setRequirements(new Requirements().setMaximalCostPerHour(BigDecimal.ONE).setMemorySize(4).setNumberOfCpuCores(4).setPlatform(Platform.LINUX));
        
        description.setUser(new User().setUsername("user"));
        marshallAndUnmarshall(description);
    }


    private void marshallAndUnmarshall(ApplicationDescriptor description) throws JsonProcessingException, IOException, JsonParseException,
            JsonMappingException, JAXBException
    {
        Applications applications = createApplications();
        description.addApplications(applications);
        String yaml = mapper_.writeValueAsString(description);
        assertNotNull(yaml);
        
        LOG.debug(yaml);
        
        ApplicationDescriptor app = mapper_.readValue(yaml, ApplicationDescriptor.class);
        assertNotNull(app);
        
        assertThat(description.getId(), equalTo(app.getId()));
        assertThat(description.getUser(), equalTo(app.getUser()));
        assertThat(description.getRequirements(), equalTo(app.getRequirements()));
        assertThat(description.getClouds().size(), equalTo(app.getClouds().size()));
        assertThat(description.getApplications().size(), equalTo(app.getApplications().size()));
        
        new JsonJaxbObjectMapper().readValue(new JsonJaxbObjectMapper().writeValueAsString(app), ApplicationDescriptor.class);
        new JAXBContextFactory<ApplicationDescriptor>(ApplicationDescriptor.class).marshal(app);
    }

    private Applications createApplications()
    {
        String defaultPath = "~/sequences";
        File[] files = new File(dir).listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.getName().endsWith(".fasta");
            }
        });
        
        Applications applications = new Applications();
        
        for (File file: files)
        {
            String name = file.getName();
            Application ssearch36 = new Application().setCommandLine("ssearch36 -d 0 ${query} ${library_file} >> ${score_table}").setName("ssearch36");
            ssearch36.setId(UUID.randomUUID().toString());
            ssearch36.addData(new AppData().setGenerated(YesNoEnum.NO).setName("query").setPath(String.format("%s/%s", defaultPath, file.getName())));
            ssearch36.addData(new AppData().setGenerated(YesNoEnum.NO).setName("library_file").setPath("~/uniprot_sprot.fasta"));
            ssearch36.addData(new AppData().setGenerated(YesNoEnum.YES).setName("score_table").setPath(String.format("~/scores/%s_scores.txt", name)));
            
            applications.add(ssearch36);
        }
        return applications;
    }
}
