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

import java.util.List;

import org.excalibur.core.cloud.service.xmpp.listeners.XMPPListener;

public interface XMPPService
{

    /**
     * Starts a connection with the XMPP server.
     * 
     * @param user
     *            The node to connect.
     * @param password
     *            The password.
     * @throws XMPPFailureException
     *             if the XMPP server is down or the connection was refused.
     */
    void connect(JID user, String password);

    /**
     * Disconnect from the XMPP server.
     */
    void disconnect();

    /**
     * Returns <code>true</code> if there is an active connection. Otherwise, return <code>false</code>.
     * 
     * @return <code>true</code> iff there is an active connection.
     */
    boolean isConnected();

    /**
     * Given a JID, look up the user's status and return it.
     * 
     * @param jabbedIds
     *            A iterator with the JIDs for users whose presence should be fetched.
     * @return A read-only list with the {@link Presence} of the given users.
     */
    List<Presence> getPresence(JID... jabbedIds);

    /**
     * Given a JID, type and optional show and status value, sends a presence packet.
     * 
     * @param jabberId
     *            JID of the user to send presence to.
     * @param type
     *            Type of presence. <code>null</code> means available.
     * @param show
     *            Value for show element. Can be <code>null</code>
     * @param status
     *            String for status element. Can be <code>null</code>.
     * @throws IllegalArgumentException
     *             If the one or more of the parameters are not valid.
     */
    void sendPresence(JID jabberId, PresenceType type, PresenceShow show, java.lang.String status);

    /**
     * Given a JID, type and optional show and status value, sends a presence packet.
     * 
     * @param jabberId
     *            JID of the user to send presence to.
     * @param type
     *            Type of presence. <code>null</code> means available.
     * @param show
     *            Value for show element. Can be <code>null</code>
     * @param status
     *            String for status element. Can be <code>null</code>.
     * @param fromJid
     *            JID of the chat bot. Can be <code>null</code>.
     * @throws IllegalArgumentException
     *             If the one or more of the parameters are not valid.
     */
    void sendPresence(JID jabberId, PresenceType type, PresenceShow show, java.lang.String status, JID fromJid);

    /**
     * Given a JID, sends a chat invitation. Uses a custom JID as the sender.
     * 
     * @param jabberId
     *            The user to send a chat invitation.
     * @param fromJid
     *            JID of the chat bot. Can be <code>null</code>.
     * @throws IllegalArgumentException
     *             if the id is not valid
     */
    void sendInvitation(JID jabberId, JID fromJid);

    /**
     * Sends a message for the provided JIDs.
     * 
     * @param message
     *            The message to send.
     * @return The response of the send operation.
     * @throws IllegalArgumentException
     *             If the {@link Message} is invalid.
     */
    SendResponse sendMessage(Message message);

    /**
     * Subscribes the active user to the give jid.
     * 
     * @param toJid
     *            The JID that will accept the subscription.
     */
    void subscribe(JID toJid);

    /**
     * Subscribes the JIDs.
     * 
     * @param fromJid
     *            The JID that to be subscribed to. Can be <code>null</code>.
     * @param toJid
     *            The JID that will accept the subscription.
     */
    void subscribe(JID fromJid, JID toJid);

    /**
     * Creates a JID's account.
     * 
     * @param account
     *            The account to be created. Might not be <code>null</code>.
     * @return <code>true</code> if the account was created, <code>false</code> if the XMPP server does not support this feature.
     * @throws NullPointerException
     *             If the jabber's id or the password is <code>null</code>.
     * @throws IllegalArgumentException
     *             If the jabber's id is invalid. In other words, if its domains is different from the server.
     * @throws XMPPFailureException
     *             If the server thrown an error.
     */
    boolean createAccount(Account account);

    /**
     * Registers a given listener. Only non-null listeners are registered.
     * 
     * @param listeners
     *            Listener to register.
     */
    void registerListener(XMPPListener... listeners);

    /**
     * Remove the listeners.
     * 
     * @param listeners
     *            Listeners to remove.
     */
    void unregisterListener(XMPPListener... listeners);

}
