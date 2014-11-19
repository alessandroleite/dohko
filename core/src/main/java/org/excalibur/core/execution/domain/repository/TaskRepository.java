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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.TaskStatus;
import org.excalibur.core.execution.domain.repository.TaskRepository.TaskRepositorySetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(TaskRepositorySetMapper.class)
public interface TaskRepository
{
    
    String SQL_SELECT_ALL = "SELECT t.job_id, t.uuid as task_uuid, t.status as task_status, t.description as task_description,\n" +
            " j.uuid as job_uuid, j.description as job_description\n" +
            "FROM task t\n" +
            " JOIN job j on j.id = t.job_id\n";
    
    @GetGeneratedKeys
    @SqlUpdate("INSERT into task (job_id, uuid, status, description) VALUES(:job.internalId, :id, :status.name, :plainText)")
    Integer insert(@BindBean Application application);
    
    @SqlBatch("INSERT into task (job_id, uuid, status, description) VALUES(:job.internalId, :id, :status.name, :plainText)")
    @BatchChunkSize(30)
    void insert(@BindBean Iterable<Application> applications);
    
    @SqlUpdate(" UPDATE task set status = :newStatus, worker_name = :workerName, exit_value = :exitValue, elapsed_time_ns = :elapsedTime,\n" +
               " result = :taskResult, last_state_time = current_timestamp\n" +
               " WHERE uuid = :uuid")
    void update(@BindBean(params = { "newStatus:status.name" }) Application task, @BindBean(params = { "workerName:name" }) VirtualMachine worker,
            @Bind("exitValue") Integer exitValue, @Bind("uuid") String uuid, @Bind("elapsedTime") Long elapsedTime, @Bind("taskResult") String result);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(t.uuid) = lower(:uuid)")
    Application findByUUID(@Bind("uuid") String id);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(j.uuid) = lower(:uuid)")
    List<Application> findAllTasksOfJob(@Bind("uuid") String jobUUID);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(j.uuid) = lower(:uuid) AND t.status = :status.name ")
    List<Application> findTasksOfJobWithStatus(@Bind("uuid") String jobUUID, @Bind("status") TaskStatus status);
    
    static class TaskRepositorySetMapper implements ResultSetMapper<Application>
    {
        @Override
        public Application map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Application()
                    .setId(r.getString("task_uuid"))
                    .setJob(new ApplicationDescriptor().setId(r.getString("job_uuid")).setPlainText(r.getString("job_description"))
                            .setInternalId(r.getInt("job_id")))
                    .setStatus(TaskStatus.valueOf(r.getString("task_status")))
                    .setPlainText(r.getString("task_description"));
        }
    }
}
