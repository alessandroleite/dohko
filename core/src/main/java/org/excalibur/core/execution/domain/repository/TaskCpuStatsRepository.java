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

import org.excalibur.core.execution.domain.repository.TaskCpuStatsRepository.TaskCpuStatsMapper;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Optional;

import io.airlift.command.ProcessCpuState;
import io.dohko.jdbi.binders.BindBean;
import io.dohko.jdbi.stereotype.Repository;

@Repository
@RegisterMapper(TaskCpuStatsMapper.class)
public interface TaskCpuStatsRepository extends Closeable 
{
	@GetGeneratedKeys
	@SqlUpdate("INSERT INTO task_cpu_stats (datetime, pid, percent, sys, user, total) VALUES (:datetime, :pid, :percent, :sys, :user, :total")
	Integer insert(@BindBean ProcessCpuState stats);
	
	@SqlBatch("INSERT INTO task_cpu_stats (pid, datetime, percent, sys, user, total) VALUES (:pid, :datetime, :percent, :sys, :user, :total")
	void insert(@BindBean Iterable<ProcessCpuState> statses);
	
	@SingleValueResult
	@SqlQuery("SELECT id, pid, datetime, percent, sys, user, total FROM task_cpu_stats WHERE pid = :pid AND datetime = :datetime")
	Optional<ProcessCpuState> getState(@Bind("pid") long pid, @Bind("datetime") DateTime datetime);
	
	@SingleValueResult
	@SqlQuery("SELECT id, pid, datetime, percent, sys, user, total FROM task_cpu_stats WHERE pid = :pid AND " + 
	          " datetime = (SELECT MAX(tc. datetime) FROM task_cpu_stats tc WHERE tc.pid = :pid)")
	Optional<ProcessCpuState> getLast(@Bind("pid") long pid);
	
	@SqlQuery("SELECT id, pid, datetime, percent, sys, user, total FROM task_cpu_stats tcpu " +
			" WHERE pid = (SELECT pid FROM task_status ts WHERE ts.task_id = (SELECT t.id FROM task t WHERE lower(t.uuid) = lower(:uuid)) " +
			"              AND task_status_type_id IS NOT NUll AND task_status_type_id in (2, 3, 4)) " +
			" AND datetime = (SELECT MAX(ts.datetime) FROM task_cpu_stats ts WHERE ts.pid = tcpu.pid)" +
			" ORDER BY datetime")
	Optional<ProcessCpuState> getLast(@Bind("uuid") String taskId);
	
	@SqlQuery("SELECT id, pid, datetime, percent, sys, user, total FROM task_cpu_stats WHERE pid = :pid ORDER BY datetime")
	List<ProcessCpuState> getStats(@Bind("pid")long pid);
	
	@SqlQuery("SELECT id, pid, datetime, percent, sys, user, total FROM task_cpu_stats " +
			" WHERE pid = (SELECT pid FROM task_status ts WHERE ts.task_id = :taskId AND task_status_type_id IS NOT NUll AND task_status_type_id in (2, 3, 4))" +
			" ORDER BY datetime")
	List<ProcessCpuState> getStatsOfTask(@Bind("taskId") long taskId);
	
	@SqlQuery("SELECT id, pid, datetime, percent, sys, user, total FROM task_cpu_stats " +
			" WHERE pid = (SELECT pid FROM task_status ts WHERE ts.task_id = (SELECT t.id FROM task t WHERE lower(t.uuid) = lower(:uuid)) " +
			"              AND task_status_type_id IS NOT NUll AND task_status_type_id in (2, 3, 4))" +
			" ORDER BY datetime")
	List<ProcessCpuState> getStatsOfTask(@Bind("uuid") String uuid);
	
	class TaskCpuStatsMapper implements ResultSetMapper<ProcessCpuState> 
	{
		@Override
		public ProcessCpuState map(int index, ResultSet r, StatementContext ctx) throws SQLException 
		{
			return new ProcessCpuState()
					    .setPid(r.getLong("pid"))
					    .setDatetime(r.getTimestamp("datetime"))
					    .setPercent(r.getLong("percent"))
					    .setSys(r.getLong("sys"))
					    .setUser(r.getLong("user"))
					    .setTotal(r.getLong("total"));
		}
	}
}
