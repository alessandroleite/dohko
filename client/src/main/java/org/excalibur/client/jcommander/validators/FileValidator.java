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

import java.io.File;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class FileValidator implements IParameterValidator
{

    @Override
    public void validate(String name, String value) throws ParameterException
    {
        File f = new File(value);

        if (!f.exists())
        {
            throw new ParameterException(String.format("File %s does not exist", f.getAbsolutePath()));
        }

        if (!f.canRead())
        {
            throw new ParameterException(String.format("Application cannot read the %s ", f.getAbsolutePath()));
        }

    }
}
