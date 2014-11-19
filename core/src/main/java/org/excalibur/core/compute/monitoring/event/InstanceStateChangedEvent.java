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
package org.excalibur.core.compute.monitoring.event;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.service.provisioning.InstanceEvent;
import org.excalibur.core.compute.monitoring.domain.provisioning.listener.InstanceStateChangedListener;
import org.excalibur.core.util.EventListener;

public class InstanceStateChangedEvent extends InstanceEvent
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 1474880847608455936L;

    private final InstanceStateType oldState;

    public InstanceStateChangedEvent(Object source, VirtualMachine vm, InstanceStateType oldState, InstanceStateType newState)
    {
        super(source, vm, newState);
        this.oldState = oldState;
    }

    @Override
    public void processListener(EventListener listener)
    {
        ((InstanceStateChangedListener) listener).stateChanged(this);
    }

    @Override
    public boolean isAppropriateListener(EventListener listener)
    {
        return (listener instanceof InstanceStateChangedListener);
    }

    /**
     * @return the oldState
     */
    public InstanceStateType getOldState()
    {
        return oldState;
    }

    public InstanceStateType getNewState()
    {
        return this.getState();
    }
}
