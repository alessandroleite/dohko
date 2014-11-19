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
import java.math.BigDecimal;

import org.excalibur.core.cloud.api.InstanceFamilyType;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.domain.repository.InstanceTypeRepository;
import org.excalibur.core.test.MockProvider;
import org.excalibur.core.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class InstanceTypeRepositoryTest extends TestSupport
{
    private InstanceTypeRepository instanceTypeRepository;
    
    @Override
    @Before
    public void setup() throws IOException
    {
        super.setup();
        this.instanceTypeRepository = openRepository(InstanceTypeRepository.class);
    }

    @Test
    public void must_insert_one_instance_type()
    {
        InstanceType type = new InstanceType().setName("micro").setProvider(new MockProvider()).setFamilyType(InstanceFamilyType.SHARED);
        type.getConfiguration().setGeneration(1)
                               .setMaximumNumberOfInstances(20)
                               .setNumberOfComputUnits(1)
                               .setNumberOfCores(1)                               
                               .setRamMemorySizeGb(0.658)
                               .setDiskSizeGb(10L)
                               .setSustainablePerformanceGflops(new BigDecimal("4.400000"));
        
        Integer typeId = instanceTypeRepository.insertInstanceType(type);
        assertNotNull(typeId);
        type.setId(typeId);
        
        InstanceType type2 = instanceTypeRepository.findInstanceTypeByName(type.getName());
        assertNotNull(type2);
        
        assertThat(type.getId(), equalTo(type2.getId()));
        assertThat(type.getName(), equalTo(type2.getName()));
        assertThat(type.getFamilyType(), equalTo(type2.getFamilyType()));
//        assertThat(type.getConfiguration(), equalTo(type2.getConfiguration()));
    }
}
