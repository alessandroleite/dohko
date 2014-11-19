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

import java.io.IOException;

import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.io.utils.IOUtils2;
import org.excalibur.jackson.databind.JsonYamlObjectMapper;

import com.beust.jcommander.IStringConverter;

public class ApplicationDescriptorConverter implements IStringConverter<ApplicationDescriptor>
{

    private static final JsonYamlObjectMapper YAML_OBJECT_MAPPER = new JsonYamlObjectMapper();

    @Override
    public ApplicationDescriptor convert(String file)
    {
        try
        {
            return YAML_OBJECT_MAPPER.readValue(IOUtils2.readLines(file), ApplicationDescriptor.class);
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
