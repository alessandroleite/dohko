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
package org.excalibur.core;

import java.util.Date;
import java.util.Random;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.packet.Time;
import org.jivesoftware.smackx.packet.Version;

public class Client
{
    public static XMPPConnection connect(String host) throws XMPPException
    {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(host, 5222, "excalibur.org");
        connectionConfiguration.setCompressionEnabled(false);
        connectionConfiguration.setSelfSignedCertificateEnabled(true);
        connectionConfiguration.setExpiredCertificatesCheckEnabled(false);
        connectionConfiguration.setDebuggerEnabled(false);
        connectionConfiguration.setSASLAuthenticationEnabled(true);
        connectionConfiguration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        //XMPPConnection.DEBUG_ENABLED = false;
        
        XMPPConnection connection = new XMPPConnection(connectionConfiguration);
        connection.connect();
        
        return connection;
    }

    static class IQListener implements PacketListener
    {
        public void processPacket(Packet packet)
        {
            IQ iq = (IQ) packet;
            String iqString = iq.toString();
            System.out.println("T" + System.currentTimeMillis() + " IQ: " + iqString + ": " + iq.toXML());
        }
    }

    static class PresenceListener implements PacketListener
    {
        public void processPacket(Packet packet)
        {
            Presence presence = (Presence) packet;
            String iqString = presence.toString();
            final PacketExtension extension = presence.getExtension("http://jabber.org/protocol/caps");
            
            if (extension != null)
                System.out.println("T" + System.currentTimeMillis() + " Pres: " + iqString + ": " + presence.toXML());
        }
    }

    public static void main(String[] args) throws XMPPException
    {
        String me = args.length > 0 ? args[0] : "coordinator";
        String to = args.length < 2 ? null : args[1];

        try
        {
            XMPPConnection connection = connect("192.168.1.93");

            connection.login(me + "@excalibur.org", "passwd");
            connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);

            connection.addPacketListener(new IQListener(), new PacketFilter()
            {
                public boolean accept(Packet packet)
                {
                    return packet instanceof IQ;
                }
            });

            connection.addPacketListener(new PresenceListener(), new PacketFilter()
            {
                public boolean accept(Packet packet)
                {
                    return packet instanceof Presence;
                }
            });

            Chat chat = null;
            
            if (to != null)
            {
                Presence presence = new Presence(Presence.Type.subscribe);
                presence.setFrom(connection.getUser());
                String toEntity = to + "@excalibur.org";
                presence.setTo(toEntity);
                connection.sendPacket(presence);

                chat = connection.getChatManager().createChat(toEntity, new MessageListener()
                {
                    public void processMessage(Chat inchat, Message message)
                    {
                        System.out.println("log received message: " + message.getBody());
                    }
                });
            }

            connection.sendPacket(new Presence(Presence.Type.available, "pommes", 1, Presence.Mode.available));

            Thread.sleep(1000);

            // query server version
            sendIQGetWithTimestamp(connection, new Version());

            // query server time
            sendIQGetWithTimestamp(connection, new Time());

            
            while (to != null)
            {
                chat.sendMessage("Hello " + to + " at " + new Date());
                try
                {
                    Thread.sleep((new Random().nextInt(15) + 1) * 1000);
                }
                catch (InterruptedException e)
                {
                    ;
                }
            }
             

            for (int i = 0; i < 10; i++)
            {
                connection.sendPacket(new Presence(Presence.Type.available, "pommes", 1, Presence.Mode.available));
                try
                {
                    Thread.sleep((new Random().nextInt(15) + 10) * 1000);
                }
                catch (InterruptedException e)
                {
                    ;
                }
                connection.sendPacket(new Presence(Presence.Type.available, "nickes", 1, Presence.Mode.away));
                try
                {
                    Thread.sleep((new Random().nextInt(15) + 10) * 1000);
                }
                catch (InterruptedException e)
                {
                    ;
                }
            }

            for (int i = 0; i < 2000; i++)
            {
                try
                {
                    Thread.sleep(500);
                }
                catch (InterruptedException e)
                {
                    ;
                }
            }

            connection.disconnect();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try
            {
                Thread.sleep(120 * 1000);
            }
            catch (InterruptedException ie)
            {
                ;
            }
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }
        System.exit(0);
    }

    private static void sendIQGetWithTimestamp(XMPPConnection connection, IQ iq)
    {
        iq.setType(IQ.Type.GET);
        connection.sendPacket(iq);
        System.out.println("T" + System.currentTimeMillis() + " IQ request sent");
    }
}
