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

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.ssh.jsch.JschSshClient;
import org.excalibur.core.util.BackoffLimitedRetryHandler;
import org.excalibur.core.util.SystemUtils2;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.Connector;
import com.jcraft.jsch.agentproxy.ConnectorFactory;

public class SshClientFactory
{
    public static SshClient.Factory defaultSshClientFactory()
    {
        return new SshClient.Factory()
        {
            int timeout = SystemUtils2.getIntegerProperty("org.excalibur.ssh.default.connection.timeout.ms", 60000);

            Optional<Connector> agentConnector = getAgentConnector();

            @Override
            public boolean isAgentAvailable()
            {
                return agentConnector.isPresent();
            }

            @Override
            public SshClient create(HostAndPort socket, LoginCredentials credentials)
            {
                return new JschSshClient(socket, credentials, timeout, agentConnector, new BackoffLimitedRetryHandler());
            }

            Optional<Connector> getAgentConnector()
            {
                try
                {
                    return Optional.of(ConnectorFactory.getDefault().createConnector());
                }
                catch (final AgentProxyException e)
                {
                    return Optional.absent();
                }
            }
        };
    }

}
