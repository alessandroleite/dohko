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

public class PresenceBuilder
{
    private PresenceType presenceType_;
    private PresenceShow presenceShow_;
    private String status_;
    private JID fromJid_;
    private JID toJid_;
    private String stanza_;

    public PresenceBuilder withPresenceType(PresenceType presenceType)
    {
        this.presenceType_ = presenceType;
        return this;
    }

    public PresenceBuilder withPresenceShow(PresenceShow presenceShow)
    {
        this.presenceShow_ = presenceShow;
        return this;
    }

    public PresenceBuilder withStatus(String status)
    {
        this.status_ = status;
        return this;
    }

    public PresenceBuilder withFromJid(JID fromJid)
    {
        this.fromJid_ = fromJid;
        return this;
    }
    
    public PresenceBuilder withFromJid(String jid)
    {
        return this.withFromJid(new JID(jid));
    }

    public PresenceBuilder withToJid(JID toJid)
    {
        this.toJid_ = toJid;
        return this;
    }
    
    public PresenceBuilder withToJid(String to)
    {
        return this.withToJid(new JID(to));
    }

    PresenceBuilder withStanza(String stanza)
    {
        this.stanza_ = stanza;
        return this;
    }

    public Presence build()
    {
        if (this.presenceType_ == null)
        {
            throw new IllegalStateException("Must have a type");
        }

        return new Presence(presenceType_, presenceShow_, status_, toJid_, fromJid_, stanza_);
    }

}
