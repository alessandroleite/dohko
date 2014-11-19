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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.excalibur.core.cloud.api.KeyPairs;
import org.excalibur.core.cloud.api.domain.Zone;
import org.excalibur.core.domain.User;
import org.excalibur.core.execution.domain.ApplicationExecDescription;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "remote-task")
public class RemoteTask implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 7111703775246433737L;

    @XmlElement(name = "host", required = true)
    private HostAndPort hostAndPort_;

    @XmlElement(name = "key", required = true)
    private KeyPairs keyPairs_;
    
    @XmlElement(name = "username", required = true)
    private String username_;
    
    @XmlElement(name = "owner", required = true)
    private User owner_;
    
    @XmlElement(name = "application", required = true)
    private ApplicationExecDescription application_;
    
    @XmlElement(name = "zone", required = true)
    private Zone zone_;

    /**
     * @return the hostAndPort
     */
    public HostAndPort getHostAndPort()
    {
        return hostAndPort_;
    }

    /**
     * @param hostAndPort
     *            the hostAndPort to set
     */
    public RemoteTask setHostAndPort(HostAndPort hostAndPort)
    {
        this.hostAndPort_ = hostAndPort;
        return this;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username_;
    }

    /**
     * @param username
     *            the username to set
     */
    public RemoteTask setUsername(String username)
    {
        this.username_ = username;
        return this;
    }

    /**
     * @return the application
     */
    public ApplicationExecDescription getApplication()
    {
        return application_;
    }

    /**
     * @param application
     *            the application to set
     */
    public RemoteTask setApplication(ApplicationExecDescription application)
    {
        this.application_ = application;
        return this;
    }

    /**
     * @return the keyPair
     */
    public KeyPairs getKeyPairs()
    {
        return keyPairs_;
    }

    /**
     * @param keyPairs
     *            the keyPairs to set
     */
    public RemoteTask setKeyPairs(KeyPairs keyPairs)
    {
        this.keyPairs_ = keyPairs;
        return this;
    }

    /**
     * @return the owner
     */
    public User getOwner()
    {
        return owner_;
    }

    /**
     * @param owner the owner to set
     */
    public RemoteTask setOwner(User owner)
    {
        this.owner_ = owner;
        return this;
    }

    /**
     * @return the zone
     */
    public Zone getZone()
    {
        return zone_;
    }

    /**
     * @param zone the zone to set
     */
    public RemoteTask setZone(Zone zone)
    {
        this.zone_ = zone;
        return this;
    }
}
