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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.deployment.utils.DeploymentUtils;
import org.excalibur.discovery.ws.ext.ObjectMapperProvider;
import org.glassfish.jersey.jackson.JacksonFeature;


public class DeployTest
{
    public static void main(String[] args) throws JAXBException
    {
        Client client = ClientBuilder.newClient().register(ObjectMapperProvider.class).register(JacksonFeature.class);

        WebTarget target = client.target(String.format("http://%s:%s/deployment", "localhost",
                System.getProperty("org.excalibur.server.port", "8080")));
        
        Deployment deployment = new Deployment();
        
        deployment.withNode(
                new Node().setName("n1")
                          .setProvider(new Provider().setName("amazon").setImageId("ami-018c9568").setInstanceType("t1.micro"))
                          .setCredential(new Credential().setName("alice")).addTags(new Tag().setName("name").setValue("deploy-1"))
                          .setRegion("us-east-1")
                          .setCount(1))
                  .setUsername("alice");
        
        deployment.withNode(
                new Node().setName("n2")
                          .setProvider(new Provider().setName("amazon").setImageId("ami-ee4f77ab").setInstanceType("t1.micro"))
                          .setCredential(new Credential().setName("alice")).addTags(new Tag().setName("name").setValue("deploy-1"))
                          .setRegion("us-west-1")
                          .setCount(1));
        
        String deploy = DeploymentUtils.marshall(deployment);
        
        Response response = target.request()
                .put(Entity.entity(deploy, MediaType.APPLICATION_XML));
        
        System.out.println(response.getStatusInfo());
    }
}
