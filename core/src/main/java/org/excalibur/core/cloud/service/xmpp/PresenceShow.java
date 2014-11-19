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

public enum PresenceShow
{
    /**
     * The entity is assumed to be online and available.
     */
    NONE,

    /**
     * The entity or resource is temporarily away.
     */
    AWAY,

    /**
     * The entity or resource is actively interested in talking.
     */
    CHAT,

    /**
     * The entity or resource is busy (dnd = "Do Not Disturb").
     */
    DND,

    /**
     * The entity or resource is away for an extended period
     * (xa = "eXtended Away").
     */
    XA;

}
