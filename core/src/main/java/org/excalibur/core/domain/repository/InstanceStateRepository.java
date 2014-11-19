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

import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.InstanceState;
import org.excalibur.core.domain.repository.InstanceStateRepository.InstanceStateMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(InstanceStateMapper.class)
public interface InstanceStateRepository extends Closeable
{
    @SqlUpdate("INSERT INTO instance_hist_state (instance_id, instance_state_type_id, date) VALUES (:instance.instance_id, :state_.id_, :date_) ")
    void insertInstanceState(@BindBean InstanceState state);

    @SqlQuery("SELECT id as instance_state_hist_id, instance_id, instance_state_type_id, date "
            + "FROM instance_hist_state WHERE  date = (SELECT max(date) FROM instance_hist_state ihs WHERE ihs.instance_id = :instanceId)")
    InstanceStateType lastInstanceState(@Bind("instanceId") Integer instanceId);
    
    public static class InstanceStateMapper implements ResultSetMapper<InstanceState>
    {
        @Override
        public InstanceState map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new InstanceState()
                    .setDate(r.getTimestamp("date"))
                    .setId(r.getInt("instance_state_hist_id"))
                    .setInstance(new VirtualMachine().setId(r.getInt("instance_id")))
                    .setState(InstanceStateType.valueOf(r.getInt("instance_state_type_id")));
        }
    }
}
