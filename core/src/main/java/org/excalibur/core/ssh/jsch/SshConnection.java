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
package org.excalibur.core.ssh.jsch;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.ssh.jsch.JschSshClient.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Proxy;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.agentproxy.Connector;
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository;

public final class SshConnection implements Connection<Session>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SshConnection.class.getName());

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private HostAndPort hostAndPort_;
        private LoginCredentials loginCredentials_;
        private Optional<Proxy> proxy_ = Optional.absent();
        private int connectionTimeout_;
        private int sessionTimeout_;
        private Optional<Connector> agentConnector_;

        /**
         * @see SshConnection#getHostAndPort()
         */
        public Builder hostAndPort(HostAndPort hostAndPort)
        {
            this.hostAndPort_ = hostAndPort;
            return this;
        }

        /**
         * @see SshConnection#getLoginCredentials()
         */
        public Builder loginCredentials(LoginCredentials loginCredentials)
        {
            this.loginCredentials_ = loginCredentials;
            return this;
        }

        /**
         * @see SshConnection#getProxy()
         */
        public Builder proxy(Proxy proxy)
        {
            this.proxy_ = Optional.fromNullable(proxy);
            return this;
        }

        /**
         * @see SshConnection#getConnectionTimeout()
         */
        public Builder connectionTimeout(int connectTimeout)
        {
            this.connectionTimeout_ = connectTimeout;
            return this;
        }

        /**
         * @see SshConnection#getConnectTimeout()
         */
        public Builder sessionTimeout(int sessionTimeout)
        {
            this.sessionTimeout_ = sessionTimeout;
            return this;
        }

        public SshConnection build()
        {
            return new SshConnection(this.hostAndPort_, this.loginCredentials_, this.proxy_, this.connectionTimeout_, this.sessionTimeout_,
                    this.agentConnector_);
        }

        public Builder from(SshConnection in)
        {
            return hostAndPort(in.hostAndPort_).loginCredentials(in.loginCredentials_).proxy(in.proxy_.orNull())
                    .connectionTimeout(in.connectTimeout_).sessionTimeout(in.sessionTimeout_).agentConnector(in.agentConnector_);
        }

        public Builder agentConnector(Optional<Connector> agentConnector)
        {
            this.agentConnector_ = agentConnector;
            return this;
        }
    }

    private final AtomicBoolean connected_ = new AtomicBoolean(false);
    private final HostAndPort hostAndPort_;
    private final LoginCredentials loginCredentials_;
    private final Optional<Proxy> proxy_;
    private final int connectTimeout_;
    private final int sessionTimeout_;
    private final Optional<Connector> agentConnector_;

    private transient volatile Session session_;

    private SshConnection(HostAndPort hostAndPort, LoginCredentials loginCredentials, Optional<Proxy> proxy, int connectTimeout, int sessionTimeout,
            Optional<Connector> agentConnector)
    {
        this.hostAndPort_ = checkNotNull(hostAndPort);
        this.loginCredentials_ = checkNotNull(loginCredentials, " login for %s", hostAndPort);
        this.proxy_ = checkNotNull(proxy, " proxy for %s", hostAndPort);
        this.connectTimeout_ = checkNotNull(connectTimeout);
        this.sessionTimeout_ = checkNotNull(sessionTimeout);
        this.agentConnector_ = checkNotNull(agentConnector, "agent connector for %s", hostAndPort);
    }

    @Override
    public void clear()
    {
        if (connected_.compareAndSet(true, false))
        {
            session_.disconnect();
            session_ = null;

            LOGGER.info("ssh session closed on {}", hostAndPort_);
        }
    }

    @Override
    public Session create() throws Exception
    {
        if (connected_.compareAndSet(false, true))
        {
            JSch jsch = new JSch();
            session_ = jsch.getSession(loginCredentials_.getUser(), hostAndPort_.getHostText(), hostAndPort_.getPortOrDefault(22));

            if (sessionTimeout_ != 0)
            {
                session_.setTimeout(sessionTimeout_);
            }

            if (loginCredentials_.getPrivateKey() == null)
            {
                session_.setPassword(loginCredentials_.getPassword());
            }
            else if (loginCredentials_.hasUnencryptedPrivateKey())
            {
                jsch.addIdentity(loginCredentials_.getPrivateKey());
//                jsch.addIdentity(loginCredentials_.getUser(), loginCredentials_.getPrivateKey().getBytes(), null, new byte[0]);
            }
            else if (agentConnector_.isPresent())
            {
                JSch.setConfig("PreferredAuthentications", "publickey");
                jsch.setIdentityRepository(new RemoteIdentityRepository(agentConnector_.get()));
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session_.setConfig(config);

            if (proxy_.isPresent())
            {
                session_.setProxy(proxy_.get());
            }

            session_.connect(connectTimeout_);
            LOGGER.info("ssh session started on {}", hostAndPort_);
        }

        return session_;
    }

    /**
     * Returns <code>true</code> if there is a SSH session. In other words, if the method {@link #create()} was called and the connection is still
     * active.
     * 
     * @return <code>true</code> if there is a SSH session.
     */
    public boolean isConnected()
    {
        return this.connected_.get();
    }

    /**
     * @return the hostAndPort
     */
    public HostAndPort getHostAndPort()
    {
        return hostAndPort_;
    }

    /**
     * @return the loginCredentials
     */
    public LoginCredentials getLoginCredentials()
    {
        return loginCredentials_;
    }

    /**
     * @return the proxy
     */
    public Optional<Proxy> getProxy()
    {
        return proxy_;
    }

    /**
     * @return the connectTimeout
     */
    public int getConnectTimeout()
    {
        return connectTimeout_;
    }

    /**
     * @return the sessionTimeout
     */
    public int getSessionTimeout()
    {
        return sessionTimeout_;
    }

    /**
     * @return the agentConnector
     */
    public Optional<Connector> getAgentConnector()
    {
        return agentConnector_;
    }

    /**
     * Returns the current session or <code>null</code> if it is not connected.
     * 
     * @return the current session or <code>null</code> if it's not connected.
     */
    @Nullable
    public Session getSession()
    {
        return session_;
    }
    
    @Override
    public String toString()
    {
        return this.getClass().getName();
    }
}
