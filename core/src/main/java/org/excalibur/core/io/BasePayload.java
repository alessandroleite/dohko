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

public abstract class BasePayload<V> implements Payload
{
    protected final V content;
    protected transient volatile boolean written;

    public BasePayload(V content)
    {
        this.content = content;
    }

    @Override
    public void close() throws IOException
    {
        release();
    }

    @Override
    public V getRawContent()
    {
        return this.content;
    }

    @Override
    public boolean isRepeatable()
    {
        return true;
    }

    @Override
    public void release()
    {
        // nothing to release here
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (obj == null)
        {
            return false;
        }
        
        if (!(obj instanceof Payload))
        {
            return false;
        }
        
        Payload other = (Payload) obj;
        
        if (content == null)
        {
            if (other.getRawContent() != null)
            {
                return false;
            }
        }
        else if (!content.equals(other.getRawContent()))
        {
            return false;
        }
        return true;
    }
}
