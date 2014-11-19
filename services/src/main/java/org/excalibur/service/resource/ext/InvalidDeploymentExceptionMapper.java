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
package org.excalibur.service.resource.ext;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.excalibur.core.deployment.validation.InvalidDeploymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class InvalidDeploymentExceptionMapper implements ExceptionMapper<InvalidDeploymentException>
{
    private static final Logger LOG = LoggerFactory.getLogger(InvalidDeploymentExceptionMapper.class.getName());
    
    @Override
    public Response toResponse(InvalidDeploymentException exception)
    {
        LOG.error("Invalid deployment request. Error message: [{}]. Cause: [{}]", exception.getMessage(), exception.getCause() != null ? exception
                .getCause().getMessage() : "", exception);
        return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
    }
}
