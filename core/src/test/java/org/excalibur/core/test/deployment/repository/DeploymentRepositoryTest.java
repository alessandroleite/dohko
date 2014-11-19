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
package org.excalibur.core.test.deployment.repository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.DeploymentStatus;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.deployment.domain.repository.DeploymentRepository;
import org.excalibur.core.test.TestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


public class DeploymentRepositoryTest extends TestSupport
{
    private DeploymentRepository deploymentRepository;

    @Override
    @Before
    public void setup() throws IOException
    {
        super.setup();
        deploymentRepository = openRepository(DeploymentRepository.class);
    }

    @Test
    public void must_insert_one_deployment()
    {
        Deployment deployment = new Deployment().withNode(new Node().setName("n1")
                .setProvider(new Provider().setImageId("ami-912837").setInstanceType("c1.xlarge"))
                .setCredential(new Credential().setName("key1").setIdentity("abc").setCredential("abcd")))
                .setUsername(user.getUsername())
                .setStatus(DeploymentStatus.SUBMITTED)
                .setStatusTime(new Date())
                .setUser(user);
        
        Integer deploymentId = deploymentRepository.insert(deployment);
        assertThat(deploymentId, equalTo(deploymentRepository.findDeploymentById(deploymentId).getId()));
        
        List<Deployment> deployments = deploymentRepository.getAllDeploymentInStatus(deployment.getStatus().name());
        assertNotNull(deployments);
        assertEquals(1, deployments.size());
        
        assertThat(DeploymentStatus.SUBMITTED, equalTo(deployments.get(0).getStatus()));
    }
}
