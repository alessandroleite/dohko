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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.excalibur.core.io.utils.IOUtils2;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

public class ExecutableChannel implements Closeable
{
    private final OutputStream input;
    private final InputStream output;
    private final InputStream error;
    private final Supplier<Integer> exitStatus;
    private final Closeable closer;

    public ExecutableChannel(OutputStream input, InputStream output, InputStream error, Supplier<Integer> exitStatus, Closeable closer)
    {
        this.input = Preconditions.checkNotNull(input, "input");
        this.output = Preconditions.checkNotNull(output, "output");
        this.error = Preconditions.checkNotNull(error, "error");
        this.exitStatus = Preconditions.checkNotNull(exitStatus, "exitStatus");
        this.closer = Preconditions.checkNotNull(closer, "closer");
    }

    /**
     * @return the command's {@code stdin} stream.
     */
    public OutputStream getInput()
    {
        return input;
    }

    /**
     * 
     * @return the command's {@code stderr} stream.
     */
    public InputStream getError()
    {
        return error;
    }

    /**
     * 
     * @return the command's {@code stdout} stream.
     */
    public InputStream getOutput()
    {
        return output;
    }

    /**
     * 
     * @return the exit status of the command if it was received, or {@code null} if this information was not received.
     */
    public Supplier<Integer> getExitStatus()
    {
        return exitStatus;
    }

    @Override
    public void close() throws IOException
    {
        IOUtils2.closeQuietly(this.input);
        IOUtils2.closeQuietly(this.output);
        IOUtils2.closeQuietly(this.error);
        IOUtils2.closeQuietly(closer);
    }
}
