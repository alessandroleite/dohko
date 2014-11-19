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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Adrian Cole
 */
public interface Payload extends Closeable
{
    /**
     * Creates a new InputStream object of the payload.
     */
    InputStream openStream() throws IOException;

    /**
     * Payload in its original form.
     */
    Object getRawContent();

    /**
     * Tells if the payload is capable of producing its data more than once.
     */
    boolean isRepeatable();

    /**
     * release resources used by this entity. This should be called when data is discarded.
     */
    void release();

//    MutableContentMetadata getContentMetadata();
//
//    void setContentMetadata(MutableContentMetadata in);

}
