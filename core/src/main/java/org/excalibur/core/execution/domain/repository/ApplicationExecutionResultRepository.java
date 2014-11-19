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

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.ApplicationExecutionResult;
import org.excalibur.core.execution.domain.repository.ApplicationDescriptionRepository.ApplicationExecutionDescriptionRowMapper;
import org.excalibur.core.execution.domain.repository.ApplicationExecutionResultRepository.InstanceStatementResultSetMapper;
import org.excalibur.core.repository.bind.BindBean;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(InstanceStatementResultSetMapper.class)
public interface ApplicationExecutionResultRepository extends Closeable
{
    String QUERY_SELECT_ALL = "SELECT " +
              " app.application_id as application_script_id, app.user_id as application_exec_desc_user_id,\n" +
              " app.failure_action_id, app.name as application_exec_desc_name, app.resource_name as application_exec_desc_resource_name,\n" +
              " app.number_of_execution as application_exec_desc_number_of_execution, app.created_in as application_exec_desc_created_in,\n" +     		  
              " ise.id as app_exec_result_id, ise.app_description_id as application_exec_desc_id, ise.instance_id, ise.started_in, ise.finished_in,\n" +
              " ise.exit_code, ise.statement_output, ise.statement_error\n" +
              "FROM application_execution_result ise\n" +
              "  JOIN application_execution_description app ON app.id = ise.app_description_id\n";

    @SqlUpdate("INSERT INTO application_execution_result (instance_id, app_description_id, started_in, finished_in, exit_code, statement_output, statement_error)\n"
            + "VALUES (:instance.id, :application.id, :startedIn, :finishedIn, :exitCode, :output, :error)")
    @GetGeneratedKeys
    Integer insertApplicationExecution(@BindBean ApplicationExecutionResult execution);
    
    @SqlUpdate(" UPDATE application_execution_result SET finished_in = :finishedIn, exit_code = :exitCode, statement_output = :output, statement_error = :error\n" +
               " WHERE id = :id\n")
    void updateApplicationExecution(@BindBean ApplicationExecutionResult execution);

    @SqlQuery(QUERY_SELECT_ALL + " WHERE ise.instance_id = :instanceId ORDER BY started_in")
    List<ApplicationExecutionResult> listApplicationsExecutedOnInstance(@Bind("instanceId") Integer instanceId);
    
    @SqlQuery(QUERY_SELECT_ALL + " WHERE ise.id = :id")
    ApplicationExecutionResult findById(@Bind("id") Integer id);

    static final class InstanceStatementResultSetMapper implements ResultSetMapper<ApplicationExecutionResult>
    {
        @Override
        public ApplicationExecutionResult map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new ApplicationExecutionResult()
                    .setError(r.getString("statement_error"))
                    .setExitCode(r.getInt("exit_code"))
                    .setFinishedIn(r.getTimestamp("finished_in"))
                    .setId(r.getInt("app_exec_result_id"))
                    .setInstance(new VirtualMachine(r.getInt("instance_id")))
                    .setOutput(r.getString("statement_output"))
                    .setStartedIn(r.getTimestamp("started_in"))
                    .setApplication(new ApplicationExecutionDescriptionRowMapper().map(index, r, ctx));
        }
    }
}
