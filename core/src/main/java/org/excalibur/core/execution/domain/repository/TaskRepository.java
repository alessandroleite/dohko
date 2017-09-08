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

import org.excalibur.core.execution.domain.Application;
import org.excalibur.core.execution.domain.repository.TaskRepository.TaskRepositorySetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(TaskRepositorySetMapper.class)
public interface TaskRepository extends Closeable
{   
    String SQL_SELECT_ALL = "SELECT (SELECT j.uuid FROM job j WHERE j.id = t.job_id) as job_uuid, \n "
    		+ " t.job_id, t.uuid as task_uuid, t.name as task_name, t.commandline as task_commandline \n"
    		+ " FROM task t\n";
    
    @GetGeneratedKeys
    @SqlUpdate("INSERT into task (job_id, uuid, name, commandline) VALUES ((SELECT id FROM job WHERE lower(uuid) = lower(:jobId)), :id, :name, :commandLine)")
    Integer insert(@BindBean Application application);
    
    @SqlBatch("INSERT into task (job_id, uuid, commandline) VALUES ((SELECT id FROM job WHERE uuid = :jobId), :id, :name, :commandLine)")
    void insert(@BindBean Iterable<Application> applications);
    
    @SqlUpdate("DELETE FROM task WHERE lower(uuid) = lower(:taskId)")
    void delete(@Bind("taskId") String taskId);
        
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(t.uuid) = lower(:uuid)")
    Application findByUUID(@Bind("uuid") String id);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE SELECT t.job_id = (SELECT id FROM job j WHERE lower(j.uuid) = lower(:uuid))")
    List<Application> findAllTasksOfJob(@Bind("uuid") String jobUUID);
    
    
    
//    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(t.uuid) = lower(:uuid) AND t.status = :status.name ")
//    List<Application> findTasksOfJobWithStatus(@Bind("uuid") String jobUUID, @Bind("status") TaskStatus status);
    
//    @RegisterMapper(ApplicationExecutionResultMapper.class)
//    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(t.uuid) = lower(:uuid) ")
//    ApplicationExecutionResult getTaskResult(@Bind("uuid") String uuid);
    
//    @RegisterMapper(ApplicationExecutionResultMapper.class)
//    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(j.uuid) = lower(:uuid) ")
//    List<ApplicationExecutionResult> getJobTasksResult(@Bind("uuid") String uuid);
    
    static class TaskRepositorySetMapper implements ResultSetMapper<Application>
    {
        @Override
        public Application map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Application()
            		.setCommandLine(r.getString("task_commandline"))
                    .setId(r.getString("task_uuid"))
                    .setJobId(r.getString("job_uuid"))
                    .setName(r.getString("task_name"));
        }
    }



	
    
//    static class ApplicationExecutionResultMapper implements ResultSetMapper<ApplicationExecutionResult>
//    {
//		@Override
//		public ApplicationExecutionResult map(int index, ResultSet r, StatementContext ctx) throws SQLException 
//		{
//			Application app = new TaskRepositorySetMapper().map(index, r, ctx);
//			
//			return new ApplicationExecutionResult()
//					.setApplication(app)
//					.setElapsedTime(r.getLong("task_elapsed_time_ns"))
//					.setExitValue(r.getInt("exit_value"))
//					.setId(r.getString("task_uuid"))
//					.setJobId(app.getJob().getId())
//					.setOutput(r.getString("task_result"))
//					.setSyserr(r.getString("syserr"))
//					.setSysout(r.getString("sysout"))
//					.setWorker(new VirtualMachine().setName(r.getString("task_worker_name")));
//		}
//    }
}
