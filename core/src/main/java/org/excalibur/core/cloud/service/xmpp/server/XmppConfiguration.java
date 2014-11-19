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
package org.excalibur.core.cloud.service.xmpp.server;

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "xmpp-server-configuration")
public class XmppConfiguration implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 970245213932959560L;
    
    @XmlElement(name = "domain", required = true)
    private String domain_;
    
    @XmlElement(name = "tcp-port", required = true)
    private int    tcpPort_;
    
    @XmlElement(name = "s2s-port", required = true)
    private int    s2sPort_;
    
    @XmlElement(name = "certificate", required = true)
    private File   certificate_;
    
    @XmlElement(name = "password", required = true)
    private String password_;

    /**
     * @return the domain
     */
    public String getDomain()
    {
        return domain_;
    }

    /**
     * @param domain
     *            the domain to set
     */
    public XmppConfiguration setDomain(String domain)
    {
        this.domain_ = domain;
        return this;
    }

    /**
     * @return the tcpPort
     */
    public int getTcpPort()
    {
        return tcpPort_;
    }

    /**
     * @param tcpPort
     *            the tcpPort to set
     */
    public XmppConfiguration setTcpPort(int tcpPort)
    {
        this.tcpPort_ = tcpPort;
        return this;
    }

    /**
     * @return the s2sPort
     */
    public int getS2SPort()
    {
        return s2sPort_;
    }

    /**
     * @param s2sPort
     *            the s2sPort to set
     */
    public XmppConfiguration setS2SPort(int s2sPort)
    {
        this.s2sPort_ = s2sPort;
        return this;
    }

    /**
     * @return the certificate
     */
    public File getCertificate()
    {
        return certificate_;
    }

    /**
     * @param certificate
     *            the certificate to set
     */
    public XmppConfiguration setCertificate(File certificate)
    {
        this.certificate_ = certificate;
        return this;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password_;
    }

    /**
     * @param password
     *            the password to set
     */
    public XmppConfiguration setPassword(String password)
    {
        this.password_ = password;
        return this;
    }
}
