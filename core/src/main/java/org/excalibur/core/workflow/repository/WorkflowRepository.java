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
package org.excalibur.core.workflow.repository;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.excalibur.core.domain.User;
import org.excalibur.core.repository.bind.BindBean;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowActivityState;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.repository.WorkflowRepository.BindWorkflow.WorkflowBinderFactory;
import org.excalibur.core.workflow.repository.WorkflowRepository.BindWorkflowActivity.WorkflowActivityBinderFactory;
import org.excalibur.core.workflow.repository.WorkflowRepository.WorkflowRowMapper;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(WorkflowRowMapper.class)
public interface WorkflowRepository
{
    /**
     * @param workflow
     */
    @SqlUpdate("INSERT INTO workflow (user_id, start_activity_id, created_in, finished_in, name) "
            + " VALUES (:user, :startActivityId, :createdIn, :finishedIn, :name)")
    @GetGeneratedKeys
    Integer insert(@BindWorkflow WorkflowDescription workflow);
    
    @SqlUpdate("UPDATE workflow set finished_in = :finishedIn where user_id = :user and id = :id")
    void update(@BindWorkflow WorkflowDescription workflow);

    /**
     * Returns all workflows of a user.
     * 
     * @param userId
     *            The user who the workflows belong.
     * @return A not <code>null</code> list with the workflows.
     */
    @SqlQuery("SELECT id as workflow_id, user_id, start_activity_id, created_in, finished_in, name FROM workflow WHERE user_id = :userId")
    List<WorkflowDescription> getUserWorkflows(@Bind("userId") Integer userId);

    /**
     * Returns the {@link WorkflowDescription} in execution of a given user.
     * 
     * @param userId
     *            The user who the workflows belong to.
     * @return A not <code>null</code> {@link List} with the {@link WorkflowDescription} in execution if there is one, or an empty {@link List} otherwise.
     */
    @SqlQuery("SELECT id as workflow_id, user_id, start_activity_id, created_in, finished_in, name FROM workflow WHERE user_id = :userId and finished_in is null")
    List<WorkflowDescription> getUncompletUserWorkflows(Integer userId);
    
    /**
     * Returns the {@link WorkflowDescription} wich has the given id or <code>null</code> if it does not exist.
     * @param id The workflow  id.
     * @return <code>null</code> if the workflow does not exist.
     */
    @SqlQuery("SELECT id as workflow_id, user_id, start_activity_id, created_in, finished_in, name FROM workflow WHERE id = :id")
    WorkflowDescription findWorkflowById(@Bind("id") Integer id);
    
    // ///////////////////////////////////////////////////////////////////
    //                         Workflow Activity                        //
    // ///////////////////////////////////////////////////////////////////
    
    String SQL_INSERT_WORKFLOW_ACTIVITY = "insert into workflow_activity(activity_id, workflow_id, label, type, parents) " +
    		" VALUES (:activity_id, :workflow_id, :label, :type, :parents)";
    
    @SqlBatch(SQL_INSERT_WORKFLOW_ACTIVITY)
    @BatchChunkSize(10)
    void insert(@BindWorkflowActivity Iterable<WorkflowActivityDescription> activities);
    
    @SqlUpdate(SQL_INSERT_WORKFLOW_ACTIVITY)
    @GetGeneratedKeys
    Integer insert(@BindWorkflowActivity WorkflowActivityDescription activity);
    
    @RegisterMapper(WorkflowActivityRowMapper.class)
    @SqlQuery("SELECT id as global_id, activity_id, workflow_id, label, type, parents FROM workflow_activity WHERE workflow_id = :workflowId and activity_id = :activityId")
    WorkflowActivityDescription findWorkflowActivityById(@Bind("workflowId") Integer workflowId, @Bind("activityId") Integer workflowActivityId);
    
    @SqlUpdate("UPDATE workflow_activity SET label = :label, type = :type, parents = :parents WHERE activity_id = :activity_id and workflow_id = :workflow_id ")
    void update(@BindWorkflowActivity WorkflowActivityDescription activity);
    
    @RegisterMapper(WorkflowActivityRowMapper.class)
    @SqlQuery("SELECT id as global_id, activity_id, workflow_id, label, type, parents FROM workflow_activity WHERE workflow_id = :workflowId order by activity_id")
    List<WorkflowActivityDescription> getWorkflowActivities(@Bind("workflowId") Integer workflowId);
    
    @SqlUpdate("INSERT INTO workflow_activity_state_history (workflow_activity_id, state, state_time, message) VALUES (:activity.internalId, :state, :updateTime, :message)")
    @GetGeneratedKeys
    Integer insertActivityState(@BindBean WorkflowActivityState state);
    
    
    // ///////////////////////////////////////////////////////////////////
    //                      Binders and Mappers                         //
    // ///////////////////////////////////////////////////////////////////
    
    
    @org.skife.jdbi.v2.sqlobject.BindingAnnotation(WorkflowBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindWorkflow
    {
        public static class WorkflowBinderFactory implements BinderFactory
        {
            @Override
            public Binder<BindWorkflow, WorkflowDescription> build(Annotation annotation)
            {
                return new Binder<BindWorkflow, WorkflowDescription>()
                {
                    @Override
                    public void bind(SQLStatement<?> q, BindWorkflow bind, WorkflowDescription arg)
                    {
                        q.bind("createdIn", arg.getCreatedIn());
                        q.bind("finishedIn", arg.getFinishedIn());
                        q.bind("id", arg.getId());
                        q.bind("name", arg.getName());
                        q.bind("startActivityId", arg.getStartActivityId());
                        q.bind("user", arg.getUser().getId());
                    }
                };
            }
        }
    }
    
    @org.skife.jdbi.v2.sqlobject.BindingAnnotation(WorkflowActivityBinderFactory.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    public @interface BindWorkflowActivity
    {
        public static class WorkflowActivityBinderFactory implements BinderFactory
        {
            @Override
            public Binder<BindWorkflowActivity, WorkflowActivityDescription> build(Annotation annotation)
            {
                return new Binder<WorkflowRepository.BindWorkflowActivity, WorkflowActivityDescription>()
                {
                    @Override
                    public void bind(SQLStatement<?> q, BindWorkflowActivity bind, WorkflowActivityDescription arg)
                    {
                        q.bind("activity_id", arg.getId());
                        q.bind("workflow_id", arg.getWorkflow().getId());
                        q.bind("label", arg.getLabel());
                        q.bind("type", arg.getType());
                        q.bind("parents", arg.getParents());
                    }
                };
            }
            
        }
    }

    public static final class WorkflowRowMapper implements ResultSetMapper<WorkflowDescription>
    {
        @Override
        public WorkflowDescription map(int index, ResultSet rs, StatementContext ctx) throws SQLException
        {
            return new WorkflowDescription()
                    .setCreatedIn(rs.getDate("created_in"))
                    .setFinishedIn(rs.getDate("finished_in"))
                    .setId(rs.getInt("workflow_id"))
                    .setName(rs.getString("name"))
                    .setStartActivityId(rs.getInt("start_activity_id"))
                    .setUser(new User().setId(rs.getInt("user_id")));
        }
    }
    
    public static final class WorkflowActivityRowMapper implements ResultSetMapper<WorkflowActivityDescription>
    {
        @Override
        public WorkflowActivityDescription map(int index, ResultSet rs, StatementContext ctx) throws SQLException
        {
            return new WorkflowActivityDescription()
                    .setParents(rs.getString("parents"))
                    .setId(rs.getInt("activity_id"))
                    .setInternalId(rs.getInt("global_id"))
                    .setLabel(rs.getString("label"))
                    .setType(rs.getString("type"))
                    .setWorkflow(new WorkflowDescription(rs.getInt("workflow_id")));
        }
    }
    
    /**
     * Close the SQL connection.
     */
    void close();
}
