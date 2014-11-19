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

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;

import static com.google.common.base.Preconditions.*;

/**
 * Represents a VM's state transition. 
 */
public abstract class InstanceEvent extends ProvisioningEvent<VirtualMachine, InstanceStateType>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7054787445821791203L;
    
    /**
     * The time when the stated changed.
     */
    private final Date stateTime;

    public InstanceEvent(Object source, VirtualMachine vm, InstanceStateType state)
    {
        super(source, checkNotNull(vm), state);
        this.stateTime = new Date();
    }

    /**
     * @return the state
     */
    public InstanceStateType getState()
    {
        return getType();
    }

    /**
     * @return the stateTime
     */
    public Date getStateTime()
    {
        return stateTime;
    }
}
