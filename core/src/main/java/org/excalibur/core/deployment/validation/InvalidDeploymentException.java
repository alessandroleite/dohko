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
package org.excalibur.core.deployment.validation;

import org.excalibur.core.deployment.domain.Deployment;
import org.springframework.util.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class InvalidDeploymentException extends RuntimeException
{
    /**
     * Serial code version <code></code> for serialization.
     */
    private static final long serialVersionUID = 3389827390076194466L;

    private final Deployment deployment_;

    private final ValidationContext context_;

    public InvalidDeploymentException(Deployment deployment, ValidationContext context)
    {
        this.deployment_ = checkNotNull(deployment);
        this.context_ = checkNotNull(context);
    }

    /**
     * @return the deployment
     */
    public Deployment getDeployment()
    {
        return deployment_;
    }

    /**
     * @return the context_
     */
    public ValidationContext getContext()
    {
        return context_;
    }

    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder("cyclic=").append(context_.isCyclic()).append(";errors:[");
        sb.append(StringUtils.collectionToCommaDelimitedString(context_.getErrors())).append("]");

        return sb.toString();
    }
}
