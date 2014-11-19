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
import java.util.List;


import org.excalibur.core.cloud.api.Volume;
import org.excalibur.core.cloud.api.VolumeType;
import org.excalibur.core.domain.repository.VolumeRepository;
import org.excalibur.core.domain.repository.VolumeTypeRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class VolumeRepositoryTest extends TestSupport
{
    private VolumeRepository volumeRepository_;
    private VolumeType volumeType;

    @Override
    @Before
    public void setup() throws IOException
    {
        super.setup();
        this.volumeRepository_ = openRepository(VolumeRepository.class);
        this.volumeType = openRepository(VolumeTypeRepository.class).listVolumeTypesOfProvider(1).get(0);
    }

    @Test
    public void must_insert_one_disk()
    {
        Volume disk = new Volume().setCreatedIn(new Date())
                .setIops(10)
                .setName("disk-01-20")
                .setOwner(user)
                .setSizeGb(10)
                .setType(volumeType)
                .setZone(zone);
        
        Integer diskId = volumeRepository_.insert(disk);
        assertThat(1, equalTo(diskId));
        disk.setId(diskId);
        
        Volume disk2 = volumeRepository_.findById(diskId);
        assertNotNull(disk2);
        
        equals(disk, disk2);
        
        List<Volume> disks = volumeRepository_.findByType(volumeType);
        assertThat(1, equalTo(disks.size()));
        equals(disk, disks.get(0));
    }

    
    private void equals(Volume disk, Volume disk2)
    {
        assertThat(disk.getId(), equalTo(disk2.getId()));
        assertThat(disk.getCreatedIn().getTime(), equalTo(disk2.getCreatedIn().getTime()));
        assertThat(disk.getDeletedIn(), equalTo(disk2.getDeletedIn()));
        assertThat(disk.getIops(), equalTo(disk2.getIops()));
        assertThat(disk.getName(), equalTo(disk2.getName()));
        assertThat(disk.getOwner(), equalTo(disk2.getOwner()));
        assertThat(disk.getSizeGb(), equalTo(disk2.getSizeGb()));
        assertThat(disk.getType(), equalTo(disk2.getType()));
        assertThat(disk.getZone(), equalTo(disk2.getZone()));
    }
}
