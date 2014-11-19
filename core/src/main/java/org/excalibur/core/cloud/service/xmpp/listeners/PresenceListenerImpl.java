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
package org.excalibur.core.cloud.service.xmpp.listeners;

import org.excalibur.core.cloud.service.xmpp.PresenceShow;
import org.excalibur.core.cloud.service.xmpp.events.PresenceEvent;

public class PresenceListenerImpl implements PresenceListener
{
    @Override
    public void onPresenceChanged(PresenceEvent event)
    {
        System.out.println(event.getType() + " " + event.getValue().getPresenceShow() + " " + event.getValue().getFromJid() + " " +
                event.getValue().getStatus());

        switch (event.getType())
        {
        case AVAILABLE:
            System.out.println("on available!");
            break;
        case UNAVAILABLE:
            System.out.println("on unavailable!");
            break;
        default:
            if (PresenceShow.AWAY.equals(event.getValue().getPresenceShow()))
            {
                System.out.println("on away!");
            }
            break;
        }
    }
}
