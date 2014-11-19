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

import org.excalibur.core.cloud.api.ProviderSupport;
import org.excalibur.core.cloud.api.VolumeType;
import org.excalibur.core.domain.repository.VolumeTypeRepository.VolumeTypeMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(VolumeTypeMapper.class)
public interface VolumeTypeRepository extends Closeable
{
    String SELECT_ALL_VOLUME_TYPES = "SELECT id as vol_type_id, provider_id as vol_type_provider_id, name as vol_type_name,\n" +
    		                         "       min_size_gb as vol_type_min_size, max_size_gb as vol_type_max_size\n, " +
    		                         "       min_iops as vol_type_min_iops, max_iops as vol_type_max_iops\n" +
    		                         "FROM disk_type\n";
    
    
    @SqlQuery(SELECT_ALL_VOLUME_TYPES + "WHERE id = :id")
    VolumeType findById(@Bind("id") Integer id);
    
    @SqlQuery(SELECT_ALL_VOLUME_TYPES + "WHERE provider_id = :providerId")
    List<VolumeType> listVolumeTypesOfProvider(@Bind("providerId") Integer providerId);

    static class VolumeTypeMapper implements ResultSetMapper<VolumeType>
    {
        @Override
        public VolumeType map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new VolumeType()
                    .setId(r.getInt("vol_type_id"))
                    .setMinIops(r.getInt("vol_type_min_iops"))
                    .setMaxIops(r.getInt("vol_type_max_iops"))
                    .setMinSizeGb(r.getInt("vol_type_min_size"))
                    .setMaxSizeGb(r.getInt("vol_type_max_size"))
                    .setName(r.getString("vol_type_name"))
                    .setProvider(new ProviderSupport().setId(r.getInt("vol_type_provider_id")));
        }
    }
}
