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
package org.excalibur.core.test.service.repository;

import java.io.IOException;

import org.excalibur.core.cloud.service.domain.Protocol;
import org.excalibur.core.cloud.service.domain.Service;
import org.excalibur.core.cloud.service.domain.repository.ServiceRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class ServiceRepositoryTest extends TestSupport
{
    private ServiceRepository serviceRepository_;

    @Before
    @Override
    public void setup() throws IOException
    {
        super.setup();
        this.serviceRepository_ = openRepository(ServiceRepository.class);
    }

    @Test
    public void must_insert_one_service()
    {
        Service service = new Service().withMediaType("application/xml")
                .withName("monitoring").withProtocol(Protocol.HTTP).withURI("http://localhost/monitor");
        
        Integer serviceId = serviceRepository_.insert(service);
        assertThat(1, equalTo(serviceRepository_.getAllServices().size()));
        assertThat(serviceId, equalTo(serviceRepository_.findServiceById(serviceId).getId()));
        assertThat(service.getName(), equalTo(serviceRepository_.findServiceByName(service.getName()).get(0).getName()));
    }
}
