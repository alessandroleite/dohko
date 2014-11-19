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
package org.excalibur.client.jcommander.validators;

import org.excalibur.client.commands.DeployCommandOptions.CloudProvidersEnum;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class CloudProvidersValidator implements IParameterValidator
{

    final static String VALID_PROVIDERS_NAMES = "ec2, gce, microsoft";
    
    
    @Override
    public void validate(String name, String value) throws ParameterException
    {
        String[] options = value.split(",");

        for (String provider : options)
        {
            if (CloudProvidersEnum.valueOfFrom(provider) == null)
            {
                throw new ParameterException(String.format("Invalid %s cloud provider. The valid options are %s", value, options));
            }
        }
    }
}
