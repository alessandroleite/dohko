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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.excalibur.core.executor.ExecutionEnvironmentBuilder;
import org.excalibur.core.executor.task.DefaultExecutionContext;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskContextBuilder;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskType;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.flow.ActivityExecutionContext;
import org.excalibur.core.workflow.flow.ActivityStateChangedEvent;
import org.excalibur.core.workflow.listener.WorkflowActivityStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.FutureCallback;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.excalibur.core.workflow.definition.Activity.Transitions.EXECUTING;
import static org.excalibur.core.workflow.definition.Activity.Transitions.FAILED;
import static org.excalibur.core.workflow.definition.Activity.Transitions.FINISHED;
import static org.excalibur.core.workflow.definition.Activity.Transitions.READY;
import static org.excalibur.core.workflow.definition.Activity.Transitions.STOPPED;
import static org.excalibur.core.workflow.definition.Activity.Transitions.STOPPING;
import static org.excalibur.core.workflow.definition.Activity.Transitions.WAITING;

public class ActivityImpl implements Activity, WorkflowActivityStateListener
{
    private static final Logger               logger = LoggerFactory.getLogger(ActivityImpl.class.getName());
    
    private final Object                      lock_ = new Integer(1);
    private final State<Transitions>          state_;
    private final StateDetails                stateDetails_;
    private final WorkflowActivityDescription description_;
    private final List<Activity>              parents_;
    private final List<Activity>              children;

    public ActivityImpl(WorkflowActivityDescription description, EventBus eventBus)
    {
        description_  = checkNotNull(description);
        checkNotNull(eventBus);
        
        state_ = new DefaultState(this, Transitions.INITIALISED, eventBus);
        stateDetails_ = new StateDetails(state_);
        this.parents_ = new ArrayList<Activity>();
        this.children = new ArrayList<Activity>();
        eventBus.register(this);
    }

    @Override
    public void register(WorkflowActivityStateListener... listeners)
    {
        this.state_.notifyOnStateChanges(listeners);
    }
    
    @Override
    public final void execute(final ActivityExecutionContext context)
    {
        if (!state_.is(EXECUTING))
        {
            state_.setContext(context);
        }
        
        if (state_.compareAndSet(READY, EXECUTING))
        {
            List<TaskContext> tasks = Lists.newArrayList();
            
            for (final TaskType<?> task : context.getTasks())
            {
                DefaultExecutionContext executionContext = new DefaultExecutionContext(context, context.getUserRepository(), 
                         new ExecutionEnvironmentBuilder().location(context.getLocation()).build());
                
                TaskContextBuilder builder = new TaskContextBuilder()
                         .setTask(task)
                         .setParentContext(context)
                         .setUserRepository(context.getUserRepository())
                         .setExecutionContext(executionContext)
                         .setTaskRepository(context.getTaskRepository())
                         .setRegionRepository(context.getRegionRepository());
                
                tasks.add(builder.build());
            }
            
            final List<TaskResult<Serializable>> results = Lists.newArrayList();
            
            logger.info("Submitted [{}] task(s) of activity [id:{}, label:{}].", tasks.size(), this.getId(), this.getLabel());
            
            context.getTaskExecutionService().invokeAllAndWait(tasks, new FutureCallback<TaskResult<Serializable>>()
            {
                @Override
                public void onSuccess(@Nullable TaskResult<Serializable> result)
                {
                    results.add(result);
                }

                @Override
                public void onFailure(Throwable t)
                {
                   fail(t.getMessage(), t);
                }
            });
            
            logger.info("Finished the tasks of activity [id:{}, label:{}]. ", this.getId(), this.getLabel());
            state_.compareAndSet(EXECUTING, FINISHED);
        }
    }

    @Override
    public final void stop(ActivityExecutionContext context)
    {
        if (!state_.isAny(Transitions.STOPPED, Transitions.FAILED, Transitions.FINISHED))
        {
            state_.set(Transitions.STOPPED);
        }
    }

    @Override
    public Activity[] getParents()
    {
        synchronized (lock_)
        {
            return this.parents_.toArray(new Activity[this.parents_.size()]);
        }
    }

    @Override
    public void addParent(Activity parent)
    {
        synchronized (lock_)
        {
            ensureValidState(parent);
            if (!this.parents_.contains(parent))
            {
                this.parents_.add(parent);
                parent.addChild(this);
                parent.register(this);
                
                if (this.getState().is(READY) && !isReadyToExecute())
                {
                    if (!this.getState().compareAndSet(READY, WAITING))
                    {
                        logger.warn("Activity [{}] added a not finished parent [{}] and it could not transition from {} state to {} state.", 
                                getLabel(), parent.getLabel(), READY, WAITING);
                    }
                }
            }
        }
    }
    
    @Override
    public void addChild(Activity child)
    {
        synchronized (lock_)
        {
            ensureValidState(child);
            this.children.add(child);
        }
    }

    private void ensureValidState(Activity activity)
    {
        checkState(!this.getState().isAny(EXECUTING, FINISHED, FAILED/*, READY*/));
        checkState(activity != null && !this.equals(activity));
    }
    
    @Override
    public Activity[] getChildren()
    {
        synchronized (lock_)
        {
            return this.children.toArray(new Activity[children.size()]);
        }
    }
    
    @Override
    public int getNumberOfChildren()
    {
        synchronized (lock_)
        {
            return children.size();
        }
    }

    @Override
    public int getNumberOfParents()
    {
        synchronized (lock_)
        {
            return parents_.size();
        }
    }
    
    @Override
    public final boolean isReadyToExecute()
    {
        boolean ready = !getState().isAny(EXECUTING, FAILED, FINISHED, STOPPING, STOPPED);
        
        synchronized (lock_)
        {
            for (Activity parent : this.parents_)
            {
                if (getState().isAny(EXECUTING, FAILED, FINISHED, STOPPING, STOPPED))
                {
                    ready = false;
                    break;
                }
                
                if (parent.getState().isAny(EXECUTING, FAILED, READY, STOPPING, STOPPED, WAITING))
                {
                    ready = false;
                    break;
                }
            }
        }
        return ready;
    }
    
    @Override
    public boolean isStart()
    {
        return this.getNumberOfParents() == 0;
    }

    @Override
    public boolean isLastActivity()
    {
        return this.getNumberOfChildren() == 0;
    }

    @Override
    public State<Transitions> getState()
    {
        return this.state_;
    }

    @Override
    public final Integer getId()
    {
        return this.getDescription().getId();
    }

    @Override
    public final String getLabel()
    {
        return getDescription().getLabel();
    }

    @Override
    public WorkflowActivityDescription getDescription()
    {
        return this.description_;
    }

    public final void fail(String reason)
    {
        this.stateDetails_.setFailureReason(reason);
        this.state_.set(FAILED);
    }

    /**
     * @param reason
     * @param cause
     */
    public final void fail(String reason, Throwable cause)
    {
        this.fail(reason);
        this.stateDetails_.setFailure(reason, cause);
    }

    /**
     * Returns <code>true</code> if the activity state was stopped or failure.
     * 
     * @return <code>true</code> if the activity was either stopped or failure.
     */
    public boolean isStopped()
    {
        return state_.isAny(STOPPED, FAILED);
    }

    /**
     * Returns <code>true</code> if the activity failure.
     * 
     * @return <code>true</code> if the activity failure.
     */
    public boolean isFailed()
    {
        return state_.is(FAILED);
    }

    @Override
    public String toString()
    {
        return this.description_.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ActivityImpl))
        {
            return false;
        }

        ActivityImpl other = (ActivityImpl) obj;

        return this.getId().equals(other.getId());
    }

    @Override
    public int hashCode()
    {
        return this.getId().hashCode() * 17;
    }

    @Subscribe
    @Override
    public void stateChanged(ActivityStateChangedEvent event)
    {
        if (event != null && this.getState().is(WAITING))
        {
            boolean isMyParent = this.parents_.contains(event.getActivity());
            
            if (isMyParent)
            {
                logger.debug("Activity [{}] received the event [{}] from activity [{}]", 
                        this.getLabel(), event.getType().name(), event.getActivity().getLabel());
            }

            if (isMyParent && FINISHED.equals(event.getNewState()))
            {
                boolean amIready = this.isReadyToExecute();

                if (amIready)
                {
                    this.getState().compareAndSet(WAITING, READY);
                }
            }
        }
    }
}
