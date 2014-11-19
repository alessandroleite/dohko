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
package org.excalibur.core.task.remote;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.executor.task.ExecutionContext;

public interface RemoteExecutionContext extends ExecutionContext
{

    /**
     * <p>
     * Returns a reference to the remote node.
     * <p>
     * <strong>Notice:</strong> This method does not check if the remote is available. It only returns the reference for it.
     * 
     * @return The reference to the remote node. It might not be <code>null</code>.
     */
    VirtualMachine getRemoteHost();

    /**
     * <p>
     * Returns the reference for the local host. It must the same as {@link getExecutionEnvironment()#getLocalHost()}.
     * 
     * <p>
     * <strong>Notice:</strong> The classes that implement this interface have to guarantee that the reference returned by this method is the same as
     * the returned by {@link #getExecutionEnvironment()}.
     * 
     * @return The reference to the local node. It might not be <code>null</code>.
     */
    VirtualMachine getLocalHost();

    /**
     * Returns the credential used to connect to the remote host.
     * 
     * @return The credential to connect to the remote host. It must not be <code>null</code>.
     */
    LoginCredentials getLoginCredentials();

}
