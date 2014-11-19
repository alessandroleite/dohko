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
package org.excalibur.core.test.domain.repository;

import java.io.IOException;
import java.util.Date;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;
import org.excalibur.core.domain.repository.InstanceRepository;
import org.excalibur.core.domain.repository.InstanceTagRepository;
import org.excalibur.core.test.TestSupport;
import org.excalibur.core.util.Strings2;
import org.junit.Test;

import static org.excalibur.core.cloud.api.domain.Tags.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class InstanceTagRepositoryTest extends TestSupport
{
    private InstanceTagRepository instanceTagRepository_;
    private VirtualMachine instance_;
    
    @Override
    public void setup() throws IOException
    {
        super.setup();
        this.instanceTagRepository_ = this.openRepository(InstanceTagRepository.class);
        
        instance_ = new VirtualMachine();

        instance_.setConfiguration(new VmConfiguration()
                              .setKeyName("keytest")
                              .setPlatform("linux")
                              .setPlatformUserName("ubuntu")
                              .setPrivateIpAddress("127.0.0.1")
                              .setPublicDnsName("localhost")
                              .setPublicIpAddress("127.0.0.1"));
        instance_.setImageId("ami-832b72ea")
                .setLaunchTime(new Date())
                .setName("i-fd6125d3")
                .setType(InstanceType.valueOf("t1.micro").setId(120))
                .setOwner(user)
                .setLocation(zone);
        
        Integer id = openRepository(InstanceRepository.class).insertInstance(instance_);
        instance_.setId(id);
    }
    
    @Test
    public void must_associate_three_tags_for_one_instance()
    {
        Tags tags = new Tags();
        
        tags.add(new Tag("name", Strings2.validRfc1025RandomUUID()));
        tags.add(new Tag("test", "t1"));
        tags.add(new Tag("t1", "tt"));
        
        this.instanceTagRepository_.addTagsToInstance(instance_, tags);
        
        Tags t2 = newTags(this.instanceTagRepository_.listTagsOfInstanceId(instance_.getId()));
        
        assertThat(tags.size(), equalTo(t2.size()));
        
    }
}
