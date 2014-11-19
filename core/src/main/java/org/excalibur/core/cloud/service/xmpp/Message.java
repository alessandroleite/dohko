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

import java.io.Serializable;

/**
 * Class that represents an XMPP message.
 */
public final class Message implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -5551966899184247976L;

    private final JID fromJid_;
    private final String body_;
    private final MessageType messageType_;
    private final JID[] recipientJids_;

    Message(JID fromJid, String body, MessageType messageType, JID... recipientJids)
    {
        this.fromJid_ = fromJid;
        this.body_ = body;
        this.messageType_ = messageType;
        this.recipientJids_ = recipientJids;
    }

    /**
     * @return the fromJid
     */
    public JID getFromJid()
    {
        return fromJid_;
    }

    /**
     * @return the body
     */
    public String getBody()
    {
        return body_;
    }

    /**
     * @return the messageType
     */
    public MessageType getMessageType()
    {
        return messageType_;
    }

    /**
     * @return the recipientJids
     */
    public JID[] getRecipientJids()
    {
        return recipientJids_;
    }

    @Override
    protected Message clone()
    {
        Message clone;

        try
        {
            clone = (Message) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Message(this.fromJid_, this.body_, this.messageType_, this.recipientJids_);
        }
        return clone;
    }

}
