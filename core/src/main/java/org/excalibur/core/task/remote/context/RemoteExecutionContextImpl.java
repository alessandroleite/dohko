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
package org.excalibur.core.task.remote.context;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.executor.Context;
import org.excalibur.core.executor.ExecutionEnvironment;
import org.excalibur.core.task.impl.AbstractContextBase;
import org.excalibur.core.task.remote.RemoteExecutionContext;

public class RemoteExecutionContextImpl extends AbstractContextBase implements RemoteExecutionContext
{
    private final RemoteExecutionEnvironment executionEnvironment;

    public RemoteExecutionContextImpl(Context parent, LoginCredentials loginCredentials, VirtualMachine localNode, VirtualMachine remoteNode)
    {
        super(parent);
        this.executionEnvironment = new RemoteExecutionEnvironment(remoteNode, localNode, loginCredentials);
    }

    @Override
    public ExecutionEnvironment getExecutionEnvironment()
    {
        return executionEnvironment;
    }

    @Override
    public VirtualMachine getRemoteHost()
    {
        return executionEnvironment.getRemoteHost();
    }

    @Override
    public VirtualMachine getLocalHost()
    {
        return executionEnvironment.getLocation();
    }

    @Override
    public LoginCredentials getLoginCredentials()
    {
        return executionEnvironment.getLoginCredentials();
    }

    static class RemoteExecutionEnvironment implements ExecutionEnvironment
    {
        private final VirtualMachine remoteNode;
        private final VirtualMachine localNode;
        private final LoginCredentials loginCredentials;
        private final Map<String, Serializable> contextVariables;
        
        public RemoteExecutionEnvironment(VirtualMachine remoteNode, VirtualMachine localNode, LoginCredentials loginCredentials)
        {
            this.remoteNode = checkNotNull(remoteNode);
            this.localNode = checkNotNull(localNode);
            this.loginCredentials = checkNotNull(loginCredentials);
            
            checkState(!isNullOrEmpty(remoteNode.getConfiguration().getPublicDnsName()));
            
            contextVariables = new HashMap<String, Serializable>(System.getenv());
        }

        @Override
        public Map<String, Serializable> getEnviromentMap()
        {
            return contextVariables;
        }

        @Override
        public VirtualMachine getLocation()
        {
            return localNode;
        }

        @Override
        public File getWorkingDirectory()
        {
            return null;
        }

        public VirtualMachine getRemoteHost()
        {
            return remoteNode;
        }
        
        public LoginCredentials getLoginCredentials()
        {
            return loginCredentials;
        }
    }

    @Override
    public UserRepository getUserRepository()
    {
        return null;
    }
}
