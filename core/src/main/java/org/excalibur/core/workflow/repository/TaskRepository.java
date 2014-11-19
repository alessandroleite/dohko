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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.repository.bind.BindBean;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.util.YesNoEnum;
import org.excalibur.core.workflow.domain.DataType;
import org.excalibur.core.workflow.domain.TaskDataDescription;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.domain.TaskDescriptionState;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.repository.TaskRepository.TaskDescriptionMapper;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@RegisterMapper(TaskDescriptionMapper.class)
public interface TaskRepository
{
    // ///////////////////////////////////////////////////////////////////
    //                          Task methods                            //
    // ///////////////////////////////////////////////////////////////////
    
    String SQL_INSERT_WORKFLOW_ACTIVITY_TASK = " INSERT INTO workflow_activity_task (workflow_activity_id, executable, type)\n " +
                                               " VALUES (:activity.internalId, :executable, :typeClass)";
    
    String SQL_SELECT_ALL_WORKFLOW_ACTIVITY_TASKS =
            " SELECT t.id as task_id, t.workflow_activity_id, t.executable, t.type, w.id as workflow_activity_global_id\n" +
            " FROM workflow_activity_task t\n" +
            " JOIN workflow_activity w on w.id = t.workflow_activity_id\n";
    
    @SqlUpdate (SQL_INSERT_WORKFLOW_ACTIVITY_TASK)
    @GetGeneratedKeys
    Integer insert(@BindBean TaskDescription task);
    
    @SqlBatch(SQL_INSERT_WORKFLOW_ACTIVITY_TASK)
    @BatchChunkSize(10)
    void insert(@BindBean Collection<TaskDescription> tasks);
    
    @SqlQuery(SQL_SELECT_ALL_WORKFLOW_ACTIVITY_TASKS + " WHERE t.id = :id")
    TaskDescription findTaskById(@Bind("id") Integer id);
    
    @SqlQuery(SQL_SELECT_ALL_WORKFLOW_ACTIVITY_TASKS + " WHERE t.workflow_activity_id = :activityId order by t.id")
    List<TaskDescription> getTasksOfActivity(@Bind("activityId") Integer activityId);
    
    // ///////////////////////////////////////////////////////////////////
    //                          Task's state                            //
    // ///////////////////////////////////////////////////////////////////
    
    
    @SqlUpdate("INSERT INTO workflow_activity_task_state (activity_task_id, node_id, state, state_time, state_message)\n" +
              "VALUES (:task.id, :node.id, :state.id, :stateTime, :message)")
    @GetGeneratedKeys
    Integer insertTaskState(@BindBean TaskDescriptionState state);

    /**
     * Returns the states of a given task ordered ascending by time. 
     * @param taskId The task to return its states. Must not be <code>null</code>.
     * @return The task's states or an empty {@link List} if there is not one.
     */
    @RegisterMapper(TaskDescriptionStateMapper.class)
    @SqlQuery(" SELECT id as task_state_id, activity_task_id, node_id, state, state_time, state_message FROM workflow_activity_task_state\n " +
              " WHERE activity_task_id = :taskId ORDER BY state_time asc")
    List<TaskDescriptionState> getTaskStates(@Bind("taskId")Integer taskId);
    
    
    // ///////////////////////////////////////////////////////////////////
    //                           Task data                              //
    // ///////////////////////////////////////////////////////////////////
    
    @SqlUpdate(" INSERT INTO workflow_activity_task_data (activity_task_id, type, is_dynamic, name, path, size_gb, is_splittable, description)\n" +
              " VALUES (:task.id, :type.id, :dynamic.id, :name, :path, :sizeGb, :splittable.id, :description)")
    @GetGeneratedKeys
    Integer insertTaskData(@BindBean TaskDataDescription data);
    
    @SqlBatch(" INSERT INTO workflow_activity_task_data (activity_task_id, type, is_dynamic, name, path, size_gb, is_splittable, description)\n" +
              " VALUES (:task.id, :type.id, :dynamic.id, :name, :path, :sizeGb, :splittable.id, :description)")
    @BatchChunkSize(10)
    void insertTaskData(@BindBean Collection<TaskDataDescription> data);
    
    @RegisterMapper(TaskDataDescriptionMapper.class)
    @SqlQuery(" SELECT id as task_data_id, activity_task_id, type, is_dynamic, name, path, size_gb, is_splittable, description " +
              " FROM workflow_activity_task_data\n" +
              " WHERE id = :id")
    TaskDataDescription findTaskDataDescriptionById(@Bind("id") Integer id);
    
    @RegisterMapper(TaskDataDescriptionMapper.class)
    @SqlQuery(" SELECT id as task_data_id, activity_task_id, type, is_dynamic, name, path, size_gb, is_splittable,\n " +
              " description FROM workflow_activity_task_data\n" +
              " WHERE activity_task_id = :taskId ORDER BY type, id")
    List<TaskDataDescription> getDataOfTask(@Bind("taskId") Integer taskId);
    
    
    // ///////////////////////////////////////////////////////////////////
    //                            Mappers                               //
    // ///////////////////////////////////////////////////////////////////
    
    static final class TaskDescriptionMapper implements ResultSetMapper<TaskDescription>
    {
        @Override
        public TaskDescription map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            return new TaskDescription(r.getInt("task_id"), 
                    new WorkflowActivityDescription(r.getInt("workflow_activity_id")).setInternalId(r.getInt("workflow_activity_global_id")))
                    .setExecutable(r.getString("executable"))
                    .setTypeClass(r.getString("type"));
        }
    }
    
    static final class TaskDataDescriptionMapper implements ResultSetMapper<TaskDataDescription>
    {
        @Override
        public TaskDataDescription map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            TaskDataDescription data = new TaskDataDescription(r.getInt("task_data_id"));
            data.setDescription(r.getString("description"))
                .setDynamic(YesNoEnum.valueOf(r.getString("is_dynamic").charAt(0)))
                .setName(r.getString("name"))
                .setPath(r.getString("path"))
                .setSizeGb(r.getBigDecimal("size_gb"))
                .setSplittable(YesNoEnum.valueOf(r.getString("is_splittable").charAt(0)))
                .setTask(new TaskDescription(r.getInt("activity_task_id")))
                .setType(DataType.valueOf(r.getString("type").charAt(0)));
            
            return data;
        }
    }
    
    static final class TaskDescriptionStateMapper implements ResultSetMapper<TaskDescriptionState>
    {
        @Override
        public TaskDescriptionState map(int index, ResultSet r, StatementContext ctx) throws SQLException
        {
            TaskDescriptionState state = new TaskDescriptionState(r.getInt("task_state_id"));
            state.setMessage(r.getString("state_message"))
                 .setNode(new VirtualMachine(r.getInt("node_id")))
                 .setState(TaskState.valueOf(r.getInt("state")))
                 .setTask(new TaskDescription(r.getInt("activity_task_id")))
                 .setStateTime(r.getTimestamp("state_time"));
            
            return state;
        }
    }
}
