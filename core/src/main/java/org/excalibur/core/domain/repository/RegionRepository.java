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

import org.excalibur.core.cloud.api.domain.Region;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.repository.GeographicRegionRepository.GeographicRegionRowMapper;
import org.excalibur.core.domain.repository.RegionRepository.RegionRowMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(RegionRowMapper.class)
public interface RegionRepository extends Closeable
{
    String SQL_SELECT_ALL_REGIONS = "SELECT r.id as region_id, r.geographic_region_id, gr.name as geographic_region_name,\n" +
    		" r.name as region_name, r.endpoint as region_endpoint, r.city_name as region_city_name\n" +
    		"FROM region r\n" +
    		"  join geographic_region gr on gr.id = r.geographic_region_id\n";
    
    @SqlQuery(SQL_SELECT_ALL_REGIONS + " WHERE r.id = :regionId")
    Region findById(@Bind("regionId") Integer regionId);

    @SqlQuery(SQL_SELECT_ALL_REGIONS + "WHERE lower(r.name) = lower(:name)")
    Region findByName(@Bind("name") String name);
    
    @SqlQuery(SQL_SELECT_ALL_REGIONS + "ORDER BY r.name")
    List<Region> listRegions();
    
    @SqlQuery(SQL_SELECT_ALL_REGIONS + "WHERE r.geographic_region_id = :geographicRegionId ORDER BY r.name")
    List<Region> listRegions(@Bind("geographicRegionId") Integer geographicRegionId);
    
    @SqlQuery("SELECT r.id as region_id, r.name as region_name, r.endpoint as region_endpoint, r.city_name as region_city_name,\n" +
              " r.geographic_region_id, gr.name as geographic_region_name\n"+
    		  "FROM region r\n" +
    		  " join region_provider rp ON rp.provider_id = :providerId AND rp.region_id = r.id\n" +
    		  " join geographic_region gr on gr.id = r.geographic_region_id\n" +
    		  "ORDER BY r.name")
    List<Region> listRegionsOfProvider(@Bind("providerId") Integer providerId);

    
    
    String SELECT_ALL_REGIONS = "SELECT z.id as zone_id, z.region_id, z.name as zone_name, r.name as region_name, r.endpoint as region_endpoint,\n" +
            "       r.city_name as region_city_name, r.geographic_region_id, gr.name as geographic_region_name\n" +
            "FROM zone z\n" +
            "  join region r on r.id = z.region_id\n" +
            "  join geographic_region gr on gr.id = r.geographic_region_id\n";
    
    @SqlQuery(SELECT_ALL_REGIONS +  "WHERE z.region_id = :regionId \n ORDER BY z.name")
    @RegisterMapper(ZoneRowMapper.class)
    List<Zone> listZoneOfRegion(@Bind("regionId")Integer regionId);
    
    @SqlQuery(SELECT_ALL_REGIONS + "WHERE lower(z.name) = lower(:name)")
    @RegisterMapper(ZoneRowMapper.class)
    Zone findZoneByName(@Bind("name") String name);
    
    
    @SqlQuery("SELECT r.id as region_id, r.name as region_name, r.endpoint as region_endpoint, r.city_name as region_city_name,\n" +
    		  "r.geographic_region_id, gr.name as geographic_region_name\n" +
    		  "FROM region_provider rp\n" +
    		  "  join region r on r.id = rp.region_id and rp.provider_id = :providerId\n" +
    		  "  join geographic_region gr on gr.id = r.geographic_region_id\n" +
    		  "ORDER BY r.name")
    List<Region> getRegionsOfProvider(@Bind("providerId") Integer providerId);
    
    
    
    public static final class RegionRowMapper implements ResultSetMapper<Region>
    {
        @Override
        public Region map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Region()
                    .setCity(r.getString("region_city_name"))
                    .setEndpoint(r.getString("region_endpoint"))
                    .setId(r.getInt("region_id"))
                    .setName(r.getString("region_name"))
                    .setGeographicRegion(new GeographicRegionRowMapper().map(index, r, ctx));
        }
    }
    
    public static final class ZoneRowMapper implements ResultSetMapper<Zone>
    {
        @Override
        public Zone map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Zone()
                    .setId(r.getInt("zone_id"))
                    .setName(r.getString("zone_name"))
                    .setRegion(new RegionRowMapper().map(index, r, ctx))
                    .setStatus("available");
        }        
    }
}    
