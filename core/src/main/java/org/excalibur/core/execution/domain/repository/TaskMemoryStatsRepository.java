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

import org.excalibur.core.execution.domain.repository.TaskMemoryStatsRepository.TaskMemoryStatsMapper;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Optional;

import io.airlift.command.ProcessMemoryState;
import io.dohko.jdbi.binders.BindBean;
import io.dohko.jdbi.stereotype.Repository;

@Repository
@RegisterMapper(TaskMemoryStatsMapper.class)
public interface TaskMemoryStatsRepository extends Closeable
{
	@SqlUpdate("INSERT task_mem_stats (pid, datetime, size, resident, share) VALUES (:pid, :datetime, :size, :resident, :share)")
	@Nonnull
	void insert(@BindBean ProcessMemoryState stat);
	
	@SqlBatch("INSERT task_mem_stats (pid, datetime, size, resident, share) VALUES (:pid, :datetime, :size, :resident, :share)")
	void insert(@BindBean Iterable<ProcessMemoryState> statses);
	
	@SqlQuery("SELECT id, pid, datetime, size, resident, share FROM task_mem_stats WHERE pid = :pid, datetime :datetime")
	@SingleValueResult
	Optional<ProcessMemoryState> getState(@Bind("pid") long pid, @Bind("datetime") DateTime datetime);
	
	@SqlQuery("SELECT id, pid, datetime, size, resident, share FROM task_mem_stats WHERE pid = :pid \n"+ 
	          " AND datetime = SELECT MAX(datetime) FROM task_mem_stats WHERE pid = :pid)")
	@SingleValueResult
	Optional<ProcessMemoryState> getLast(@Bind("pid") long pid);
	
	@SqlQuery("SELECT id, pid, datetime, size, resident, share FROM task_mem_stats WHERE pid = :pid\n" + 
	          "ORDER BY datetime desc")
	List<ProcessMemoryState> getStats(@Bind("pid") long pid);
	
	@SqlQuery(" SELECT id, pid, datetime, size, resident, share FROM task_mem_stats\n" + 
	          " WHERE pid = (SELECT pid FROM task_status ts WHERE ts.task_id = (SELECT t.id FROM task t WHERE lower(t.uuid) = lower(:uuid))\n\t\t" +
			  "             AND ts.task_status_type_id IS NOT NUll AND ts.task_status_type_id in (2, 3, 4))\n" +
	          " ORDER BY datetime")
	List<ProcessMemoryState> getStatsOfTask(@Bind("uuid") String uuid);
	
	class TaskMemoryStatsMapper implements ResultSetMapper<ProcessMemoryState>
	{
		@Override
		public ProcessMemoryState map(int index, ResultSet r, StatementContext ctx) throws SQLException 
		{
			return new ProcessMemoryState()
					   .setPid(r.getLong("pid"))
					   .setDatetime(r.getTimestamp("datetime"))
					   .setSize(r.getLong("size"))
					   .setResident(r.getLong("resident"))
					   .setShare(r.getLong("share"));
		}
	}
}
