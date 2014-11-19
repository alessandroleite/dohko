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

import java.util.Date;

import org.excalibur.core.util.EventListener;
import org.excalibur.core.util.EventObject;
import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.definition.Activity.Transitions;
import org.excalibur.core.workflow.listener.WorkflowActivityStateListener;

public class ActivityStateChangedEvent extends EventObject<Activity, Transitions>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7270990494025017330L;
  
    private final ActivityExecutionContext context_;
    private final Date                     stateTime_;
    private final Transitions              oldState_;

    public ActivityStateChangedEvent(Object source, Activity activity, Transitions oldState, Transitions newState, ActivityExecutionContext context)
    {
        this(source, activity, oldState, newState, new Date(), context);
    }

    public ActivityStateChangedEvent(Object source, Activity activity, Transitions oldState, Transitions newState, Date stateTime,
            ActivityExecutionContext context)
    {
        super(source, activity, newState);
        this.context_ = context;
        this.stateTime_ = stateTime;
        this.oldState_ = oldState;
    }

    public final Activity getActivity()
    {
        return getValue();
    }
    
    public final Transitions getOldState()
    {
        return this.oldState_;
    }

    /**
     * @return the state
     */
    public final Transitions getNewState()
    {
        return getType();
    }

    /**
     * @return the context
     */
    public ActivityExecutionContext getContext()
    {
        return context_;
    }

    public Date getStateTime()
    {
        return this.stateTime_;
    }

    @Override
    public void processListener(EventListener listener)
    {
        ((WorkflowActivityStateListener) listener).stateChanged(this);
    }

    @Override
    public boolean isAppropriateListener(EventListener listener)
    {
        return listener instanceof WorkflowActivityStateListener;
    }
}
