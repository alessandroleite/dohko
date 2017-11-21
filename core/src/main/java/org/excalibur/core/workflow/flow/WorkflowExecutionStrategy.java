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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.listener.AbstractWorkflowActivityStateListener;
import org.excalibur.core.workflow.listener.WorkflowActivityStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import static org.excalibur.core.workflow.definition.Activity.Transitions.READY;

public class WorkflowExecutionStrategy
{
    private static final Logger logger = LoggerFactory.getLogger(WorkflowExecutionStrategy.class.getName());
    
    private final AtomicInteger count_ = new AtomicInteger(0);
    private final WorkflowActivityStateListener stateListener_ = new WorkflowExecutionFlowListener();
    private final Workflow workflow_;
    private final Object monitor = new Integer(1);

    public WorkflowExecutionStrategy(Workflow workflow)
    {
        this.workflow_ = workflow;

        for (Activity activity : workflow.getActivities())
        {
            activity.register(stateListener_);
        }
    }

    public boolean hashNext()
    {
        return this.count_.get() < workflow_.getActivities().length;
    }

    public Activity[] nextActivities() throws InterruptedException
    {
        synchronized (monitor)
        {
            List<Activity> readyActivities = getReadyStateActivities();

            if (hashNext() && !workflow_.isFinished() && readyActivities.isEmpty())
            {
                logger.debug("There is no ready activity. Going to wait ...");
                monitor.wait();
                logger.debug("Woke up from the wait!");
                readyActivities = getReadyStateActivities();
            }

            return readyActivities.toArray(new Activity[readyActivities.size()]);
        }
    }

    private List<Activity> getReadyStateActivities()
    {
        List<Activity> readyActivities = Lists.newArrayList();

        for (Activity activity : workflow_.getActivities())
        {
            if (activity.isReadyToExecute() || activity.getState().is(READY))
            {
                readyActivities.add(activity);
            }
        }
        return readyActivities;
    }

    private class WorkflowExecutionFlowListener extends AbstractWorkflowActivityStateListener
    {
        @Subscribe
        @Override
        public void stateChanged(ActivityStateChangedEvent event)
        {
            super.stateChanged(event);
            switch (event.getNewState())
            {
            case FAILED:
                // event.getContext().getConfiguration().getMaximumRetries();
                // TODO check the number of maximum retries and fail when it is reached.
//                event.getActivity().getState().compareAndSet(FAILED, READY);
                count_.incrementAndGet();
                break;
            case FINISHED:
//                logger.debug("number of completed activites: {}", count_.incrementAndGet());
                count_.incrementAndGet();
                break;
            default:
                break;
            }
            
            synchronized (monitor)
            {
                monitor.notifyAll();
            }
        }
    }
}
