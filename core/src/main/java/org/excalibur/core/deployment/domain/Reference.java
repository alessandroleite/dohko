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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ref")
@XmlType(name = "ref", propOrder = { "node_", "attribute_" })
public class Reference implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 1715812840577802555L;

    @XmlAttribute(name = "node", required = true)
    private String node_;

    @XmlAttribute(name = "attribute", required = true)
    private String attribute_;

    /**
     * @return the node
     */
    public String getNode()
    {
        return node_;
    }

    /**
     * @param node
     *            the node to set
     */
    public Reference setNode(String node)
    {
        this.node_ = node;
        return this;
    }

    /**
     * @return the attribute
     */
    public String getAttribute()
    {
        return attribute_;
    }

    /**
     * @param attribute
     *            the attribute to set
     */
    public Reference setAttribute(String attribute)
    {
        this.attribute_ = attribute;
        return this;
    }
}
