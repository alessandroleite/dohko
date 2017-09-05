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
	
	@SqlQuery(" SELECT out.uuid, (SELECT t.uuid FROM task t WHERE lower(t.id) = lower(out.task_id)) as task_id,\n" + 
	          " task_output_type_id as type_id, value \n" +
	          " FROM task_output out WHERE lower(out.uuid) = lower(:uuid)\n")
	TaskOutput getById(@Bind("uuid")String id);
	
	@SqlQuery(" SELECT out.uuid, (SELECT t.uuid FROM task t WHERE lower(t.uuid) = lower(:taskId)) as task_id,\n" + 
	          " task_output_type_id as type_id, value \n" +
	          " FROM task_output out ORDER BY out.id\n")
	@Nonnull
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
