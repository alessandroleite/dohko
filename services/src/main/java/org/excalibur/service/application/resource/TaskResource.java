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
    	TaskStatus status = taskStatusRepository_.getLastStatusOfTask(taskId);
    	return buildResponse(status);
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
