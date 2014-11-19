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

public class Provider implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -7795161315990401086L;

    private Integer id_;
    private String name_;
    private String description_;

    public Provider withId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    public Provider withName(String name)
    {
        this.name_ = name;
        return this;
    }

    public Provider withDescription(String description)
    {
        this.description_ = description;
        return this;
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
    public void setId(Integer id)
    {
        this.id_ = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name_ = name;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description)
    {
        this.description_ = description;
    }
}
