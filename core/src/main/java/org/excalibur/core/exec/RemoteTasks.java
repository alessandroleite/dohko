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
package org.excalibur.core.exec;

import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "remote-tasks")
public class RemoteTasks implements Serializable, Iterable<RemoteTask>
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = -8179417782594668262L;
    
    
    @XmlElement(name = "tasks")
    @XmlElementWrapper(name = "task")
    private final List<RemoteTask> tasks_ = newCopyOnWriteArrayList();

    public RemoteTasks()
    {
        super();
    }

    public List<RemoteTask> getTasks()
    {
        return unmodifiableList(tasks_);
    }

    public RemoteTasks add(RemoteTask task)
    {
        if (task != null)
        {
            this.tasks_.add(task);
        }

        return this;
    }

    @Override
    public Iterator<RemoteTask> iterator()
    {
        return getTasks().iterator();
    }
    
    public int size()
    {
        return tasks_.size();
    }
    
    public RemoteTask first()
    {
        return this.tasks_.isEmpty() ? null : this.tasks_.get(0);
    }
}
