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
package org.excalibur.core.deployment.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "param")
public class Param
{
    /**
     * Parameter's name.
     */
    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlTransient
    private Reference reference_;

    @XmlElementRef(name = "ref", type = Reference.class)
    @XmlMixed
    protected final List<Serializable> content = new ArrayList<Serializable>();

    public Param()
    {
        super();
    }

    public Param(String name)
    {
        this.name_ = name;
    }

    public Param withName(String name)
    {
        this.name_ = name;
        return this;
    }

    public Param withValue(Serializable value)
    {
        if (value != null)
        {
            this.content.add(value);

            if (value instanceof Reference)
            {
                this.reference_ = (Reference) value;
            }
        }
        return this;
    }

    public Param withReference(Reference reference)
    {
        if (reference != null)
        {
            this.content.add(reference);
            this.reference_ = reference;
        }

        return this;
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
     * @return the reference
     */
    public Reference getReference()
    {
        return reference_;
    }
}
