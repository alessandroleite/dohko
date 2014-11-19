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
package org.excalibur.service.xmpp.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;

import org.apache.vysper.xmpp.authorization.AccountCreationException;
import org.excalibur.core.cloud.service.xmpp.Contacts;
import org.excalibur.core.cloud.service.xmpp.IXMPPServiceFactoryProvider;
import org.excalibur.core.cloud.service.xmpp.JID;
import org.excalibur.core.cloud.service.xmpp.XMPPConnectionConfigurationBuilder;
import org.excalibur.core.cloud.service.xmpp.XMPPService;
import org.excalibur.core.cloud.service.xmpp.listeners.XMPPListener;
import org.excalibur.core.cloud.service.xmpp.server.XmppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static org.excalibur.core.cloud.service.xmpp.Contacts.*;


public class XmppService
{
    private static final Logger                      LOG = LoggerFactory.getLogger(XmppService.class.getName());
    
    private final XmppServer                         xmppServer_;
    private final ConcurrentMap<JID,XMPPService>     xmppSessions = new ConcurrentHashMap<JID, XMPPService>();
    private final XMPPConnectionConfigurationBuilder xmppConnectionConfig_;
    
    public XmppService(@Nonnull XmppServer server, @Nonnull String host) throws Exception
    {
        checkNotNull(server);
        checkState(!isNullOrEmpty(host));
        
        xmppConnectionConfig_ = new XMPPConnectionConfigurationBuilder()
                .serviceName(server.getConfiguration().getDomain())
                .host(host)
                .enableCompression()
                .disableExpiredCertificatesCheck()
                .enableSelfSignedCertificate()
                .enableSAASLAuthentication();
        
        xmppServer_ = server;
    }
    
    public XmppService registerAccount(@Nonnull String name, @Nonnull String password)
    {
        JID jid = toJid(name);
        this.registerAccount(jid, password);
        
        return this;
    }
    
    public XmppService registerAccount(@Nonnull JID jid)
    {
        this.registerAccount(jid, System.getProperty("org.excalibur.xmpp.user.default.password", "excalibur"));
        
        return this;
    }
    
    public XmppService registerAccount(@Nonnull JID jid, @Nonnull String password)
    {
        try
        {
            this.xmppServer_.registerAccount(jid, password);
            this.connect(jid, password);
            
            for (JID user : this.xmppServer_.getUsers())
            {
                if (!user.equals(jid))
                {
                    this.xmppSessions.get(jid).sendInvitation(user, jid);
                }
            }
        }
        catch (AccountCreationException e)
        {
            LOG.error(e.getMessage(), e);
        }
        
        return this;
    }
    
    public XmppService registerListener(JID jid, XMPPListener listener)
    {
        XMPPService xmpp = this.xmppSessions.get(jid);
        
        if (xmpp != null && listener != null)
        {
            xmpp.registerListener(listener);
        }
        
        return this;
    }
    
    public XmppService removeListener(JID jid, XMPPListener listener)
    {
        XMPPService xmpp = this.xmppSessions.get(jid);
        
        if (xmpp != null)
        {
            xmpp.unregisterListener(listener);
        }
        
        return this;
    }
    
    public XmppService connect(JID jid, String password)
    {
        checkNotNull(jid, "JID account may not be null");
        
        XMPPService xmppService = this.xmppSessions.get(jid);
        
        if (xmppService == null)
        {
            xmppService = new IXMPPServiceFactoryProvider().getXMPPService(xmppConnectionConfig_);
            
            this.xmppSessions.put(jid, xmppService);
//            xmppService.registerListener(new PresenceListenerImpl());
        }
        
        if (!xmppService.isConnected())
        {
            xmppService.connect(jid, password);    
        }
        
        return this;
    }
    
    public XmppService disconnect(String jid)
    {
        this.disconnect(toJid(jid));
        
        return this;
    }
    
    public XmppService disconnect(JID jid)
    {
        XMPPService session = this.xmppSessions.remove(jid);
        
        if (session != null)
        {
            session.disconnect();
        }
        
        return this;
    }

    public Contacts getUsers()
    {
        return  newContacts(this.xmppServer_.getUsers());
    }
    
    public XmppService closeAllSessions()
    {
        for(XMPPService service: this.xmppSessions.values())
        {
            service.disconnect();
        }
        
        return this;
    }
    
    public XmppService closeAndRemoveAllSessions()
    {
        closeAllSessions();
        this.xmppSessions.clear();
        
        return this;
    }
    
    private JID toJid(String name)
    {
        JID jid;
        
        if (name.indexOf("@") < 0)
        {
           jid = new JID(String.format("%s@%s", name, xmppConnectionConfig_.getServiceName()));
        }
        else 
        {
            jid = new JID(name);
        }
        
        return jid;
    }
}
