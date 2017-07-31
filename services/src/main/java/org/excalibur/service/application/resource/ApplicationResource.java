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
package org.excalibur.service.application.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.service.application.ApplicationService;
import org.excalibur.service.application.JobService;
import org.excalibur.service.manager.NodeManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.excalibur.core.execution.job.ApplicationExecutionResult;

import static java.util.UUID.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.*;

import java.util.List;



@Path(ApplicationResource.APPLICATION_PATH)
public class ApplicationResource
{
    public static final String APPLICATION_PATH = "/application";

    @Context
    private UriInfo uriInfo_;

    @Context
    private Request request_;

    @Autowired
    private ApplicationService applicationService_;
    
    @Autowired
    private JobService jobService_;
    
    
    @GET
    @Path("{id}")
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    @Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })
    public Response findById(@PathParam("id") String id)
    {
    	Application application = jobService_.findApplicationByUUID(id);
    	
    	if (application != null) 
    	{
    		ok().entity(application).build();
    	}
    	
    	return status(NOT_FOUND).build();
    }
    
    @GET
    @Path("{id}/tasks")
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    @Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })
    public Response getJobTasksResult(@PathParam("id") String id)
    {
    	List<ApplicationExecutionResult> results = jobService_.getJobTasksResult(id);
    	
    	if (results != null && !results.isEmpty()) 
    	{
    		ok().entity(results).build();
    	}
    	
    	return status(NOT_FOUND).build();
    }

    @PUT
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    @Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })
    public Response deploy(ApplicationDescriptor job) throws Exception
    {
        checkNotNull(job);

        if (isNullOrEmpty(job.getId()))
        {
            job.setId(randomUUID().toString());
        }

        applicationService_.register(job);
        NodeManagerFactory.getManagerReference().provision(job);

        return Response.status(ACCEPTED).entity(job.getId()).build();
    }

    @POST
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    public Response execute(ApplicationExecutionRequest app) throws Exception
    {
        checkNotNull(app).setWorker(NodeManagerFactory.getManagerReference().getThisNodeReference());
        NodeManagerFactory.getManagerReference().execute(app);

        return Response.status(ACCEPTED).build();
    }

    @POST
    @Path("reply")
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    public Response result(ApplicationExecutionResult result) throws Exception
    {
        NodeManagerFactory.getManagerReference().finished(result, result.getWorker());
        return Response.status(ACCEPTED).build();
    }
}
