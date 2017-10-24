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
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.domain.FailureAction;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.excalibur.core.execution.domain.repository.ApplicationDescriptionRepository.ApplicationExecutionDescriptionRowMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import io.dohko.jdbi.stereotype.Repository;

@Repository
@RegisterMapper(ApplicationExecutionDescriptionRowMapper.class)
public interface ApplicationDescriptionRepository extends Closeable
{
    String SQL_SELECT_ALL = "SELECT id as application_exec_desc_id, application_id as application_script_id, user_id as application_exec_desc_user_id,\n" +
            " failure_action_id, name as application_exec_desc_name, resource_name as application_exec_desc_resource_name,\n" +
            " number_of_execution as application_exec_desc_number_of_execution, created_in as application_exec_desc_created_in\n" +
            "FROM application_execution_description app_desc\n";
    
    
    @SqlUpdate("INSERT INTO application_execution_description (application_id, failure_action_id, user_id, name, resource_name, number_of_execution, created_in)\n" +
    		   "VALUES (:application.id, :failureAction.id, :user.id, :name, :resource, :numberOfExecutions, :createdIn)\n")
    @GetGeneratedKeys
    Integer insert(@BindBean ApplicationExecDescription description);
    
    @SqlQuery(SQL_SELECT_ALL + "WHERE app_desc.id = :id")
    ApplicationExecDescription findById(@Bind("id") Integer id);
    
    @SqlQuery(SQL_SELECT_ALL + "WHERE lower(name) = lower(:name)")
    ApplicationExecDescription findByName(@Bind("name") String name);
    
    @SqlQuery(SQL_SELECT_ALL + "ORDER BY id, name\n")
    List<ApplicationExecDescription> listAll();
    
    @SqlQuery(SQL_SELECT_ALL + "WHERE app_desc.id NOT IN (SELECT app_description_id FROM application_execution_result WHERE instance_id = :instanceId)\n")
    List<ApplicationExecDescription> listPendentExecutionsForInstance(@Bind("instanceId") Integer instanceId);
    
    @SqlQuery(SQL_SELECT_ALL + "WHERE app_desc.id NOT IN (SELECT app_description_id FROM application_execution_result) and resource_name != 'all-instance-types'\n")
    List<ApplicationExecDescription> listPendentExecutions();

    @SqlQuery(SQL_SELECT_ALL + "WHERE lower(resource_name) = lower(:name) ORDER BY id\n")
    List<ApplicationExecDescription> listExecutionsForResource(@Bind("name") String name);
    
    
    static final class ApplicationExecutionDescriptionRowMapper implements ResultSetMapper<ApplicationExecDescription>
    {
        @Override
        public ApplicationExecDescription map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new ApplicationExecDescription()
                         .setApplication(new ScriptStatement(r.getInt("application_script_id")))
                         .setCreatedIn(r.getTimestamp("application_exec_desc_created_in"))
                         .setFailureAction(FailureAction.valueOf(r.getInt("failure_action_id")))
                         .setId(r.getInt("application_exec_desc_id"))
                         .setName(r.getString("application_exec_desc_name"))
                         .setNumberOfExecutions(r.getInt("application_exec_desc_number_of_execution"))
                         .setResource(r.getString("application_exec_desc_resource_name"))
                         .setUser(new User(r.getInt("application_exec_desc_user_id")));
        }
    }

}
