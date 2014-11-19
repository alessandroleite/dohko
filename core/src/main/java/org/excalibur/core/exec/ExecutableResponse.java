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
package org.excalibur.core.exec;

import java.io.Serializable;

import com.google.common.base.Objects;

public class ExecutableResponse implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 715561931782927472L;

    private final String output;
    private final String error;
    private final int exitStatus;

    public ExecutableResponse(String output, String error, int exitStatus)
    {
        this.output = output;
        this.error = error;
        this.exitStatus = exitStatus;
    }

    public String getError()
    {
        return error;
    }

    public String getOutput()
    {
        return output;
    }

    public int getExitStatus()
    {
        return exitStatus;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(output, error, exitStatus);
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

        if (!obj.getClass().equals(getClass()))
        {
            return false;
        }

        ExecutableResponse that = ExecutableResponse.class.cast(obj);
        
        return Objects.equal(this.output, that.output) && 
               Objects.equal(this.error, that.error)   && 
               Objects.equal(this.exitStatus, that.exitStatus);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("output", output)
                .add("error", error)
                .add("exitStatus", exitStatus)
                .toString();
    }
}
