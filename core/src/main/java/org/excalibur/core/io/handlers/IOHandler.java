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
package org.excalibur.core.io.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IOHandler extends Handler
{
    /**
     * Assigns a handler for an {@link InputStream}.
     * 
     * @param is
     *            {@link InputStream} to read. Might not be <code>null</code>.
     */
    void setInputStream(InputStream is) throws IOException;

    /**
     * Assigns a handler for an {@link OutputStream}.
     * 
     * @param os
     *            The {@link OutputStream} to write. Might not be <code>null</code>.
     */
    void setOutputStream(OutputStream os) throws IOException;
}
