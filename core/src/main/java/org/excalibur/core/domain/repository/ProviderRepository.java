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
package org.excalibur.core.domain.repository;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.ProviderFactory;
import org.excalibur.core.domain.repository.ProviderRepository.ProviderRowMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(ProviderRowMapper.class)
public interface ProviderRepository extends Closeable
{
    // -------------------------------------------------------- //
    // ---                 Constants                        --- //
    // -------------------------------------------------------- //
    String QUERY_SELECT_PROVIDER = "SELECT id as provider_id, name as provider_name, class_name, ub_instances_per_type FROM provider\n";
    
    
    // -------------------------------------------------------- //
    // ---              Provider query methods              --- //
    // -------------------------------------------------------- //

    @SqlQuery(QUERY_SELECT_PROVIDER + " where id = :id")
    Provider findProviderById(@Bind("id") Integer id);
    
    @SqlQuery(QUERY_SELECT_PROVIDER + " where lower(name) = lower(:name)")
    Provider findByExactlyProviderName(@Bind("name") String name);
    
    /**
     * Returns all the providers available on the system.
     * 
     * @return A not <code>null</code> {@link List} with the available providers.
     */
    @SqlQuery(QUERY_SELECT_PROVIDER + " order by provider_name")
    List<Provider> getAllProviders();
    
    static final class ProviderRowMapper implements ResultSetMapper<Provider>
    {
        @Override
        public Provider map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            Mirror mirror = new Mirror();
            Provider provider = ProviderFactory.getProviderByName(r.getString("provider_name"));
            
            mirror.on(provider).set().field("id_").withValue(r.getInt("provider_id"));
            mirror.on(provider).set().field("name_").withValue(r.getString("provider_name"));
            mirror.on(provider).set().field("limitOfResourcesPerType_").withValue(r.getInt("ub_instances_per_type"));
            mirror.on(provider).set().field("serviceClass_").withValue(r.getString("class_name"));
            
            return provider;
        }
    }
}
