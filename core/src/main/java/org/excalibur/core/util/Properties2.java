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
package org.excalibur.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

public final class Properties2
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Properties2.class.getName());
    
    private Properties2()
    {
        throw new UnsupportedOperationException();
    }

    public static Properties load(InputStream inStream)
    {
        Properties properties = new Properties();

        try
        {
            properties.load(inStream);
        }
        catch (IOException e)
        {
            LOGGER.error("Error on loading the properties. Error message: {}", e.getMessage());
            AnyThrow.throwUncheked(e);
        }

        return properties;
    }
    
    public static Properties load(String resource)
    {
        return load(ClassUtils.getDefaultClassLoader().getResourceAsStream(resource));
    }
    
    public static Properties load(File file) throws FileNotFoundException
    {
        return load(new FileInputStream(file));
    }
}
