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

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import static org.excalibur.core.io.utils.IOUtils2.*;

public final class OnlineChannel implements Closeable
{
    private final OutputStream input_;
    private final InputStream output_;
    private final Closeable closer_;

    public OnlineChannel(OutputStream input, InputStream output, Closeable closer)
    {
        this.input_ = checkNotNull(input, "input");
        this.output_ = checkNotNull(output, "output");
        this.closer_ = checkNotNull(closer, "closer");
    }

    /**
     * @return the command's {@code stdin} stream.
     */
    public OutputStream getInput()
    {
        return input_;
    }

    /**
     * 
     * @return the command's {@code stdout} stream.
     */
    public InputStream getOutput()
    {
        return output_;
    }

    @Override
    public void close() throws IOException
    {
        closeQuietly(input_, output_, closer_);
    }
    
    /**
     * 
     * @param command
     * @throws IOException
     */
    public void write(String command) throws IOException
    {
        if (!isNullOrEmpty(command))
        {
            write(command.getBytes());
        }
    }
    
    public void write(byte [] data) throws IOException
    {
        getInput().write(data);
        getInput().flush();
    }
    
    public void send(String command) throws IOException
    {
        write(command);
    }
    
    public void send(byte[] data) throws IOException
    {
        write(data);
    }
}
