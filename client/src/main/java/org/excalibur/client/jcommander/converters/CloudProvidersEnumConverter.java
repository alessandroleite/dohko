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
package org.excalibur.client.jcommander.converters;

import static com.google.common.base.Strings.isNullOrEmpty;

import org.excalibur.client.commands.DeployCommandOptions.CloudProvidersEnum;

import com.beust.jcommander.IStringConverter;

public class CloudProvidersEnumConverter implements IStringConverter<CloudProvidersEnum[]>
{
    @Override
    public CloudProvidersEnum[] convert(String value)
    {
        CloudProvidersEnum[] result = new CloudProvidersEnum[0];

        if (!isNullOrEmpty(value))
        {
            String[] names = value.split(",");

            result = new CloudProvidersEnum[names.length];

            for (int i = 0; i < names.length; i++)
            {
                result[i] = CloudProvidersEnum.valueOfFrom(names[i].trim().toLowerCase());
            }
        }

        return result;
    }
}
