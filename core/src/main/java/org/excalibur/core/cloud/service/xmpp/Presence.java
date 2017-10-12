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

import com.google.common.base.MoreObjects;

/**
 * Represents presence information returned by the server.
 */
public final class Presence implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization. 
     */
    private static final long serialVersionUID = 6107240559695739239L;
    
    private final PresenceType presenceType_;
    private final PresenceShow presenceShow_;
    private final String status_;
    private final JID fromJid_;
    private final JID toJid_;
    private final String stanza_;

    Presence(JID toJid, JID fromJid)
    {
        this.presenceType_ = PresenceType.AVAILABLE;
        this.presenceShow_ = null;
        this.fromJid_ = fromJid;
        this.toJid_ = toJid;
        stanza_ = null;
        status_ = null;
    }

    Presence(PresenceType type, PresenceShow show, String status, JID toJid, JID fromJid, String stanza)
    {
        this.presenceType_ = type;
        this.presenceShow_ = show;
        this.status_ = status;
        this.toJid_ = toJid;
        this.fromJid_ = fromJid;
        this.stanza_ = stanza;
    }

    /**
     * @return the presenceType
     */
    public PresenceType getPresenceType()
    {
        return presenceType_;
    }

    /**
     * @return the presenceShow
     */
    public PresenceShow getPresenceShow()
    {
        return presenceShow_;
    }

    /**
     * @return the isAvailable
     */
    public boolean isAvailable()
    {
        return presenceType_ != PresenceType.UNAVAILABLE;
    }

    /**
     * @return the status
     */
    public String getStatus()
    {
        return status_;
    }

    /**
     * @return the fromJid
     */
    public JID getFromJid()
    {
        return fromJid_;
    }

    /**
     * @return the toJid
     */
    public JID getToJid()
    {
        return toJid_;
    }

    /**
     * @return the stanza
     */
    public String getStanza()
    {
        return stanza_;
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("from", getFromJid())
                .add("to", getToJid())
                .add("presenceType", getPresenceType())
                .add("presenceShow", getPresenceShow())
                .add("stanza", getStanza())
                .omitNullValues()
                .toString();
    }
}
