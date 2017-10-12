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
package org.excalibur.core.cloud.service.xmpp.events;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import org.excalibur.core.cloud.service.xmpp.Presence;
import org.excalibur.core.cloud.service.xmpp.PresenceType;
import org.excalibur.core.cloud.service.xmpp.XMPPEvent;
import org.excalibur.core.cloud.service.xmpp.listeners.PresenceListener;
import org.excalibur.core.util.EventListener;

import com.google.common.base.MoreObjects;

public class PresenceEvent extends XMPPEvent<Presence, PresenceType>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -415742916717631791L;

    public PresenceEvent(Object source, Presence value, PresenceType type)
    {
        super(source, checkNotNull(value), checkNotNull(type));
    }

    public PresenceEvent(Object source, Presence value)
    {
        this(source, checkNotNull(value), value.getPresenceType());
    }

    @Override
    public void processListener(EventListener listener)
    {
        PresenceListener list = ((PresenceListener) listener);
        list.onPresenceChanged(this);
    }

    @Override
    public boolean isAppropriateListener(EventListener listener)
    {
        return listener instanceof PresenceListener;
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("type", getType())
        		.add("presence", getValue())
        		.omitNullValues()
        		.toString();
    }
}
