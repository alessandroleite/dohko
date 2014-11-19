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

import java.util.ArrayList;
import java.util.List;

import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.domain.WorkflowDescription;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.excalibur.core.workflow.definition.Activity.Transitions.FAILED;
import static org.excalibur.core.workflow.definition.Activity.Transitions.FINISHED;

final class WorkflowImpl implements Workflow
{
    private final WorkflowDescription description_;
    private final Activity            start_;
    private Activity                  last_;
    private final Activity[]          activities_;
    

    WorkflowImpl(WorkflowDescription description, Activity start)
    {
        this.description_ = checkNotNull(description);
        this.start_       = checkNotNull(start);
        checkState(start.isStart() && start.getNumberOfParents() == 0);
        
        List<Activity> activitiesList = getActivities(new ArrayList<Activity>(), start);
        this.activities_  = activitiesList.toArray(new Activity[activitiesList.size()]);
        
        ensureState();
    }

    private void ensureState()
    {
        for (Activity activity : activities_)
        {
            if (activity.isLastActivity())
            {
                if (last_ == null)
                {
                    this.last_ = activity;
                }
                else if (!last_.equals(activity))
                {
                    throw new IllegalArgumentException(String.format("There are more than one finish activity [%s, %s]", last_.getId(), activity.getId()));
                }
            }
            
            if (activity.isStart() && !this.start_.equals(activity))
            {
                throw new IllegalArgumentException(String.format("There are more than one start activity [%s, %s]", start_.getId(), activity.getId()));
            }
        }
    }

    private List<Activity> getActivities(List<Activity> activities, Activity activity)
    {
        if (!activities.contains(activity))
        {
            activities.add(activity);

            for (Activity child : activity.getChildren())
            {
                getActivities(activities, child);
            }
        }

        return activities;
    }

    @Override
    public WorkflowDescription getDescription()
    {
        return description_;
    }

    @Override
    public Activity getStartActivity()
    {
        return this.start_;
    }

    @Override
    public Activity[] getActivities()
    {
        return activities_;
    }

    @Override
    public boolean isFinished()
    {
        return last_.getState().isAny(FINISHED, FAILED);
    }
}
