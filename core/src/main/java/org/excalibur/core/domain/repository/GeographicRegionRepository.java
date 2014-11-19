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

import org.excalibur.core.cloud.api.domain.GeographicRegion;
import org.excalibur.core.domain.repository.GeographicRegionRepository.GeographicRegionRowMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(GeographicRegionRowMapper.class)
public interface GeographicRegionRepository extends Closeable
{
    String SQL_SELECT_ALL = "SELECT id as geographic_region_id, name as geographic_region_name FROM geographic_region\n";

    @SqlQuery(SQL_SELECT_ALL + "ORDER by name\n")
    List<GeographicRegion> listAll();

    @SqlQuery(SQL_SELECT_ALL + "WHERE lower(name) = lower(:name)")
    GeographicRegion findByName(@Bind("name") String name);

    @SqlQuery(SQL_SELECT_ALL + "WHERE id = :id")
    GeographicRegion findById(@Bind("id") Integer id);

    static class GeographicRegionRowMapper implements ResultSetMapper<GeographicRegion>
    {
        @Override
        public GeographicRegion map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new GeographicRegion().setId(r.getInt("geographic_region_id")).setName(r.getString("geographic_region_name"));
        }
    }
}
