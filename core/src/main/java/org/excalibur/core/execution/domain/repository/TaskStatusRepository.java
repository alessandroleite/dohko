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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.TaskStatusType;
import org.excalibur.core.execution.domain.repository.TaskStatusRepository.TaskStatusRepositorySetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(TaskStatusRepositorySetMapper.class)
public interface TaskStatusRepository extends Closeable 
{
	@SqlUpdate("INSERT INTO task_status (task_id, task_status_type_id, status_time, worker_id, pid) \n" + 
               " VALUES ((SELECT id FROM task WHERE lower(uuid) = :taskId), :type.id, :date, :worker, :pid)")
	@GetGeneratedKeys
	Integer insert(@BindBean TaskStatus status);
	
	@SqlBatch("INSERT INTO task_status (task_id, task_status_type_id, status_time, worker_id, pid) \n" + 
            " VALUES ((SELECT id FROM task WHERE lower(uuid) = :taskId), :type.id, :date, :worker, :pid)")
	void insert(@BindBean Iterable<TaskStatus> statuses);
	
	@SqlUpdate("DELETE FROM task_status WHERE task_id = (SELECT t.id FROM task t WHERE lower(t.uuid) = :taskId)")
	void deleteAllStatusesOfTask(@Bind("taskId") String taskId);
	
	
	/**
	 * Returns the statuses of a given task. The returned set is never <code>null</code>. Therefore, it might be empty. An
	 * empty set means that the task is invalid or unknown by the system.
	 * @param taskId task'id to return its statuses. It might not be <code>null</code>
	 * @return A non-null {@link List} with the existing statuses of the given task. 
	 * An empty {@link List} means that the task is unknown or invalid.
	 */
	@SqlQuery("SELECT task_id, (SELECT t.uuid FROM task t WHERE t.id = ts.task_id and lower(t.uuid) = lower(:taskId)) as task_uuid, \n" + 
	          " task_status_type_id, status_time, worker_id, pid \n" + 
			  " FROM task_status ts\n" + 
	          "ORDER BY status_time\n")
	@Nonnull
	List<TaskStatus> getAllStatusesOfTask(@Bind("taskId") String taskId);
	
	/**
	 * Returns the last known status of a given task.
	 * @param taskId id of the task to return its last status
	 * @return the last status of the given task. It may be <code>null</code>
	 */
	@SqlQuery("SELECT (SELECT t.uuid FROM task t WHERE t.id = ts.task_id and lower(t.uuid) = lower(:taskId)) as task_uuid, \n" + 
	          " task_id, task_status_type_id, status_time, worker_id, pid \n" + 
			  " FROM task_status ts\n" + 
	          "WHERE ts.status_time = (SELECT MAX(status_time) FROM task_status tsm WHERE tsm.task_id = ts.task_id)")
	@Nullable
	TaskStatus getLastStatusOfTask(@Bind("taskId") String taskId);
	
	class TaskStatusRepositorySetMapper implements ResultSetMapper<TaskStatus> 
	{

		@Override
		public TaskStatus map(int index, ResultSet r, StatementContext ctx) throws SQLException 
		{
			return new TaskStatus()
					    .setDate(r.getTimestamp("status_time"))
					    .setPid(r.getInt("pid"))
					    .setTaskId(r.getString("task_uuid"))
					    .setType(TaskStatusType.valueOf(r.getInt("task_status_type_id")))
					    .setWorker(r.getString("worker_id"));
		}
	}
}
