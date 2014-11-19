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
package org.excalibur.driver.google.compute.test;

import java.io.File;
import java.io.IOException;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.io.utils.IOUtils2;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.driver.google.compute.GoogleCompute;
import org.junit.Before;

import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class GoogleComputeTest
{
    private UserProviderCredentials credentials_;
    
    @Before
    public void setUp()
    {
        LoginCredentials loginCredentials = LoginCredentials.builder()
                .credential("compute-service-privatekey.p12")
                .identity("compute-service-account.json")
                .authenticateAsSudo(true)
                .credentialName("aleite")
                .build();
        
        credentials_ = new UserProviderCredentials()
                .setLoginCredentials(loginCredentials)
                .setProject("poised-bot-553")
                .setRegion(new Region().setName("us-central1-a").setEndpoint(
                                "https://www.googleapis.com/compute/v1/projects/poised-bot-553/zones/us-central1-a"));
    }
    
    //@Test
    public void must_allocate_one_micro_instance() throws IOException
    {
        GoogleCompute compute = new GoogleCompute(credentials_);
        
        File sshKey = new File(SystemUtils2.getUserDirectory(), "/.ssh/leite_rsa.pub");
        final String sshKeyMaterial = IOUtils2.readLines(sshKey);

        InstanceTemplate template = new InstanceTemplate()
                .setImageId("debian-7-wheezy")
                .setInstanceType(InstanceType.valueOf("f1-micro"))
                .setKeyName(credentials_.getLoginCredentials().getCredentialName())
                .setLoginCredentials(credentials_.getLoginCredentials().toBuilder().privateKey(sshKeyMaterial).user("aleite").build())
                .setMinCount(1)
                .setMaxCount(1)
                .setInstanceName("f1-micro-test")
                .setRegion(credentials_.getRegion())
                .setTags(Tags.newTags(new org.excalibur.core.cloud.api.domain.Tag("test", "allocation")));

        Instances instances = compute.createInstances(template);
        assertNotNull(instances);
        assertThat(1, equalTo(instances.size()));
        
        compute.terminateInstances(instances);
        
        compute.close();
    }
}
