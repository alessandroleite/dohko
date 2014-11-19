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
package org.excalibur.core.cloud.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.domain.UserProviderCredentials;

import com.google.common.base.Objects;

@XmlRootElement(name = "instance-configuration")
@XmlType(name = "instance-configuration")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.PROPERTY)
public class VmConfiguration implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID<code> for serialization.
     */
    private static final long serialVersionUID = 2068062148770254653L;

    /**
     * The private IP address of a VM.
     */
    private String privateIpAddress_;

    /**
     * The public IP address of a VM.
     */
    private String publicIpAddress_;

    /**
     * The public DNS name of this VM.
     */
    private String publicDnsName_;

    /**
     * Platform of the instance (e.g., Windows).
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>Windows
     */
    private String platform_;

    /**
     * The OS's user name.
     */
    private String platformUserName_;

    /**
     * If this VM was launched with an associated key pair, this displays the key pair name.
     */
    private String keyName_;
    
    /**
     * The keypairs (public and private) to access the instance.
     */
    private KeyPairs keyPairs_;
    
    /**
     * The credentials of the user. It is not included in the XML.
     */
    private UserProviderCredentials credentials_;

    public VmConfiguration setPrivateIpAddress(String privateIpAddress)
    {
        this.privateIpAddress_ = privateIpAddress;
        return this;
    }

    public VmConfiguration setPublicDnsName(String publicDnsName)
    {
        this.publicDnsName_ = publicDnsName;
        return this;
    }

    public VmConfiguration setPublicIpAddress(String publicIpAddress)
    {
        this.publicIpAddress_ = publicIpAddress;
        return this;
    }

    public VmConfiguration setPlatform(String platform)
    {
        this.platform_ = platform;
        return this;
    }

    public VmConfiguration setPlatformUserName(String userName)
    {
        this.platformUserName_ = userName;
        return this;
    }

    public VmConfiguration setKeyName(String keyName)
    {
        this.keyName_ = keyName;
        return this;
    }

    /**
     * @return the keyPairs
     */
    @XmlElement(name="keypairs")
    public KeyPairs getKeyPairs()
    {
        return keyPairs_;
    }

    /**
     * @param keyPairs the keyPairs to set
     */
    public VmConfiguration setKeyPairs(KeyPairs keyPairs)
    {
        this.keyPairs_ = keyPairs;
        return this;
    }

    /**
     * @return the privateIpAddress
     */
    @XmlElement(name = "private-ip")
    public String getPrivateIpAddress()
    {
        return privateIpAddress_;
    }

    /**
     * @return the publicIpAddress
     */
    @XmlElement(name = "public-ip")
    public String getPublicIpAddress()
    {
        return publicIpAddress_;
    }

    /**
     * @return the platform
     */
    @XmlElement(name = "platform")
    public String getPlatform()
    {
        return platform_;
    }

    /**
     * @return the platformUserName
     */
    @XmlElement(name = "username")
    public String getPlatformUserName()
    {
        return platformUserName_;
    }

    /**
     * @return the keyName
     */
    @XmlElement(name = "keyname")
    public String getKeyName()
    {
        return keyName_;
    }

    /**
     * @return the publicDnsName
     */
    @XmlElement(name = "public-dns-name")
    public String getPublicDnsName()
    {
        return publicDnsName_;
    }
    
    
    /**
     * @return the credentials
     */
    @XmlTransient
    public UserProviderCredentials getCredentials()
    {
        return credentials_;
    }

    /**
     * @param credentials the credentials to set
     */
    public VmConfiguration setCredentials(UserProviderCredentials credentials)
    {
        this.credentials_ = credentials;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof VmConfiguration))
        {
            return false;
        }
        
        VmConfiguration other = (VmConfiguration)obj;
        
        return Objects.equal(this.getPrivateIpAddress(), other.getPrivateIpAddress()) && 
               Objects.equal(this.getPublicIpAddress(), other.getPublicIpAddress()) &&
               Objects.equal(this.getPublicDnsName(), other.getPublicDnsName());
    }
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getPrivateIpAddress(), this.getPublicIpAddress(), this.getPublicDnsName());
    }
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("ip", this.getPublicIpAddress())
                .add("dns", this.getPublicDnsName())
                .add("platform", this.getPlatform())
                .add("username", this.getPlatformUserName())
                .add("keyname", this.getKeyName())
                .omitNullValues()
                .toString();
    }
}
