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

import java.util.List;
import java.util.Map;

import org.excalibur.core.deployment.domain.Credential;
import org.excalibur.core.deployment.domain.Dependency;
import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.Node;
import org.excalibur.core.validator.ValidationResult;
import org.excalibur.core.validator.Validator;

import com.google.common.collect.Lists;

import static com.google.common.base.Strings.*;

public class DeploymentValidator implements Validator<Deployment, ValidationResult<ValidationContext>>
{
    private static final String NODES_DATA_KEY = "nodes";

    @Override
    public ValidationResult<ValidationContext> validate(Deployment deployment)
    {
        final ValidationResult<ValidationContext> result = new ValidationResult<ValidationContext>(new ValidationContext());

        Map<String, Credential> credentialsMap = deployment.getCredentialMaps();
        result.get().put(NODES_DATA_KEY, deployment.getNodesMap());

        List<String> nodeNames = Lists.newArrayList();
        
        for (Node node : deployment.getNodes())
        {
            validateDependencies(result.get(), node);
            Credential credential = node.getCredential();

            if (credential == null || isNullOrEmpty(credential.getName()))
            {
                if (credentialsMap.isEmpty())
                {
                    result.get().addError(String.format("A credential was not defined for node %s", node.getName()));
                }
                else 
                {
                    node.setCredential(credentialsMap.values().iterator().next());
                }
            }
            
            if (nodeNames.contains(node.getName()))
            {
                result.get().addError(String.format("Duplicated node name [%s]", node.getName()));
            }
            else
            {
                nodeNames.add(node.getName());
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void validateDependencies(final ValidationContext context, final Node node)
    {
        if (ValidationContext.isRegistered(node))
        {
            context.cyclic();
            return;
        }

        ValidationContext.register(node);

        try
        {
            for (Dependency dependency : node.getDependencies())
            {
                Node dependentNode = ((Map<String, Node>) context.getData(NODES_DATA_KEY)).get(dependency.getNode());

                if (dependentNode != null)
                {
                    validateDependencies(context, dependentNode);
                }
                else
                {
                    context.addError(String.format("The dependency %s was not found", dependency.getNode()));
                }
            }
        }
        finally
        {
            ValidationContext.unregister(node);
        }
    }
}
