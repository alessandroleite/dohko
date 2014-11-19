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
package org.excalibur.core.cloud.service.xmpp.server;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.vysper.mina.S2SEndpoint;
import org.apache.vysper.mina.TCPEndpoint;
import org.apache.vysper.storage.StorageProviderRegistry;
import org.apache.vysper.storage.inmemory.MemoryStorageProviderRegistry;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.vysper.xmpp.authorization.AccountCreationException;
import org.apache.vysper.xmpp.authorization.AccountManagement;
import org.apache.vysper.xmpp.modules.extension.xep0049_privatedata.PrivateDataModule;
import org.apache.vysper.xmpp.modules.extension.xep0054_vcardtemp.VcardTempModule;
import org.apache.vysper.xmpp.modules.extension.xep0119_xmppping.XmppPingModule;
import org.apache.vysper.xmpp.modules.extension.xep0202_entity_time.EntityTimeModule;
import org.apache.vysper.xmpp.server.ServerFeatures;
import org.apache.vysper.xmpp.server.XMPPServer;
import org.excalibur.core.cloud.service.xmpp.JID;
import org.excalibur.core.cloud.service.xmpp.XMPPFailureException;
import org.excalibur.core.util.SystemUtils2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmppServer
{
    private static final Logger     LOG = LoggerFactory.getLogger(XmppServer.class.getName());
    
    private final XMPPServer        server_;
    private final AccountManagement accountManagement_;
    private final AtomicBoolean     isStarted = new AtomicBoolean(false);
    private final List<JID>         users_ = newCopyOnWriteArrayList();
    private final XmppConfiguration configuration_;
    
    
    public XmppServer (XmppConfiguration configuration)
    {
        checkState(configuration != null);
        this.configuration_ = configuration;
        this.server_ = new XMPPServer(configuration.getDomain());
        
        StorageProviderRegistry providerRegistry = new MemoryStorageProviderRegistry();
        this.accountManagement_ = (AccountManagement) providerRegistry.retrieve(AccountManagement.class);
        server_.setStorageProviderRegistry(providerRegistry);
        
        TCPEndpoint tcpEndpoint = new TCPEndpoint();
        tcpEndpoint.setPort(configuration.getTcpPort());
        S2SEndpoint s2sEndpoint = new S2SEndpoint();
        s2sEndpoint.setPort(configuration.getS2SPort());
        
        server_.addEndpoint(tcpEndpoint);
        server_.addEndpoint(s2sEndpoint);
        
        try
        {
            server_.setTLSCertificateInfo(configuration.getCertificate(), configuration.getPassword());
        }
        catch (FileNotFoundException e)
        {
            LOG.error("File {} not found! Trying to look for in application dir!", configuration.getCertificate().getPath());
            
            File certificateHomeDir = new File(SystemUtils2.getApplicationDataDir(), configuration.getCertificate().getName());
            
            if (certificateHomeDir.exists())
            {
                try
                {
                    server_.setTLSCertificateInfo(certificateHomeDir, configuration.getPassword());
                }
                catch (FileNotFoundException e1)
                {
                    throw new XMPPFailureException(e1.getMessage(), e1);
                }
            }
            else
            {
                throw new XMPPFailureException(e.getMessage(), e);
            }
        }
    }
    
//    public XmppServer (String domain, int tcpPort, int s2Port)
//    {
//        this
//        (
//                domain, 
//                getPropertyFile("org.excalibur.xmpp.certificate.file", "excalibur.jks"), 
//                getProperty("org.excalibur.xmpp.certificate.password", "excalibur"), 
//                tcpPort, 
//                s2Port
//        );
//    }
//    
//    public XmppServer (String domain)
//    {
//        this
//        (
//                domain, 
//                getIntegerProperty("org.excalibur.xmpp.server.tcp.port", 5222), 
//                getIntegerProperty("org.excalibur.xmpp.server.s2.port", 5269)
//        );
//    }
    
    public void start() throws Exception
    {
        if (this.isStarted.compareAndSet(false, true))
        {
            server_.start();
            
            LOG.info("XMPP server is running...");

            ServerFeatures serverFeatures = server_.getServerRuntimeContext().getServerFeatures();
            serverFeatures.setRelayingToFederationServers(true);

            server_.addModule(new XmppPingModule());
            server_.addModule(new EntityTimeModule());
            server_.addModule(new PrivateDataModule());
            server_.addModule(new VcardTempModule());
        }
    }
    
    public void stop()
    {
        if (this.isStarted.compareAndSet(true,false))
        {
            this.server_.stop();
        }
    }
    
    public void registerAccount(JID id, String password) throws AccountCreationException
    {
        if (!this.users_.contains(id))
        {
            users_.add(id);
            accountManagement_.addUser(EntityImpl.parseUnchecked(id.getId()), password);
        }
    }
    
    public List<JID> getUsers()
    {
        return Collections.unmodifiableList(users_);
    }
    
    public XmppConfiguration getConfiguration()
    {
        return this.configuration_;
    }
}
