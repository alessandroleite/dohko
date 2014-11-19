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
package org.excalibur.core.compute.monitoring.domain.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.excalibur.core.compute.monitoring.domain.MemoryState;
import org.excalibur.core.compute.monitoring.domain.MemoryType;
import org.excalibur.core.compute.monitoring.domain.repository.MemoryMonitoringRepository.MemoryStateResultMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(MemoryStateResultMapper.class)
public interface MemoryMonitoringRepository
{
    @SqlUpdate("INSERT INTO memory_monitoring (free, size, used, type_id, sample_time) VALUES (:free, :size, :used, :type.id, :sampleTime)")
    @GetGeneratedKeys
    Integer insert(@BindBean MemoryState state);

    @SqlUpdate("INSERT INTO memory_monitoring (free, size, used, type_id, sample_time) VALUES (:free, :size, :used, :type.id, :sampleTime)")
    void insert(@BindBean Iterable<MemoryState> state);

    public static class MemoryStateResultMapper implements ResultSetMapper<MemoryState>
    {
        @Override
        public MemoryState map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return MemoryState.builder()
                    .free(r.getLong("free"))
                    .size(r.getLong("size"))
                    .used(r.getLong("used"))
                    .type(MemoryType.valueOf(r.getInt("type_id")))
                    .sampleTime(r.getTimestamp("sample_time"))
                    .build();
        }
    }
}
