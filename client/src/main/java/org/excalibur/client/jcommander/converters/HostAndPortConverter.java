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

import com.beust.jcommander.IStringConverter;
import com.google.common.net.HostAndPort;

import static com.google.common.base.Strings.*;
import static org.excalibur.core.util.SystemUtils2.*;

public class HostAndPortConverter implements IStringConverter<HostAndPort>
{
    private static final HostAndPort DEFAULT_HOST = HostAndPort.fromParts
    (
            getProperty("org.excalibur.server.host", "localhost"),
            getIntegerProperty("org.excalibur.server.port", 8080)
    );

    @Override
    public HostAndPort convert(String value)
    {
        if (!isNullOrEmpty(value))
        {
            String[] parts = value.split(":");
            return HostAndPort.fromParts(parts[0], parts.length == 1 ? DEFAULT_HOST.getPort() : Integer.parseInt(parts[1]));
        }

        return DEFAULT_HOST;
    }
}
