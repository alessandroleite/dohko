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

public final class MessageBuilder
{
    private MessageType messageType_;
    private String messageBody_;
    private JID fromJid_;
    private JID[] recipientJids_;

    public MessageBuilder withMessageType(MessageType type)
    {
        this.messageType_ = type;
        return this;
    }

    public MessageBuilder withBody(java.lang.String body)
    {
        this.messageBody_ = body;
        return this;
    }

    public MessageBuilder withFromJid(JID fromJid)
    {
        this.fromJid_ = fromJid;
        return this;
    }

    public MessageBuilder withRecipientJids(JID ... recipientJids)
    {
        recipientJids_ = new JID[recipientJids.length];
        
        for (int i = 0; i < recipientJids.length; i++)
        {
            if (recipientJids != null)
            {
                this.recipientJids_[i] = recipientJids[i];
            }
        }
        
        return this;
    }

    public Message build()
    {
        if (messageType_ == null)
        {
            messageType_ = MessageType.NORMAL;
        }
        
        if (recipientJids_ == null || recipientJids_.length == 0)
        {
            throw new IllegalStateException("At least one recipient is required!");
        }
        
        return new Message(fromJid_, messageBody_, messageType_, recipientJids_);
    }
}
