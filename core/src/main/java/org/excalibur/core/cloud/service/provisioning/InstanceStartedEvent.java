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
package org.excalibur.core.cloud.service.provisioning;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.compute.monitoring.domain.provisioning.listener.InstanceListener;
import org.excalibur.core.util.EventListener;

public class InstanceStartedEvent extends InstanceEvent
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7168920781602772887L;

    public InstanceStartedEvent(Object source, VirtualMachine vm)
    {
        super(source, vm, InstanceStateType.RUNNING);
    }

    @Override
    public void processListener(EventListener listener)
    {
        ((InstanceListener) listener).onStarted(this);
    }

    @Override
    public boolean isAppropriateListener(EventListener listener)
    {
        return listener instanceof InstanceListener && InstanceStateType.RUNNING.equals(this.getState());
    }
}
