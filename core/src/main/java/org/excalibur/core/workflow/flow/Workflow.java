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
package org.excalibur.core.workflow.flow;

import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.domain.WorkflowDescription;

/**
 * <p>
 * A workflow model is composed of a number of activities which are connected in the form of a directed graph. An executing instance of a workflow
 * model is called a case or process instance. There may be multiple cases of a particular workflow model running simultaneously, however each of
 * these is assumed to have an independent existence and they typically execute without reference to each other.
 * 
 * <p>
 * There is usually a <strong>unique start task</strong> and a <strong>unique final task</strong> in a workflow. These are the tasks that are first to
 * run and last to run in a given workflow case. Each invocation of a task is termed a task instance.
 * 
 * <p>
 * To more information about workflow model, please read the text available on <a
 * href="http://www.workflowpatterns.com/patterns/data/workflow_structure.php">Workflow Structure</a>.
 */
public interface Workflow
{
    /**
     * Returns the metadata of the workflow.
     * 
     * @return the workflow's metadata. It might not be <code>null</code>.
     */
    WorkflowDescription getDescription();

    /**
     * Returns the start activity.
     * 
     * @return the start activity. It must not be <code>null</code>.
     */
    Activity getStartActivity();

    /**
     * Returns the activities of this {@link Workflow}.
     * 
     * @return the workflow's activities.
     */
    Activity[] getActivities();

    /**
     * Returns <code>true</code> when the last activity finishes.
     * 
     * @return <code>true</code> if the last activity was executed, otherwise <code>false</code>.
     */
    boolean isFinished();
}
