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
package org.excalibur.core.ssh.ethz;

import java.io.IOException;

import org.excalibur.core.exec.ExecutableChannel;
import org.excalibur.core.exec.ExecutableResponse;
import org.excalibur.core.exec.OnlineChannel;
import org.excalibur.core.io.Payload;
import org.excalibur.core.ssh.SshClient;

public class EthzSshClient implements SshClient
{
    public EthzSshClient()
    {
    }

    @Override
    public String getUsername()
    {
        return null;
    }

    @Override
    public String getHostAddress()
    {
        return null;
    }

    @Override
    public void put(String path, Payload contents)
    {
    }

    @Override
    public Payload get(String path)
    {
        return null;
    }

    @Override
    public ExecutableResponse execute(String command)
    {
        return null;
    }

    @Override
    public ExecutableChannel executableChannel(String command)
    {
        return null;
    }

    @Override
    public OnlineChannel shell()
    {
        return null;
    }

    @Override
    public void connect()
    {
    }

    @Override
    public void disconnect()
    {
    }

    @Override
    public void put(String path, String contents)
    {
    }

    @Override
    public void close() throws IOException
    {
        // TODO Auto-generated method stub
        
    }
}
