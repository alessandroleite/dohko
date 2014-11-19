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

import java.util.Date;
import java.util.List;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.util.EventListener;
import org.excalibur.core.util.EventObject;

public class InstancesEvent extends EventObject<List<VirtualMachine>, InstanceStateType>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -750640511811172079L;

    private final Date updateTime_;

    public InstancesEvent(Object source, List<VirtualMachine> value, InstanceStateType type, Date updateTime)
    {
        super(source, value, type);
        this.updateTime_ = updateTime;
    }

    @Override
    public void processListener(EventListener listener)
    {
        ((InstancesListener) listener).stateChanged(this);
    }

    @Override
    public boolean isAppropriateListener(EventListener listener)
    {
        return (listener instanceof InstancesListener);
    }

    /**
     * @return the updateTime
     */
    public Date getUpdateTime()
    {
        return updateTime_;
    }

}
