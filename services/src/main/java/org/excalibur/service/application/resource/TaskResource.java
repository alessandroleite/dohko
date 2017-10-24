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

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.repository.TaskRepository;
import org.excalibur.core.execution.domain.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.*;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;


@Path("/task")
public class TaskResource 
{
	@Context
    private UriInfo uriInfo_;

    @Context
    private Request request_;
    
    @Autowired
    private TaskRepository taskRepository_;
    
    @Autowired
    private TaskStatusRepository taskStatusRepository_;
    
    @GET
    @Path("{taskId}")
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    @Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })
    public Response findById(@PathParam("taskId") String taskId)
    {
    	Application task = taskRepository_.findByUUID(taskId);
    	return buildResponse(task);
    }
    
    @GET
    @Path("{taskId}/status")
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    @Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })
    public Response lastStatus(@PathParam("taskId") String taskId)
    {
    	Optional<TaskStatus> status = taskStatusRepository_.getLastStatusOfTask(taskId);
    	return buildResponse(status.orNull());
    }
    
    @GET
    @Path("{taskId}/statuses")
    @Consumes({ APPLICATION_XML, APPLICATION_JSON })
    @Produces({APPLICATION_XML, APPLICATION_JSON, TEXT_XML })
    public Response statuses(@PathParam("taskId") String taskId)
    {
    	List<TaskStatus> statuses = taskStatusRepository_.getAllStatusesOfTask(taskId);
    	return buildResponse(statuses);
    }
    
    
    protected <T> Response buildResponse(T entity)
    {
    	return entity != null ? ok().entity(entity).build() : status(NOT_FOUND).build();
    }
}
