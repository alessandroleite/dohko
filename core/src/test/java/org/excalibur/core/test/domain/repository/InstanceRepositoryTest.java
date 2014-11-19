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

import java.util.Date;

import org.excalibur.core.cloud.api.InstanceStateDetails;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VmConfiguration;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.domain.repository.InstanceRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class InstanceRepositoryTest extends TestSupport
{
    private InstanceRepository instanceRepository_;

    @Override
    public void setup() throws java.io.IOException
    {
        super.setup();
        this.instanceRepository_ = openRepository(InstanceRepository.class);
    };

    @Test
    public void must_insert_one_instance()
    {
        VirtualMachine instance = new VirtualMachine();

        instance.setConfiguration(new VmConfiguration()
                              .setKeyName("keytest")
                              .setPlatform("linux")
                              .setPlatformUserName("ubuntu")
                              .setPrivateIpAddress("127.0.0.1")
                              .setPublicDnsName("localhost")
                              .setPublicIpAddress("127.0.0.1"));
        instance.setImageId("ami-832b72ea")
                .setLaunchTime(new Date())
                .setName("i-fd6125d3")
                .setType(InstanceType.valueOf("t1.micro").setId(120))
                .setOwner(user)
                .setLocation(zone);
        
        Integer instanceId = instanceRepository_.insertInstance(instance);
        assertNotNull(instanceId);
        
        VirtualMachine instanceFound = instanceRepository_.findInstanceById(instance.getOwner().getId(), instanceId);
        assertNotNull(instanceFound);
        assertNotNull(instanceFound.getOwner());
        
        assertThat(instanceId, equalTo(instanceFound.getId()));
        
        InstanceStateDetails state = new InstanceStateDetails().setInstance(instanceFound).setState(InstanceStateType.RUNNING).setTime(new Date());
        Integer stateId = this.instanceRepository_.insertInstanceState(state);
        assertNotNull(stateId);
        
        
        instanceFound = instanceRepository_.findInstanceById(instance.getOwner().getId(), instanceId);
        assertThat(state.getState(), equalTo(instanceFound.getState().getState()));
    }
}
