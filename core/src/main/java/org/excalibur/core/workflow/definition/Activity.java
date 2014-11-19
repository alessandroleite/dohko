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
package org.excalibur.core.workflow.definition;

import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.flow.ActivityExecutionContext;
import org.excalibur.core.workflow.listener.WorkflowActivityStateListener;

public interface Activity
{
    /**
     * The core transition states of the activity.
     */
    public enum Transitions
    {
        CREATED, INITIALISED, STARTING, STARTED, EXECUTING, STOPPING, STOPPED, FAILED, FINISHED, READY, WAITING;
    };

    /**
     * Returns the current running state of this activity.
     * 
     * @return The current state of this activity.
     */
    State<Transitions> getState();

    /**
     * Returns the parents of this {@link Activity}. Every non-start activity has at least one parent.
     * 
     * @return The parents of this {@link Activity} or an empty array if this {@link Activity} is a start activity.
     */
    Activity[] getParents();

    /**
     * <p>
     * Adds a parent for this {@link Activity}. Self-reference and <code>null</code> are not allowed.
     * 
     * <p>
     * <strong>Notice:</strong> This method can only be called before the activity starts.
     * 
     * @param parent
     *            The parent of this {@link Activity}. Must not be <code>null</code> nor a reference for this {@link Activity}.
     * @throws IllegalStateException
     *             If this {@link Activity} had already started, finished or failed.
     * @see #getState()
     */
    void addParent(Activity parent);

    /**
     * Returns the children of this {@link Activity}.
     * 
     * @return the children of this activity or an empty array if this {@link Activity} is a finish activity.
     */
    Activity[] getChildren();

    /**
     * <p>
     * Adds a child for this {@link Activity}. Self-reference and <code>null</code> are not allowed.
     * 
     * <p>
     * <strong>Notice:</strong> This method can only be called before the activity starts.
     * 
     * @param child
     *            A child of this {@link Activity}. Must not be <code>null</code> nor a self-reference.
     * @throws IllegalStateException
     *             If this {@link Activity} had already started, finished or failed.
     * @see #getState()
     */
    void addChild(Activity child);

    /**
     * Returns the id of this activity.
     * 
     * @return The id of this activity.
     */
    Integer getId();

    /**
     * Returns the label of this activity.
     * 
     * @return The label of this activity.
     */
    String getLabel();

    /**
     * Returns the description of this {@link Activity}. In other words, the data about the activity.
     * 
     * @return the description of this {@link Activity}. In other words, the data about the activity.
     */
    WorkflowActivityDescription getDescription();

    /**
     * Starts the activity. Once it is started it can take an arbitrary amount of time to complete. The execution of an activity is usually
     * asynchronous in nature (though its not mandatory). The work of an activity depends of the associated tasks.
     */
    void execute(ActivityExecutionContext context);

    /**
     * Stops the activity, setting the status to {@link Transitions#STOPPED}.
     * 
     * @param context
     *            The activity execution context.
     */
    void stop(ActivityExecutionContext context);

    /**
     * Registers a listener to be notified when the state changes.
     * 
     * @param listeners
     *            The listener to notify when the state changes. <code>null</code> values are ignored.
     */
    void register(WorkflowActivityStateListener... listeners);

    /**
     * Returns the number of children of this activity.
     * 
     * @return the number of children.
     */
    int getNumberOfChildren();

    /**
     * Returns the number of parents of this activity.
     * 
     * @return the number of parents.
     */
    int getNumberOfParents();

    /**
     * <p>
     * Returns <code>true</code> if this activity can be executed. In other words, if its parents have all executed successfully.
     * 
     * <p>
     * <strong>Warning:</strong> this method returns <code>false</code> if this activity is executing or either if it had failed or terminated. In
     * this case, sometimes is necessary to check the state calling the method {@link #getState()}.
     * 
     * @return <code>true</code> if this the state of parents is {@link Transitions#FINISHED}, otherwise <code>false</code>.
     * @see #getState()
     */
    boolean isReadyToExecute();

    /**
     * Returns <code>true</code> if this is a start activity. In other words, the return is <code>null</code> if there is no parent for this activity.
     * 
     * @return <code>true</code> if there is no parent for this task.
     */
    boolean isStart();

    /**
     * Returns <code>true</code> if this is the last activity.
     * 
     * @return <code>true</code> if this is the last task, otherwise <code>false</code>.
     */
    boolean isLastActivity();
}
