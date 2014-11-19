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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "network-rule")
public class NetworkRule
{
    @XmlElement(name = "name", required = true, nillable = false)
    private String name_;

    @XmlElement(name = "from-port", required = true, nillable = false)
    private Integer fromPort_;

    @XmlElement(name = "to-port")
    private Integer toPort_;

    @XmlElement(name = "protocol", required = true, nillable = false)
    private Protocol protocol_;

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
    public NetworkRule setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the fromPort
     */
    public Integer getFromPort()
    {
        return fromPort_;
    }

    /**
     * @param fromPort
     *            the fromPort to set
     */
    public NetworkRule setFromPort(Integer fromPort)
    {
        this.fromPort_ = fromPort;
        return this;
    }

    /**
     * @return the toPort
     */
    public Integer getToPort()
    {
        return toPort_;
    }

    /**
     * @param toPort
     *            the toPort to set
     */
    public NetworkRule setToPort(Integer toPort)
    {
        this.toPort_ = toPort;
        return this;
    }

    /**
     * @return the protocol
     */
    public Protocol getProtocol()
    {
        return protocol_;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public NetworkRule setProtocol(Protocol protocol)
    {
        this.protocol_ = protocol;
        return this;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("name", this.getName())
                .add("from-port", this.getFromPort())
                .add("to-port", this.getToPort())
                .add("protocol", this.getProtocol())
                .omitNullValues()
                .toString();
    }
}
