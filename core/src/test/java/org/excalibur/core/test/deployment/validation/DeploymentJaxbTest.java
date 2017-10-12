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
package org.excalibur.core.test.deployment.validation;

import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.junit.Assert;

import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Dependency;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Param;
import org.excalibur.core.deployment.domain.Plugin;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.deployment.domain.Reference;
import org.excalibur.core.util.JAXBContextFactory;
import org.junit.Before;
import org.junit.Test;

public class DeploymentJaxbTest
{
    static final Deployment VALID_DEPLOYMENT_DESCRIPTION = new Deployment()
            .withNode(new Node().setName("n1")
                                .setProvider(new Provider().setImageId("ami-6ac2a85a").setInstanceType("t1.micro").setName("amazon"))
                                .setCount(1)
                                .addTags(Tag.valueOf("name", "value"))
                                .setRegion("us-west-2")
                                .setGroup("excalibur")
                                .setZone("us-west-2a")
                                .setUserData("data")
            .setCredential(new Credential().setName("aleite").setIdentity("abc").setCredential("acbd")))
            .withUsername("alice");
    
    
    static final Deployment VALID_DEPLOYMENT_DESCRIPTION_2 = new Deployment()
            .withNode(new Node().setName("server").setRegion("us-west-2")
                            .addPlugin(new Plugin().withScript("nfs_server.sh").withParam(new Param().withName("EXPORT").withValue("/mnt")))
                            .setProvider(new Provider().setName("amazon").setImageId("ami-912837").setInstanceType("c1.xlarge"))
                            .setCredential(new Credential().setName("server-key").setIdentity("abc").setCredential("acbd")))

            .withNode(new Node()
                            .setCount(3)
                            .setName("client")
                            .setGroup("clients")
                            .setRegion("us-west-2")
                            .setProvider(new Provider().setImageId("ami-90186").setInstanceType("c1.xlarge").setName("amazon"))
                            .addPlugin(new Plugin()
                                            .withScript("nfs_client.sh")
                                            .withParam(new Param().withName("SERVER")
                                                                  .withReference(new Reference().setNode("server").setAttribute("local-ipv4")))
                                            .withParam(new Param().withName("PATH").withValue("/mnt"))
                                            .withParam(new Param().withName("MOUNT").withValue("/nfs/data")))
                            .addDependencies(new Dependency("server")))
            .withCredential(new Credential().setName("default-key").setCredential("abcd").setIdentity("abc")).withUsername("alice");
    
    protected JAXBContextFactory<Deployment> context;
    
    @Before
    public void setup() throws JAXBException
    {
        Schema schema = JAXBContextFactory.getSchema(DeploymentJaxbTest.class.getClassLoader().getResource("org/excalibur/core/deployment/domain/deployment.xsd"));
        context = new JAXBContextFactory<Deployment>(schema, Deployment.class.getPackage().getName());
    }
    
    @Test
    public void must_generate_valid_description() throws JAXBException
    {
        String marshall = context.marshal(VALID_DEPLOYMENT_DESCRIPTION);
        Assert.assertNotNull(marshall);        
        context.unmarshal(marshall);
        
        marshall = context.marshal(VALID_DEPLOYMENT_DESCRIPTION_2);
        context.unmarshal(marshall);
    }
    
}
