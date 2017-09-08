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

import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.ApplicationDescriptor;
import org.excalibur.core.execution.domain.repository.JobRepository.JobRowSetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(JobRowSetMapper.class)
public interface JobRepository extends Closeable
{
    String SQL_SELECT_ALL = " SELECT u.username, j.user_id, j.uuid, j.description, j.name as job_name, j.created_in, j.finished_in\n" +
             " FROM job j\n" +
             " JOIN user u ON u.id = j.user_id\n";

    @GetGeneratedKeys
    @SqlUpdate("INSERT into job (user_id, name, uuid, description, created_in, finished_in) VALUES (:user.id, :name, :id, :description, :createdIn, :finishedIn)")
    Integer insert(@BindBean ApplicationDescriptor job);
    
    @SqlBatch("INSERT into job (user_id, name, uuid, description, created_in, finished_in) VALUES (:user.id, :name, :id, :description, :createdIn, :finishedIn)")
    void insert(@BindBean Iterable<ApplicationDescriptor> jobs);
    
    @SqlUpdate("DELETE FROM job WHERE lower(uuid) = lower(:id)")
    void delete(@Bind("id") String id);
    
    @SqlUpdate("UPDATE job SET finished_in = :finishedIn WHERE uuid = :jobId AND finished_in IS NULL")
    void finished(@Bind("jobId")String jobId, @Bind("finishedIn") long finishedIn);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE lower(uuid) = lower (:uuid)")
    ApplicationDescriptor findByUUID(@Bind("uuid") String id);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE j.id in (SELECT t.job_id FROM task t WHERE lower(t.uuid) = lower(:taskId))")
    ApplicationDescriptor findJobOfTaskId(@Bind("taskId") String taskId);
    
    @SqlQuery(SQL_SELECT_ALL + " WHERE j.finished_in is null")
    List<ApplicationDescriptor> listAllPending();
    
    static class JobRowSetMapper implements ResultSetMapper<ApplicationDescriptor>
    {
        @Override
        public ApplicationDescriptor map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
        	Long finishedIn = r.getLong("finished_in");
        	
        	if (r.wasNull())
        	{
        		finishedIn = null;
        	}
        	
            return new ApplicationDescriptor()
                    .setCreatedIn(r.getLong("created_in"))
                    .setDescription(r.getString("description"))
                    .setFinishedIn(finishedIn)
                    .setId(r.getString("uuid"))
                    .setName(r.getString("job_name"))
                    .setPlainText(r.getString("description"))
                    .setUser(new User().setId(r.getInt("user_id")).setUsername(r.getString("username")));
        }
    }
}
