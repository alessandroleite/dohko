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
package org.excalibur.core.domain;

import java.io.Serializable;
import java.util.Date;

import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.VirtualMachine;

public class InstanceState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = -887570617187975197L;
    
    private Integer id_;
    private VirtualMachine instance_;
    private InstanceStateType state_;
    private Date date_;

    public InstanceState()
    {
        super();
    }

    public InstanceState(Integer id)
    {
        this.id_ = id;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public InstanceState setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the instance
     */
    public VirtualMachine getInstance()
    {
        return instance_;
    }

    /**
     * @param instance
     *            the instance to set
     */
    public InstanceState setInstance(VirtualMachine instance)
    {
        this.instance_ = instance;
        return this;
    }

    /**
     * @return the state
     */
    public InstanceStateType getState()
    {
        return state_;
    }

    /**
     * @param state
     *            the state to set
     */
    public InstanceState setState(InstanceStateType state)
    {
        this.state_ = state;
        return this;
    }

    /**
     * @return the date
     */
    public Date getDate()
    {
        return date_;
    }

    /**
     * @param date
     *            the date to set
     */
    public InstanceState setDate(Date date)
    {
        this.date_ = date;
        return this;
    }
}
