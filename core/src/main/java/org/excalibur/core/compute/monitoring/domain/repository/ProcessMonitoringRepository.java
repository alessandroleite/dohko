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

import static org.excalibur.core.compute.monitoring.domain.ProcessState.builder;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.excalibur.core.compute.monitoring.domain.ProcessState;
import org.excalibur.core.compute.monitoring.domain.ProcessState.Builder;
import org.excalibur.core.compute.monitoring.domain.repository.ProcessMonitoringRepository.ProcessStateResultMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(ProcessStateResultMapper.class)
public interface ProcessMonitoringRepository
{
    public static final class ProcessStateResultMapper implements ResultSetMapper<ProcessState>
    {
        @Override
        public ProcessState map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            Builder builder = builder().process(r.getLong("process_id"));
            
            return builder.build();
        }
    }
}
