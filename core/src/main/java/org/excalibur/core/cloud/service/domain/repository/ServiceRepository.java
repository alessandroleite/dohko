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
package org.excalibur.core.cloud.service.domain.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.cloud.service.domain.Protocol;
import org.excalibur.core.cloud.service.domain.Service;
import org.excalibur.core.cloud.service.domain.repository.ServiceRepository.ServiceRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(ServiceRowMapper.class)
public interface ServiceRepository
{
    @SqlUpdate("INSERT INTO service (name, uri, media_type, protocol) VALUES (:name, :uri, :mediaType, :protocol.name)")
    @GetGeneratedKeys
    Integer insert(@BindBean Service service);
    
    @SqlUpdate("UPDATE service SET name = :name, uri = :uri, media_type = :mediaType, protocol = :protocol.name WHERE id = :id")
    void update(@BindBean Service service);
    
    @SqlQuery("SELECT id as service_id, name, uri, media_type, protocol FROM service WHERE id = :id")
    Service findServiceById(@Bind("id") Integer id);
    
    @SqlQuery("SELECT id as service_id, name, uri, media_type, protocol FROM service WHERE name = :name ORDER BY name")
    List<Service> findServiceByName(@Bind("name") String name);
    
    @SqlQuery("SELECT id as service_id, name, uri, media_type, protocol FROM service ORDER BY name")
    List<Service> getAllServices();
    
    public static final class ServiceRowMapper implements ResultSetMapper<Service>
    {
        @Override
        public Service map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Service().withId(r.getInt("service_id"))
                    .withMediaType(r.getString("media_type"))
                    .withName(r.getString("name"))
                    .withProtocol(Protocol.valueOf(r.getString("protocol").toUpperCase()))
                    .withURI(r.getString("uri"));
        }
    }
}
