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
package org.excalibur.service.deployment.resource;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.excalibur.core.cloud.api.InstanceTypes;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.validation.DeploymentValidator;
import org.excalibur.core.deployment.validation.InvalidDeploymentException;
import org.excalibur.core.deployment.validation.ValidationContext;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserNotFoundException;
import org.excalibur.core.services.UserService;
import org.excalibur.service.deployment.service.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;


@Path(DeploymentResource.DEPLOYMENT_PATH)
public class DeploymentResource
{
    public static final String DEPLOYMENT_PATH = "/deployment";
    
    @Autowired
    private DeploymentService deploymentService_;
    
    @Autowired
    private UserService userService_;

    @Context
    private UriInfo uriInfo_;

    @Context
    private Request request_;
    
    @GET
    @Path("{username}/{deploymentId}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findById(@PathParam("username") String username, @PathParam("deploymentId") Integer deploymentId)
    {
        Deployment deployment = this.deploymentService_.findDeployment(username, deploymentId);

        if (deployment != null)
        {
            return Response.ok().entity(deployment).build();
        }

        return Response.status(Status.NOT_FOUND).header("deployment-id", deploymentId).header("user", username).build();
    }

    @GET
    @Path("status/{username}/{deploymentId}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response status(@PathParam("username") String username, @PathParam("deploymentId") Integer deploymentId)
    {
        DeploymentStatusDetails status = this.deploymentService_.getDeploymentStatus(username, deploymentId);
        return Response.ok().entity(status).build();
    }
    
    
    @POST
    @Path("types/{username}/deploy")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response deploy(InstanceTypes types, @PathParam("username") String username)
    {
        final User user = userService_.findUserByUsername(username);
        
        if (null == user)
        {
            throw new UserNotFoundException(username, String.format("User [%s] does not exist", username));
        }
        
        this.deploymentService_.deploy(types, user);
        
        return Response.ok().build();
    }

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response add(Deployment deployment)
    {
        ValidationContext validationResult = new DeploymentValidator().validate(deployment).get();
        
        if (validationResult.hasError() || validationResult.isCyclic())
        {
            throw new InvalidDeploymentException(deployment, validationResult);
        }
        
        if (null == userService_.findUserByUsername(deployment.getUsername()))
        {
            throw new UserNotFoundException(deployment.getUsername(), String.format("User [%s] does not exist", deployment.getUsername()));
        }
        
       deploymentService_.create(deployment);
        
        return Response.ok().build();
    }
}
