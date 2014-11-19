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
package org.excalibur.core.deployment.domain.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.domain.DeploymentStatus;
import org.excalibur.core.deployment.domain.repository.DeploymentRepository.DeploymentResultSetMapper;
import org.excalibur.core.domain.User;
import org.excalibur.core.repository.bind.BindBean;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(DeploymentResultSetMapper.class)
public interface DeploymentRepository
{
    @SqlUpdate("INSERT INTO deployment (user_id, workflow_id, status, status_time, description, data, username) VALUES " +
               "(:userId, :workflowId, :status, :statusTime, :description, :data, :username)")
    @GetGeneratedKeys
    Integer insert(@BindBean(binder=DeploymentBind.class) Deployment deployment);
    
    @SqlUpdate("UPDATE deployment SET workflow_id = :workflowId, status = :status, status_time = :statusTime, description = :description,\n" +
    		   " data = :data, username = :username WHERE id = :id and user_id = :userId")
    void update(@BindBean(binder=DeploymentBind.class) Deployment deployment);
    
    @SqlQuery("SELECT id as deployment_id, workflow_id, user_id, status, status_time, description, data, username FROM deployment WHERE id = :deploymentId")
    Deployment findDeploymentById(@Bind("deploymentId") Integer deploymentId);
    
    @SqlQuery("SELECT id as deployment_id, workflow_id, user_id, status, status_time, description, data, username FROM deployment WHERE id = :deploymentId and username = :username")
    Deployment findDeploymentById(@Bind("deploymentId") Integer deploymentId, @Bind("username") String username);
    
    @SqlQuery("SELECT id as deployment_id, workflow_id, user_id, status, status_time, description, data, username FROM deployment WHERE status = :status")
    List<Deployment> getAllDeploymentInStatus (@Bind("status") String status);
    
    public static final class DeploymentResultSetMapper implements ResultSetMapper<Deployment>
    {
        @Override
        public Deployment map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new Deployment()
                    .withId(r.getInt("deployment_id"))
                    .withStatus(DeploymentStatus.valueOf(r.getString("status")))
                    .withStatusTime(r.getDate("status_time"))
                    .withText(r.getString("data"))
                    .withUser(new User(r.getInt("user_id")))
                    .withUsername(r.getString("username"))
                    .withWorkflow(new WorkflowDescription()
                    .setId(r.getInt("workflow_id")));
        }
    }

    public static final class DeploymentBind implements Binder<BindBean, Deployment>
    {
        @Override
        public void bind(SQLStatement<?> q, BindBean bind, Deployment arg)
        {
            q.bind("id", arg.getId());
            q.bind("userId", arg.getUser().getId());
            q.bind("workflowId", arg.getWorkflow() != null ? arg.getWorkflow().getId(): null);
            q.bind("status", arg.getStatus().name());
            q.bind("statusTime", arg.getStatusTime());
            q.bind("description", arg.getDescription());
            q.bind("data", arg.getAsText());
            q.bind("username", arg.getUsername());
        }
    }
}
