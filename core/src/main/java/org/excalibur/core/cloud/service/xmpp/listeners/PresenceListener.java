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

import org.excalibur.core.cloud.service.xmpp.events.PresenceEvent;

public interface PresenceListener extends XMPPListener
{
    /**
     * Performed when a presence changed and it's not {@link PresenceType#AVAILABLE}, {@link PresenceType#UNAVAILABLE} or {@link PresenceShow#AWAY}.
     * 
     * @param event
     *            Reference to the new presence state of a resource.
     */
    void onPresenceChanged(PresenceEvent event);

    /**
     * Performed when the presence has changed to {@link PresenceType#AVAILABLE}.
     * 
     * @param event
     */
//    void onAvailable(PresenceEvent event);

    /**
     * 
     * @param event
     */
//    void onUnavailable(PresenceEvent event);

    /**
     * Performed when the resource is available but the mode ({@link PresenceShow}) changed to {@link PresenceShow#AWAY}.
     * 
     * @param event
     */
//    void onAway(PresenceEvent event);
    
}
