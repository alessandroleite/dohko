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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "account", namespace = "http://www.excalibur.org/types/xmpp")
@XmlType(name = "account", namespace = "http://www.excalibur.org/types/xmpp")
public final class AccountAdapter extends XmlAdapter<AccountBuilder, Account>
{

    @Override
    public AccountBuilder marshal(Account account) throws Exception
    {
        return null;
    }

    @Override
    public Account unmarshal(AccountBuilder builder) throws Exception
    {
        return null;
    }
}
