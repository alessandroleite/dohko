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
package org.excalibur.core.cloud.service.xmpp.authorization;

import org.apache.vysper.storage.StorageProvider;
import org.excalibur.core.cloud.service.xmpp.Account;
import org.excalibur.core.cloud.service.xmpp.JID;

public interface DatabaseAccountManagement extends StorageProvider
{
    void addUser(Account account);

    void disableUser(Account account);

    void disableUser(JID jid);

    /**
     * Checks if there is this {@code jid} is registered with the server.
     * 
     * @param jid
     *            The id to check if it's already registered in the server.
     * @return <code>true</code> if the given {@code jid} is already registered or <code>false</code> otherwise.
     */
    boolean verifyAccountExists(JID jid);
}
