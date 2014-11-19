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
package org.excalibur.core.cloud.api.compute;

import java.io.Closeable;
import java.util.List;

import org.excalibur.core.cloud.api.KeyPair;
import org.excalibur.core.cloud.api.Service;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.VirtualMachineImage;
import org.excalibur.core.cloud.api.domain.InstanceTemplate;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;

public interface ComputeService extends Service, Closeable
{
    Instances createInstances(InstanceTemplate request);

    Instances createInstances(final InstanceTemplate request, final boolean waitForRunningState);

    Instances listInstances();

    /**
     * Returns all instances of all regions.
     * 
     * @return The instances of all regions.
     */
    Instances aggregateInstances();

    VirtualMachine getInstanceWithName(String name, String zone);

    /**
     * Returns all instances that have at least one of the given tags.
     * 
     * @param tags
     *            The tags to filter the instances.
     * @return A non-null reference with the instances that have at least one of the tags.
     */
    Instances listInstancesWithTags(Tags tags);

    /**
     * Returns all instances that have at least one of the given tags.
     * 
     * @param tags
     *            The tags to filter the instances.
     * @return A non-null reference with the instances that have at least one of the tags.
     */
    Instances listInstancesWithTags(Tag... tags);

    /**
     * Adds or overwrites a {@link Tag} for the specified instance. Tag keys must be unique per resource.
     * 
     * @param instanceId
     *            Instance's id. Might not be <code>null</code> or empty.
     * @param tag
     *            Tag to be added. Might not be <code>null</code>.
     */
    void setTag(String instanceId, Tag tag);

    /**
     * Adds or overwrites the {@link Tag} for the specified instance. Tag keys must be unique per resource.
     * 
     * @param instanceId
     *            Instance's id. Might not be <code>null</code> or empty.
     * @param tags
     *            {@link Tags} to be added. Might not be <code>null</code>.
     */
    void setTags(String instanceId, Tags tags);

    
    /**
     * Adds or overwrites the {@link Tag} for the specified instances. Tag keys must be unique per resource.
     * 
     * @param instanceId
     *            Instance's id. Might not be <code>null</code> or empty.
     * @param tags
     *            {@link Tags} to be added. Might not be <code>null</code>.
     */
    void setTags(Iterable<String> instanceIds, Tags tags);

    void stop(VirtualMachine instance);

    void stop(String... instanceIds);

    void stop(Instances instances);

    void terminateInstances(String... instanceIds);

    void terminateInstances(Instances instances);

    void terminateInstance(VirtualMachine instance);

    String importKeyPair(KeyPair keyPair);

    List<VirtualMachineImage> listImages(String... imageIds);

}
