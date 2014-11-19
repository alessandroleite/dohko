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
package org.excalibur.core.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class InputStreamPayload extends BasePayload<InputStream>
{
    public InputStreamPayload(InputStream content)
    {
        super(content);
    }

    @Override
    public InputStream openStream() throws IOException
    {
        return content;
    }

    @Override
    public boolean isRepeatable()
    {
        return false;
    }

    @Override
    public void release()
    {
        IOUtils.closeQuietly(this.content);
    }
}
