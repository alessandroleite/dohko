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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.definition.Activity.Transitions;
import org.excalibur.core.workflow.definition.ActivityImpl;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowDescription;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class WorkflowBuilder
{
    private WorkflowDescription description_;
    private EventBus workflowEventBus_;
    
    public WorkflowBuilder description(WorkflowDescription workflowDescription)
    {
        this.description_ = workflowDescription;
        return this;
    }
    
    public WorkflowBuilder eventBus(EventBus eventBus)
    {
        this.workflowEventBus_ = eventBus;
        return this;
    }
    
    public Workflow build()
    {
        checkNotNull(description_);
        
        if (workflowEventBus_ == null)
        {
            ThreadFactory threadFactory = new ThreadFactoryBuilder()
//                    .setDaemon(true)
                    .setNameFormat("event-bus-for-workflow-" + description_.getName() + "-%d")
                    .build();
            workflowEventBus_ = new AsyncEventBus(DynamicExecutors.newScalingThreadPool(description_.getActivitesMap().size(), 
                    100, 3, TimeUnit.MINUTES, threadFactory));
        }
        
        Map<Integer, Activity> activities = new HashMap<Integer, Activity>();
        createActivitiesFromWorkflowDescription(description_.getActivitesMap(), activities);
        Activity first = activities.get(description_.getStartActivityId());
        
        checkState(first.isStart());
        
        return new WorkflowImpl(description_, first);
    }

    private void createActivitiesFromWorkflowDescription(Map<Integer, WorkflowActivityDescription> workflowActivities, Map<Integer, Activity> activities)
    {
        for (WorkflowActivityDescription metadata : workflowActivities.values())
        {
            createActivity(metadata, workflowActivities, activities);
        }
    }

    private Activity createActivity(final WorkflowActivityDescription activityMetadata,
            final Map<Integer, WorkflowActivityDescription> workflowActivities, final Map<Integer, Activity> activities)
    {
        Activity activity = activities.get(activityMetadata.getId());

        if (activity == null)
        {
            activity = new ActivityImpl(activityMetadata, workflowEventBus_);
            activities.put(activity.getId(), activity);

            for (WorkflowActivityDescription dependency : activityMetadata.getDependencies())
            {
                Activity parent = createActivity(dependency, workflowActivities, activities);
                activity.addParent(parent);
            }

            checkState(activityMetadata.getNumberOfDependencies() == activity.getNumberOfParents());

            if (activity.isStart())
            {
                activity.getState().set(Transitions.READY);
            }
            else
            {
                activity.getState().set(Transitions.WAITING);
            }
        }
        return activity;
    }
}
