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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "provider")
@XmlType(name = "provider", propOrder = { "name_", "imageId_", "instanceType_" })
public class Provider
{
    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "image", required = true)
    private String imageId_;

    @XmlElement(name = "instance-type", required = true)
    private String instanceType_;
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name the name to set
     */
    public Provider setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the imageId
     */
    public String getImageId()
    {
        return imageId_;
    }

    /**
     * @param imageId the imageId to set
     */
    public Provider setImageId(String imageId)
    {
        this.imageId_ = imageId;
        return this;
    }

    /**
     * @return the instanceType
     */
    public String getInstanceType()
    {
        return instanceType_;
    }

    /**
     * @param instanceType the instanceType to set
     */
    public Provider setInstanceType(String instanceType)
    {
        this.instanceType_ = instanceType;
        return this;
    }

}
