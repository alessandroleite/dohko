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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;

import org.excalibur.core.cloud.service.xmpp.events.PresenceEvent;
import org.excalibur.core.cloud.service.xmpp.listeners.XMPPListener;
import org.excalibur.core.util.SystemUtils2;
import org.excalibur.core.util.concurrent.Futures2;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;


final class XMPPServiceImpl implements XMPPService
{
    /**
     * The logger instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(XMPPServiceImpl.class.getName());
    
    /**
     * The XMPP connection configuration.
     */
    private final ConnectionConfiguration config_;
    
    /**
     * The registered listeners.
     */
    private final List<XMPPListener> registeredListeners_ = new CopyOnWriteArrayList<XMPPListener>();
    
    /**
     * Cache with the mapping of event, listener and its method to be executed when a given event happens. TODO: refactor to use the a cache system
     * with eviction policies.
     */
    private final ConcurrentMap<Class<XMPPEvent<?, ?>>, Future<Method>> cachedEventListenersMapping_ = new ConcurrentHashMap<Class<XMPPEvent<?, ?>>, Future<Method>>();
   
    /**
     * Manager responsible for sending the {@link XMPPEvent}s.
     */
    private final EventQueueManager eventManager_ = new EventQueueManager();
    
    /**
     * 
     */
    private volatile Session session_;

    /**
     * Creates an instance of the {@link XMPPService}.
     * <p>Note: This method only creates the object without starting a connection. To connect call the method {@link #connect(JID, String)}</P>
     * @param config The connection configuration.
     */
    XMPPServiceImpl(ConnectionConfiguration config)
    {
        this.config_ = config;
    }

    @Override
    public void connect(JID user, String password)
    {
        XMPPConnection connection = new XMPPConnection(this.config_);
        
        try
        {
            connection.connect();
            connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            connection.login(user.getId(), password);
            
            session_ = new Session(new AccountBuilder()
                    .jid(user)
                    .password(password)
                    .withAttribute("host", connection.getHost())
                    .withAttribute("serviceName", connection.getServiceName())
                    .withAttribute("port", String.valueOf(connection.getPort()))
                    .build(),
                    connection);
            
//            connection.addPacketListener(new IQListener(), new PacketFilter()
//            {
//                public boolean accept(Packet packet)
//                {
//                    System.out.println("IQ Listener: "+ packet);
//                    return packet instanceof IQ;
//                }
//            });
            
//            connection.addPacketInterceptor(new PacketInterceptor()
//            {
//                
//                @Override
//                public void interceptPacket(Packet packet)
//                {
//                    System.out.println("PacketInterceptor...:" + packet);
//                }
//            }, new PacketFilter()
//            {
//                @Override
//                public boolean accept(Packet packet)
//                {
//                    return packet instanceof org.jivesoftware.smack.packet.Presence;
//                }
//            });
            
            connection.addPacketListener(new PresenceListener(), new PacketFilter()
            {
                public boolean accept(Packet packet)
                {
                    return packet instanceof org.jivesoftware.smack.packet.Presence;
                }
            });
            
            eventManager_.start();
        }
        catch (XMPPException exception)
        {
            if (connection.isConnected())
            {
                connection.disconnect();
            }
            
            throw new XMPPFailureException(exception.getMessage(), exception.getCause())
                        .withStreamError(new StreamError(exception.getStreamError().getCode()));
        }
    }
    
    @Override
    public void disconnect()
    {
        if (this.isConnected())
        {
            org.jivesoftware.smack.packet.Presence unavailable = new org.jivesoftware.smack.packet.Presence(Type.unavailable);
            unavailable.setFrom(this.session_.getUser().getId().getId());
            
            this.session_.getConnection().disconnect(unavailable);
            this.eventManager_.stop();
        }
    }
    
    @Override
    public List<Presence> getPresence(JID ... jabbedIds)
    {
        List<Future<Presence>> tasks = new ArrayList<Future<Presence>>();
        
        FutureTask<Presence> ft;
        for (final JID jabberId : jabbedIds)
        {
            Callable<Presence> eval = new Callable<Presence>()
            {
                @Override
                public Presence call() throws Exception
                {
                    org.jivesoftware.smack.packet.Presence presence = session_.getConnection().getRoster().getPresence(jabberId.getId());
                    return convertSmackPresenceToExcaliburPresence(presence);
                }
            };
            tasks.add(ft = new FutureTask<Presence>(eval));
            ft.run();
        }
        
        List<Presence> presences = Futures2.getUnchecked(tasks);

        return Collections.unmodifiableList(presences);
    }
    

    @Override
    public void sendPresence(JID jabberId, PresenceType type, PresenceShow show, String status)
    {
        this.sendPresence(jabberId, type, show, status, session_.getUser().getId());
    }

    @Override
    public void sendPresence(JID jabberId, PresenceType type, PresenceShow show, String status, JID fromJid)
    {
        checkNotNull(jabberId);
        
        final Type presenceType = type == null ? Type.available : Type.valueOf(type.name().toLowerCase());
        final Mode presenceMode = show == null ? null : Mode.valueOf(show.name().toLowerCase());
        
        org.jivesoftware.smack.packet.Presence presence = new org.jivesoftware.smack.packet.Presence(presenceType, status, 1, presenceMode);
        presence.setFrom(fromJid == null ? null : fromJid.getId());
        presence.setTo(jabberId.getId());
        this.session_.getConnection().sendPacket(presence);
    }

    @Override
    public SendResponse sendMessage(Message message)
    {
        org.jivesoftware.smack.packet.Message.Type type = org.jivesoftware.smack.packet.Message.Type.chat;
        org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message();
        msg.setFrom(message.getFromJid().getId());
        msg.setBody(message.getBody());
        msg.setType(type);
        
        for (JID to : message.getRecipientJids())
        {
            msg.setTo(to.getId());
            session_.getConnection().sendPacket(msg);
        }
        
        return null;
    }

    @Override
    public void subscribe(JID fromJid, JID toJid)
    {
        org.jivesoftware.smack.packet.Presence presence = new org.jivesoftware.smack.packet.Presence(Type.subscribe);
        presence.setFrom(fromJid == null ? this.session_.getUser().getName() : fromJid.getId());
        presence.setTo(toJid.getId());
        
        session_.getConnection().sendPacket(presence);
    }
    
    @Override
    public void subscribe(JID toJid)
    {
        this.subscribe(this.session_.getUser().getId(), toJid);
    }
    
    @Override
    public void sendInvitation(JID jabberId, JID fromJid)
    {
        this.subscribe(fromJid, jabberId);
    }

    @Deprecated
    @Override
    public boolean createAccount(Account account)
    {
        boolean created = false;
        AccountManager accountManager = this.session_.getConnection().getAccountManager();
        
        if (accountManager.supportsAccountCreation())
        {
            try
            {
                accountManager.createAccount(account.getId().getId(), account.getPassword());
                created = true;
            }
            catch (XMPPException ex)
            {
                LOG.error("Error in creating user's account: {}", ex.getMessage(), ex);
                throw new XMPPFailureException(ex.getMessage(), ex.getCause()).withStreamError(new StreamError(ex.getStreamError().getCode()));
            }
        }
        
        return created;
    }
   
    @Override
    public void registerListener(XMPPListener... listeners)
    {
        if (listeners != null)
        {
            for (XMPPListener listener : listeners)
            {
                if (listener != null)
                {
                    this.registeredListeners_.add(listener);
                }
            }
        }
    }
    
    // ****************************************//
    //            Utilities methods           *//
    // ****************************************//
    
    @Override
    public boolean isConnected()
    {
        return session_ != null && session_.getConnection().isConnected();
    }
    
    // ****************************************//
    //           private methods              *//
    // ****************************************//
    
    private Presence convertSmackPresenceToExcaliburPresence(org.jivesoftware.smack.packet.Presence presence)
    {
        return new PresenceBuilder()
                .withFromJid(new JID(presence.getFrom()))
                .withToJid(new JID(presence.getTo()))
                .withPresenceType(PresenceType.valueOf(presence.getType().name().toUpperCase()))
                .withStatus(presence.getStatus())
                .withPresenceShow(presence.getMode() != null ? PresenceShow.valueOf(presence.getMode().name().toUpperCase()): null)
                .build();
    }

    @Override
    public void unregisterListener(XMPPListener... listeners)
    {
        if (listeners != null)
        {
            this.registeredListeners_.removeAll(Arrays.asList(listeners));
        }
    }

    
    // ****************************************//
    //             Inner classes              *//
    // ****************************************//
    
    class EventQueueManager
    {
        /**
         * The XMPP event queue.
         */
        private final BlockingQueue<XMPPEvent<?, ?>> eventQueue_ = new ArrayBlockingQueue<XMPPEvent<?, ?>>
            (SystemUtils2.getIntegerProperty("excalibur.xmpp.event.queue.maximum.size", 100));
        
        private ListeningExecutorService executor_ = MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        
        void start()
        {
            Thread th = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while (!executor_.isShutdown())
                    {
                        try
                        {
                            XMPPEvent<?, ?> event = eventQueue_.take();
                            executor_.execute(new NotifyXmppEventTask(event));
                        }
                        catch (InterruptedException e)
                        {
                            LOG.error("Error in the event queue manager. Message: " + e.getMessage(), e);
                            
                        }catch(RejectedExecutionException e)
                        {
                            LOG.error("The event cannot be accepted!", e);
                        }
                    }
                }
            }, "XMPP-EVENT-QUEUE-MANAGER");
            th.setDaemon(true);
            th.start();
        }
        
        void queue(XMPPEvent<?, ?> event)
        {
            this.eventQueue_.offer(event);
        }
        
        void stop()
        {
            executor_.shutdown();
        }
    }
    
    
    class NotifyXmppEventTask implements Runnable
    {
        private final XMPPEvent<?, ?> event_;
        private final Class<XMPPEvent<?, ?>> clazz;
        
        @SuppressWarnings("unchecked")
        NotifyXmppEventTask(XMPPEvent<?, ?> event)
        {
            this.event_ = event;
            clazz = (Class<XMPPEvent<?, ?>>) event_.getClass();
        }
        
        @Override
        public void run()
        {
            for (final XMPPListener listener : registeredListeners_)
            {
                if (event_.isAppropriateListener(listener))
                {
                    Future<Method> f = cachedEventListenersMapping_.get(clazz);

                    if (f == null)
                    {
                        Callable<Method> eval = new Callable<Method>()
                        {
                            @Override
                            public Method call() throws Exception
                            {
                                Method method = null;
                                
                                for (Method meth : listener.getClass().getDeclaredMethods())
                                {
                                    if (meth.getParameterTypes() != null && meth.getParameterTypes().length == 1 && 
                                        meth.getParameterTypes()[0].equals(event_.getClass()))
                                    {
                                        meth.setAccessible(true);
                                        method = meth;
                                        break;
                                    }
                                }
                                return method;
                            }
                        };

                        FutureTask<Method> ft = new FutureTask<Method>(eval);
                        f = cachedEventListenersMapping_.putIfAbsent(clazz, ft);
                        
                        if (f == null)
                        {
                            f = ft;
                            ft.run();
                        }
                    }
                    
                    try
                    {
                        Method method = f.get();
                        checkNotNull(method).invoke(listener, event_);
                    }
                    catch (CancellationException e)
                    {
                        cachedEventListenersMapping_.remove(clazz, f);
                    }
                    catch (Exception e)
                    {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    class IQListener implements PacketListener
    {
        @Override
        public void processPacket(Packet packet)
        {
            IQ iq = (IQ) packet;
            String iqString = iq.toString();
            LOG.debug("T {} IQ: {} : {}", System.currentTimeMillis(), iqString, iq.toXML());
        }
    }
    
    class PresenceListener implements PacketListener
    {
        @Override
        public void processPacket(Packet packet)
        {
            org.jivesoftware.smack.packet.Presence presence = (org.jivesoftware.smack.packet.Presence) packet;
            
            String iqString = presence.toString();
            final PacketExtension extension = presence.getExtension("http://jabber.org/protocol/caps");
            
            if (extension != null)
            {
                LOG.debug("T {} Press: {} : {}", System.currentTimeMillis(), iqString, presence.toXML());
            }
            
            eventManager_.queue(new PresenceEvent(this, convertSmackPresenceToExcaliburPresence(presence)));
        }
    }
}
