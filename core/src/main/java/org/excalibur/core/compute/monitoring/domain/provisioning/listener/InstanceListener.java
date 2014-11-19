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
package org.excalibur.core.compute.monitoring.domain.provisioning.listener;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.service.provisioning.InstanceEvent;

public interface InstanceListener extends ProvisioningListener
{
    /**
     * Event called after a VM had successfully started.
     * 
     * @param event
     *            reference for the started {@link VirtualMachine}. Must not be <code>null</code>.
     */
    void onStarted(InstanceEvent event);

    /**
     * Event called after a VM had stopped.
     * 
     * @param event
     *            The reference for the stopped VM. Must not be <code>null</code>.
     */
    void onStopped(InstanceEvent event);

    /**
     * Event called after a VM had been terminated.
     * 
     * @param event
     *            The reference for the terminated VM. After terminated, the system does not allow any operation with the VM.
     */
    void onTerminated(InstanceEvent event);
}
