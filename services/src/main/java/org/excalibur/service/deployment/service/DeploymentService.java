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
package org.excalibur.service.deployment.service;

import javax.annotation.Nonnull;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.DeploymentStatus;
import org.excalibur.core.deployment.validation.InvalidDeploymentException;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.UserNotFoundException;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.service.deployment.resource.DeploymentStatusDetails;

public interface DeploymentService
{
    /**
     * Creates a deployment plan for a given {@link Deployment}.
     * 
     * @param deployment
     * @return A unique deployment ID.
     * @throws UserNotFoundException If the user was not found.
     * @throws InvalidDeploymentException If the deployment description is invalid.
     * @see org.excalibur.core.deployment.validation.DeploymentValidator
     */
    Integer createDeployment(Deployment deployment);

    /**
     * Finds a deployment of a user.
     * @param username The user's name. Might not be <code>null</code>.
     * @param deploymentId The deployment's id. Might not be <code>null</code>.
     * @return The {@link Deployment} with the given id or <code>null</code> if not found.
     * @throws NullPointerException If {@code username} or {@code deploymentId} is <code>null</code>.
     */
    Deployment findDeployment(String username, Integer deploymentId);

    /**
     * Returns the status of a given deployment Id. If the deployment does not exists, this method returns
     * a {@link DeploymentStatusDetails} object with an {@link DeploymentStatus#UNKNOWN} status.
     * 
     * @param username The user who the deployment belongs to. Might not be <code>null</code>.
     * @param deploymentId The deployment id. Might not be <code>null</code>.
     * @return The status of the {@link Deployment}.
     */
    DeploymentStatusDetails getDeploymentStatus(String username, Integer deploymentId);
    
    WorkflowDescription createWorkflowFor(Deployment deployment);

    void create(@Nonnull Deployment deployment);
    
    void deploy(@Nonnull Iterable<InstanceType> types, @Nonnull User user);
}
