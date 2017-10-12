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
import java.util.List;

import org.excalibur.core.execution.domain.ResourceType;
import org.excalibur.core.execution.domain.TaskResourceUsage;
import org.excalibur.core.execution.domain.repository.TaskResourceUsageRepository.TaskResourceUsageRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(TaskResourceUsageRowMapper.class)
public interface TaskResourceUsageRepository extends Closeable 
{
	@SqlUpdate("INSERT INTO task_resource_usage (task_id, resource_type_id, pid, datetime, value) \n " + 
	           "VALUES ((SELECT id FROM task WHERE lower(uuid) = lower(:taskId) ), :resourceType.id, :pid, :datetime, :value)")
	@GetGeneratedKeys
	Integer insert(@BindBean TaskResourceUsage data);
	
	@SqlUpdate("INSERT INTO task_resource_usage (task_id, resource_type_id, pid, datetime, value) \n " + 
	           "VALUES ((SELECT id FROM task WHERE lower(uuid) = lower(:taskId) ), :resourceType.id, :pid, :datetime, :value)")
	void insert(@BindBean Iterable<TaskResourceUsage> data);
	
	@SqlUpdate("DELETE FROM task_resource_usage WHERE task_id = (SELECT id FROM task t WHERE lower(t.uuid) = lower(:taskId))")
	void deleteAllOfTask(@Bind("taskId") String taskId);
	
	@SqlQuery("SELECT r.id, t.uuid as task_id, resource_type_id, pid, datetime, value \n" + 
			 " FROM task_resource_usage r\n" +
			 " JOIN task t ON t.id = r.task_id \n" +
			 " WHERE lower(t.uuid) = lower(:taskId)\n" + 
			 " ORDER BY resource_type_id, datetime") 
	List<TaskResourceUsage> getAllOfTask(@Bind("taskId") String id);

	@SqlQuery("SELECT r.id, t.uuid as task_id, resource_type_id, pid, datetime, value \n" + 
			 " FROM task_resource_usage r\n" +
			 " JOIN task t ON t.id = r.task_id \n" +
			 " WHERE lower(t.uuid) = lower(:taskId) AND r.resource_type_id = :resourceType\n" + 
			 " ORDER BY datetime")
	List<TaskResourceUsage> getAllTypeOfTask(@Bind("taskId") String taskId, @Bind("resourceType") Integer resourceType);
	
	class TaskResourceUsageRowMapper implements ResultSetMapper<TaskResourceUsage> 
	{
		@Override
		public TaskResourceUsage map(int index, ResultSet r, StatementContext ctx) throws SQLException 
		{
			return new TaskResourceUsage()
					.setDatetime(r.getTimestamp("datetime"))
					.setId(r.getInt("id"))
					.setPid(r.getInt("pid"))
					.setResourceType(ResourceType.valueOf(r.getInt("resource_type_id")))
					.setTaskId(r.getString("task_id"))
					.setValue(r.getBigDecimal("value"));
		}
	}
}
