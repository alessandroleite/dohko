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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "credential")
@XmlRootElement(name = "credential")
public class Credential implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 2491649410595054080L;

    @XmlAttribute(name = "name", required = true)
    protected String name_;

    @XmlElement(name = "identity")
    protected String identity_;

    @XmlElement(name = "credential")
    protected String credential_;

    public Credential()
    {
        super();
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
    public Credential setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the identity
     */
    public String getIdentity()
    {
        return identity_;
    }

    /**
     * @param identity
     *            the identity to set
     */
    public Credential setIdentity(String identity)
    {
        this.identity_ = identity;
        return this;
    }

    /**
     * @return the credential
     */
    public String getCredential()
    {
        return credential_;
    }

    /**
     * @param credential
     *            the credential to set
     */
    public Credential setCredential(String credential)
    {
        this.credential_ = credential;
        return this;
    }
}
