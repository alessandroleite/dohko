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

import java.util.Date;

import org.excalibur.core.workflow.definition.Activity.Transitions;
import org.excalibur.core.workflow.flow.ActivityExecutionContext;
import org.excalibur.core.workflow.flow.ActivityStateChangedEvent;
import org.excalibur.core.workflow.listener.WorkflowActivityStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;


public class DefaultState implements State<Transitions>
{
    private static final Logger            logger = LoggerFactory.getLogger(DefaultState.class.getName());
    
    private final Object                   lock_ = new Object();
//    private final WorkflowListenerNotifier notifier_;
    private final Activity                 activity_;
    private final EventBus            eventBus_;
    private Transitions                    value_;
    private ActivityExecutionContext       context_;
    
//    private static AtomicInteger count = new AtomicInteger();

//    public DefaultState(Activity activity, Transitions value)
//    {
//        this(activity, value, new SynchronousWorkflowListenerNotifier(), new EventBus());
//    }

    public DefaultState(Activity activity, Transitions value, /*WorkflowListenerNotifier notifier,*/ EventBus eventBus)
    {
        this.activity_ = activity;
//        this.notifier_ = Preconditions.checkNotNull(notifier);
        this.value_ = value;
        
//        Thread t = new Thread(notifier);
//        t.setName(String.format("notifier-%d-activity-%d", count.incrementAndGet(), activity.getId()));
//        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlers.systemExit());
//        t.start();
        this.eventBus_ = eventBus;
    }

    @Override
    public Transitions get()
    {
        synchronized (lock_)
        {
            return this.value_;
        }
    }

    @Override
    public void set(Transitions value)
    {
        synchronized (lock_)
        {
            Transitions oldValue = this.value_;
            this.value_ = value;
            postEvent(oldValue, value);
        }
    }

    @Override
    public Transitions getAndSet(Transitions newValue)
    {
        Transitions oldState = null;
        synchronized (lock_)
        {
            oldState = newValue;
            this.value_ = newValue;
            postEvent(oldState, newValue);
        }
        return oldState;
    }

    @Override
    public boolean compareAndSet(Transitions expected, Transitions newValue)
    {
        boolean changed = false;
        synchronized (lock_)
        {
            if (equals(this.value_, expected))
            {
                this.value_ = newValue;
                changed = true;
                postEvent(expected, newValue);
            }
        }
        return changed;
    }

    @Override
    public boolean is(Transitions state)
    {
        final Transitions currentState = get();
        return equals(currentState, state);
    }

    @Override
    public boolean isAny(Transitions... values)
    {
        if (values != null && values.length > 0)
        {
            final Transitions currentState = get();

            for (Transitions value : values)
            {
                if (equals(currentState, value))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString()
    {
        Transitions currentState = get();
        return currentState == null ? "null state" : currentState.toString();
    }

    @Override
    public void notifyOnStateChanges(WorkflowActivityStateListener... listeners)
    {
//        this.notifier_.add(listeners);
        for (WorkflowActivityStateListener listener: listeners)
        {
            this.eventBus_.register(listener);    
        }
    }

    @Override
    public void setContext(ActivityExecutionContext context)
    {
        synchronized (lock_)
        {
            this.context_ = context;
        }
    }
    
    /**
     * Enqueue a {@link ActivityStateChangedEvent} with the givens state.
     * 
     * @param transition
     *            The state to be enqueued.
     */
    private void postEvent(Transitions from, Transitions to)
    {
        logger.debug("Changed the state of activity [id:{}, label:{}] from {} to {}", this.activity_.getId(), this.activity_.getLabel(),
                from.name(), to.name());
        
        Date now = new Date();
//        activity_.getDescription().setState(to.name());
//        activity_.getDescription().setLastStateTime(now);
        //this.notifier_.fireEvent(new ActivityStateChangedEvent(this, activity_, from, to, now, context_));
        this.eventBus_.post(new ActivityStateChangedEvent(this, activity_, from, to, now, context_));
    }
    
    /**
     * Returns <code>true</code> if the two values are equal, handling <code>null</code> pointers gracefully.
     * 
     * @param value1 The value to compare.
     * @param value2 The value to compare.
     * @return <code>true</code> if the two values are equals.
     */
    private boolean equals(Object value1, Object value2)
    {
        if (value1 == value2)
        {
            return true;
        }
        if (value1 == null || value2 == null)
        {
            return false;
        }
        return value1.equals(value2);
    }
}
