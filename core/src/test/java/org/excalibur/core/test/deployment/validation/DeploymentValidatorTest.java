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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Dependency;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.deployment.domain.Param;
import org.excalibur.core.deployment.domain.Plugin;
import org.excalibur.core.deployment.domain.Provider;
import org.excalibur.core.deployment.domain.Reference;
import org.excalibur.core.deployment.validation.DeploymentValidator;
import org.excalibur.core.deployment.validation.ValidationContext;
import org.excalibur.core.validator.ValidationResult;
import org.junit.Test;

public class DeploymentValidatorTest
{
    static final Deployment VALID_DEPLOYMENT_DESCRIPTION = new Deployment()
    .withNode(new Node().setName("server").addPlugin(new Plugin().withScript("nfs_server.sh")
            .withParam(new Param().withName("EXPORT").withValue("/mnt")))
            .setProvider(new Provider().setName("amazon").setImageId("ami-912837").setInstanceType("c1.xlarge"))
            .setRegion("us-east-1")
            .setCredential(new Credential().setName("server-key")))
    .withNode(new Node().setCount(3).setName("client").setGroup("clients")
            .setProvider(new Provider().setImageId("ami-90186").setInstanceType("c1.xlarge").setName("amazon"))
            .setRegion("us-east-1")
            .setCredential(new Credential().setName("server-key"))
            .addPlugin(new Plugin().withScript("nfs_client.sh")
                    .withParam(new Param().withName("SERVER").withReference(new Reference().setNode("server").setAttribute("local-ipv4")))
                    .withParam(new Param().withName("PATH").withValue("/mnt"))
                    .withParam(new Param().withName("MOUNT").withValue("/nfs/data")))
            .addDependencies(new Dependency("server")))
    .withUsername("username");
            
    
    static final Deployment CYCLIC_SERVER_DEPENDENCIES = new Deployment()
    .withNode(new Node().setName("server").addPlugin(new Plugin().withScript("nfs_server.sh")
            .withParam(new Param().withName("EXPORT").withValue("/mnt")))
            .setProvider(new Provider().setName("amazon").setImageId("ami-912837").setInstanceType("c1.xlarge"))
            .setCredential(new Credential().setName("server-key"))
            .setRegion("us-east-1")
            .addDependencies(new Dependency("client")))
    .withNode(new Node().setCount(3).setName("client").setGroup("clients")
            .setProvider(new Provider().setImageId("ami-90186").setInstanceType("c1.xlarge").setName("amazon"))
            .setRegion("us-east-1")
            .addPlugin(new Plugin().withScript("nfs_client.sh")
                    .withParam(new Param().withName("SERVER").withReference(new Reference().setNode("server").setAttribute("local-ipv4")))
                    .withParam(new Param().withName("PATH").withValue("/mnt"))
                    .withParam(new Param().withName("MOUNT").withValue("/nfs/data")))
            .addDependencies(new Dependency("server")))
            .withCredentials(new Credential().setName("default-key"));
    
    static final Deployment CYCLIC_SERVER_DEPENDENCIES2 = new Deployment()
       .withNode(new Node().setName("n1")
                           .setProvider(new Provider().setImageId("ami-912837").setInstanceType("c1.xlarge"))
                           .addDependencies(new Dependency("n2"), new Dependency("n3"))
                           .setCredential(new Credential().setIdentity("key1"))
                           .setRegion("us-east-1"))
       .withNode(new Node().setName("n2")
               .setProvider(new Provider().setImageId("ami-912837").setInstanceType("c1.xlarge"))
               .setRegion("us-east-1")
               .addDependencies(new Dependency("n3"))
               .setCredential(new Credential().setName("key1")))
       .withNode(new Node().setName("n3")
               .setProvider(new Provider().setImageId("ami-912837").setInstanceType("c1.xlarge"))
               .addDependencies(new Dependency("n1"))
               .setCredential(new Credential().setName("key1")));
    
    static final Deployment SELF_DEPENDENCY = new Deployment()
       .withNode(new Node().setName("n1")
            .setProvider(new Provider().setImageId("ami-912837").setInstanceType("c1.xlarge"))
            .setRegion("us-east-1")
            .addDependencies(new Dependency("n1"))
            .setCredential(new Credential().setName("key1")));
    
    static final Deployment UNDEFINED_DEPENDENCY = new Deployment()
    .withNode(new Node().setName("n1")
                        .setProvider(new Provider().setImageId("ami-912837").setInstanceType("c1.xlarge"))
                        .addDependencies(new Dependency("n2"))
                        .setCredential(new Credential().setName("key1").setIdentity("abc").setCredential("abcd")));
    
    private final DeploymentValidator VALIDATOR = new DeploymentValidator();
    
    @Test
    public void must_be_a_valid_deploy_descriptor()
    {
        ValidationResult<ValidationContext> result = VALIDATOR.validate(VALID_DEPLOYMENT_DESCRIPTION);
        assertFalse(result.get().hasError());
    }
    
    @Test
    public void must_be_cyclic()
    {
        ValidationResult<ValidationContext> result = VALIDATOR.validate(CYCLIC_SERVER_DEPENDENCIES);
        assertTrue(result.get().isCyclic());
        
        result = VALIDATOR.validate(CYCLIC_SERVER_DEPENDENCIES2);
        assertTrue(result.get().isCyclic());
        
        result = VALIDATOR.validate(SELF_DEPENDENCY);
        assertTrue(result.get().isCyclic());
        
    }
    
    @Test
    public void must_have_undefined_dependency()
    {
        ValidationResult<ValidationContext> result = VALIDATOR.validate(UNDEFINED_DEPENDENCY);
        assertTrue(result.get().hasError());
        assertEquals(1, result.get().getErrors().size());
    }
}
