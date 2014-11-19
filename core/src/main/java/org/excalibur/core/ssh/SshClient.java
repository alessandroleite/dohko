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
package org.excalibur.core.ssh;

import java.io.Closeable;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.exec.ExecutableChannel;
import org.excalibur.core.exec.ExecutableResponse;
import org.excalibur.core.exec.OnlineChannel;
import org.excalibur.core.io.Payload;

import com.google.common.net.HostAndPort;

public interface SshClient extends Closeable
{
    interface Factory
    {
        SshClient create(HostAndPort socket, LoginCredentials credentials);

        boolean isAgentAvailable();
    }

    String getUsername();

    String getHostAddress();

    void put(String path, Payload contents);

    Payload get(String path);

    /**
     * Executes a process and block until it is complete.
     * 
     * @param command
     *            command line to invoke.
     * @return output of the command
     * @throws ExecutableResponse
     *             if the status code is different of 0 and the maximum retries was reached.
     */
    ExecutableResponse execute(String command);
    
    /**
     * Executes a script and block until it is complete.
     * 
     * <p>
     * <em>Notice:</em>  
     * 
     * @param script script to execute. 
     * @return
     */
//    ExecutableResponse executeAsScript(String script);

    /**
     * Executes a process and allow the user to interact with it. Note that this will allow the session to exist indefinitely, and its connection is
     * not closed when {@link #disconnect()} is called.
     * 
     * @param command
     *            command line to invoke
     * @return reference to the running process
     */
    ExecutableChannel executableChannel(String command);

    /**
     * Opens a shell and allows the users to interact with it. Note that this method does not execute any command and that the session will exist
     * indefinitely until the user closes the connection.
     * 
     * @return reference to a shell.
     */
    OnlineChannel shell();

    void connect();

    void disconnect();

    void put(String path, String contents);

}
