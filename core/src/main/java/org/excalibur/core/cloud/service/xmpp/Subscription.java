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

/**
 * Represents an incoming subscription stanza from the server.
 */
public final class Subscription
{
    private final JID fromJid_;

    private final JID toJid_;

    private final SubscriptionType subscriptionType_;

    private final String stanza_;

    Subscription(JID fromJid, JID toJid, SubscriptionType subscriptionType, String stanza)
    {
        super();
        this.fromJid_ = fromJid;
        this.toJid_ = toJid;
        this.subscriptionType_ = subscriptionType;
        this.stanza_ = stanza;
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
     * @return the subscriptionType
     */
    public SubscriptionType getSubscriptionType()
    {
        return subscriptionType_;
    }

    /**
     * @return the stanza
     */
    public String getStanza()
    {
        return stanza_;
    }

}
