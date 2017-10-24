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
package org.excalibur.core.execution.domain.repository;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import org.excalibur.core.execution.domain.TaskOutput;
import org.excalibur.core.execution.domain.TaskOutputType;
import org.excalibur.core.execution.domain.repository.TaskOutputRepository.TaskOutputRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import io.dohko.jdbi.stereotype.Repository;

@Repository
@RegisterMapper(TaskOutputRowMapper.class)
public interface TaskOutputRepository extends Closeable 
{
	@Nonnull
	@SqlUpdate("INSERT INTO task_output (uuid, task_id, task_output_type_id, value) \n" + 
               "VALUES (:id, (SELECT id FROM task t WHERE lower(t.uuid) = lower(:taskId) ), :type.id, :value) ")
	@GetGeneratedKeys
	Integer insert(@Nonnull @BindBean TaskOutput result);
	
	@SqlBatch("INSERT INTO task_output (uuid, task_id, task_output_type_id, value) \n" + 
            "VALUES (:id, (SELECT id FROM task t WHERE lower(t.uuid) = lower(:taskId)), :type.id, value) ")
	void insert(@Nonnull @BindBean Iterable<TaskOutput> results);
	
	@SqlUpdate("DELETE FROM task_output WHERE uuid = :id")
	void delete(@Bind("id") String id);
	
	@SqlUpdate("DELETE FROM task_output WHERE id = :id")
	void delete(@Bind("id") Integer id);
	
	@SqlQuery(" SELECT ot.uuid, (SELECT t.uuid FROM task t WHERE lower(t.id) = lower(ot.task_id)) as task_id,\n" + 
	          " task_output_type_id as type_id, value \n" +
	          " FROM task_output ot \n" + 
	          " WHERE lower(ot.uuid) = lower(:uuid) \n")
	TaskOutput getById(@Bind("uuid")String id);
	
	@Nonnull
	@SqlQuery(" SELECT ot.uuid, (SELECT t.uuid FROM task t WHERE lower(t.uuid) = lower(:taskId)) as task_id,\n" + 
	          " task_output_type_id as type_id, value \n" +
	          " FROM task_output ot \n" + 
	          " ORDER BY ot.id \n")
	Iterable<TaskOutput> getAllOutputsOfTask(@Nonnull @Bind("taskId") String taskId);
		
	class TaskOutputRowMapper implements ResultSetMapper<TaskOutput> 
	{
		@Override
		public TaskOutput map(int index, ResultSet r, StatementContext ctx) throws SQLException 
		{
			return new TaskOutput()
					.setId(r.getString("uuid"))
					.setTaskId(r.getString("task_id"))
					.setType(TaskOutputType.valueOf(r.getInt("type_id")))
					.setValue(r.getString("value"));
		}
	}
}
