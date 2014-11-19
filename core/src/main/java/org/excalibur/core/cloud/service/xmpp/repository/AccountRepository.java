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
package org.excalibur.core.cloud.service.xmpp.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.cloud.service.xmpp.Account;
import org.excalibur.core.cloud.service.xmpp.AccountBuilder;
import org.excalibur.core.cloud.service.xmpp.JID;
import org.excalibur.core.cloud.service.xmpp.repository.AccountRepository.AccountRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(AccountRowMapper.class)
public interface AccountRepository
{
    @SqlUpdate("INSERT INTO jid_account (id, owner_id, jid, domain, passwd, name, resource, attributes, created_in, date_status) " +
    		   " VALUES (:ownerId, :domain, :password, :name, :resource, :attributes, :createIn, :dateLastStatus)")
    @GetGeneratedKeys
    Integer insert(@BindBean Account account);
    
    @SqlQuery(" SELECT id, owner_id, jid, domain, passwd, resource, name, attributes, created_in, date_status " +
            " FROM jid_account where owner_id = :ownerId and jid = :id order by jid")
    Account findAccountByJID(@Bind("ownerId") Integer ownerId, @BindBean JID jid);
    
    @SqlQuery(" SELECT id, owner_id, jid, domain, passwd, resource, name, attributes, created_in, date_status " +
    		  " FROM jid_account where owner_id = :ownerId order by jid")
    List<Account> getAccountsOfOwnerId(@Bind("ownerId") Integer ownerId);
    
    public static final class AccountRowMapper implements ResultSetMapper<Account>
    {
        public Account map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            AccountBuilder builder = new AccountBuilder()
                           .createdIn(r.getTimestamp("created_in"))                           
                           .domain(r.getString("domain"))                           
                           .jid(new JID(r.getString("jid")))
                           .lastStatusDate(r.getTimestamp("date_status"))
                           .name(r.getString("name"))
                           .ownerId(r.getInt("owner_id"))
                           .password(r.getString("passwd"));
            
            String attributes = r.getString("attributes");
            if (!r.wasNull() && attributes.trim().length() > 0)
            {
                for(String attribute: attributes.split(";"))
                {
                    String[] parts = attribute.split("=");
                    builder.withAttribute(parts[0], parts[1]);
                }
            }
            
            return builder.build();
        }
    }
}
