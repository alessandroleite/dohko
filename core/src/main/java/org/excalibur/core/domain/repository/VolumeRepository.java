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
import java.util.Date;
import java.util.List;

import org.excalibur.core.cloud.api.Volume;
import org.excalibur.core.cloud.api.VolumeType;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.domain.repository.VolumeRepository.VolumeMapper;
import org.excalibur.core.domain.repository.VolumeTypeRepository.VolumeTypeMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(VolumeMapper.class)
public interface VolumeRepository extends Closeable
{

    String SQL_SELECT_DISK = "SELECT d.id as disk_id, d.name as disk_name, d.size_gb as disk_size, d.iops as disk_iops,\n" +
    		                 "       d.created_in as disk_created_in, d.deleted_in as disk_deleted_in, d.owner_id as disk_owner_id,\n" +    		               
    		                 "       z.id as zone_id, z.name as zone_name, z.region_id,\n" +
    		                 "       dt.id as vol_type_id, dt.provider_id as vol_type_provider_id, dt.name as vol_type_name,\n" +
                             "       dt.min_size_gb as vol_type_min_size, dt.max_size_gb as vol_type_max_size\n, " +
                             "       dt.min_iops as vol_type_min_iops, dt.max_iops as vol_type_max_iops\n "+
    		                 "FROM disk d\n" +
    		                 "     JOIN zone z ON z.id = d.zone_id\n" +
    		                 "     JOIN disk_type dt ON dt.id = d.type_id\n";

    @SqlUpdate("INSERT INTO disk (owner_id, type_id, zone_id, name, size_gb, iops, created_in, deleted_in)\n" +
    		   "VALUES (:owner.id, :type.id, :zone.id, :name, :sizeGb, :iops, :createdIn, :deletedIn)\n")
    @GetGeneratedKeys
    Integer insert(@BindBean Volume volume);

    @SqlQuery(SQL_SELECT_DISK + "WHERE d.id = :id")
    Volume findById(@Bind("id") Integer id);
    
    @SqlQuery(SQL_SELECT_DISK + "WHERE d.type_id = :id ORDER BY zone_name, disk_name")
    List<Volume> findByType(@BindBean VolumeType type);
    
    
    public static final class VolumeMapper implements ResultSetMapper<Volume>
    {
        @Override
        public Volume map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
        	Date deletedIn = r.getTimestamp("disk_deleted_in");
        	
        	if (r.wasNull())
        	{
        		deletedIn = null;
        	}
        		
            return new Volume()
                    .setCreatedIn(r.getTimestamp("disk_created_in"))
                    .setDeletedIn(deletedIn)
                    .setId(r.getInt("disk_id"))
                    .setIops(r.getInt("disk_iops"))
                    .setOwner(new User().setId(r.getInt("disk_owner_id")))
                    .setName(r.getString("disk_name"))
                    .setSizeGb(r.getInt("disk_size"))
                    .setType(new VolumeTypeMapper().map(index, r, ctx))
                    .setZone(new Zone().setId(r.getInt("zone_id")).setName(r.getString("zone_name")));
        }
    }
}
