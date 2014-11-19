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
package org.excalibur.core.cloud.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Maps.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "instance-disks")
public final class Volumes implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -4914559803225061398L;

    private VirtualMachine instance_;
    
    private final Map<String, InstanceVolume> volumes_ = newConcurrentMap();

    protected Volumes()
    {
        super();
    }
    
    public Volumes(VirtualMachine instance)
    {
        this.instance_ = checkNotNull(instance);
    }

    public Volumes add(InstanceVolume volume)
    {
        if (volume != null && isNullOrEmpty(volume.getDevice()) && !volumes_.containsKey(volume.getDevice()))
        {
            checkState(Objects.equal(this.instance_.getLocation(), volume.getVolume().getZone()));
            this.volumes_.put(volume.getDevice(), volume.setInstance(this.instance_));
        }
        
        return this;
    }
    
    public Volumes remove(InstanceVolume disk)
    {
        this.volumes_.remove(disk.getDevice());
        return this;
    }

    /**
     * @return the volumes
     */
    @XmlElement(name = "volume")
//    @XmlElementWrapper(name = "volumes", nillable = false)
    public List<InstanceVolume> getDisks()
    {
        List<InstanceVolume> disks = Lists.newArrayList(volumes_.values());
        return Collections.unmodifiableList(disks);
    }

    /**
     * @return the instance
     */
    @XmlTransient
    public VirtualMachine getInstance()
    {
        return instance_;
    }
}
