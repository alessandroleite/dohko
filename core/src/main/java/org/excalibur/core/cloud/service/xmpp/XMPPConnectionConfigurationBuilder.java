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
package org.excalibur.core.cloud.service.xmpp;

import org.excalibur.core.util.SystemUtils2;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class XMPPConnectionConfigurationBuilder
{
    private String serviceName_;
    private String host_;
    private int port_ = SystemUtils2.getIntegerProperty("org.excalibur.xmpp.server.tcp.port", 5222);

    private boolean compressionEnabled_;
    private boolean selfSignedCertificateEnabled_;
    private boolean expiredCertificatesCheckEnabled_;
    
    private boolean saaslAuthenticationEnabled_ = true;

    /**
     * A flag that indicates if the roster must be loaded from the server when logging in. This is the common behaviour for clients but sometimes
     * clients may want to differ this or just never do it if not interested in rosters.
     */
    private boolean loadRosterAtLogin_ = true;

    /**
     * Sets if an initial available presence will be sent to the server. By default an available presence will be sent to the server indicating that
     * this presence is not online and available to receive messages. If you want to log in without being 'noticed' then pass a false value.
     */
    private boolean sendPresence_ = true;
    
    private SecurityMode securityMode_;

    public XMPPConnectionConfigurationBuilder serviceName(String serviceName)
    {
        this.serviceName_ = serviceName;
        return this;
    }

    public XMPPConnectionConfigurationBuilder host(String host)
    {
        this.host_ = host;
        return this;
    }

    public XMPPConnectionConfigurationBuilder port(int port)
    {
        this.port_ = port;
        return this;
    }

    public XMPPConnectionConfigurationBuilder securityMode(SecurityMode mode)
    {
        this.securityMode_ = mode;
        return this;
    }

    public XMPPConnectionConfigurationBuilder enableCompression()
    {
        this.compressionEnabled_ = true;
        return this;
    }

    public XMPPConnectionConfigurationBuilder enableSelfSignedCertificate()
    {
        this.selfSignedCertificateEnabled_ = true;
        return this;
    }

    public XMPPConnectionConfigurationBuilder enableExpiredCertificatesCheck()
    {
        this.expiredCertificatesCheckEnabled_ = true;
        return this;
    }

    public XMPPConnectionConfigurationBuilder enableSAASLAuthentication()
    {
        this.saaslAuthenticationEnabled_ = true;
        return this;
    }
    
    public XMPPConnectionConfigurationBuilder enableRosterLoadAtLogin()
    {
        this.loadRosterAtLogin_ = true;
        return this;
    }
    
    public XMPPConnectionConfigurationBuilder enableSendPresence()
    {
        this.sendPresence_ = true;
        return this;
    }

    public ConnectionConfiguration build()
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(this.host_) && !Strings.isNullOrEmpty(this.serviceName_));
        ConnectionConfiguration configuration = new ConnectionConfiguration(host_, port_, serviceName_);

        configuration.setCompressionEnabled(this.compressionEnabled_);
        configuration.setSelfSignedCertificateEnabled(this.selfSignedCertificateEnabled_);
        configuration.setExpiredCertificatesCheckEnabled(this.expiredCertificatesCheckEnabled_);
        configuration.setSASLAuthenticationEnabled(this.saaslAuthenticationEnabled_);
        configuration.setSecurityMode(this.securityMode_);
        configuration.setRosterLoadedAtLogin(this.loadRosterAtLogin_);
        configuration.setSendPresence(this.sendPresence_);

        return configuration;

    }

    public XMPPConnectionConfigurationBuilder disableExpiredCertificatesCheck()
    {
        this.expiredCertificatesCheckEnabled_ = false;
        return this;
    }
    
    public XMPPConnectionConfigurationBuilder disableRosterLoadAtLogin()
    {
        this.loadRosterAtLogin_ = false;
        return this;
    }
    
    public XMPPConnectionConfigurationBuilder disableSendPresence()
    {
        this.sendPresence_ = false;
        return this;
    }
    
    public String getServiceName()
    {
        return serviceName_;
    }
}
