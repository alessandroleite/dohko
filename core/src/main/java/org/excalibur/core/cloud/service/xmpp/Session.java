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

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smack.XMPPConnection;

final class Session
{
    private final Account user_;
    private final XMPPConnection connection_;
    private ConcurrentMap<String, JID> onlineRosters_ = new ConcurrentHashMap<String, JID>();

    public Session(Account account, XMPPConnection connection)
    {
        this.user_ = account;
        this.connection_ = connection;
        
//        connection_.getRoster().addRosterListener(new RosterListener()
//        {
//            @Override
//            public void presenceChanged(org.jivesoftware.smack.packet.Presence presence)
//            {
//                System.out.printf("type=%s, mode=%s\n", presence.getType(), presence.getMode());
//            }
//
//            @Override
//            public void entriesUpdated(Collection<String> entriesUpdated)
//            {
//            }
//
//            @Override
//            public void entriesDeleted(Collection<String> entriesDeleted)
//            {
//            }
//
//            @Override
//            public void entriesAdded(Collection<String> entriesAdded)
//            {
//            }
//        });
    }

    /**
     * @return the user
     */
    public Account getUser()
    {
        return user_;
    }

    /**
     * @return the connection
     */
    XMPPConnection getConnection()
    {
        return connection_;
    }

    public Collection<JID> onlines()
    {
        return Collections.unmodifiableCollection(this.onlineRosters_.values());
    }

}
