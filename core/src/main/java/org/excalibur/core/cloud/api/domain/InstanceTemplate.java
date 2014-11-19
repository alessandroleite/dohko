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
package org.excalibur.core.cloud.api.domain;

import java.io.Serializable;
import java.util.Date;

import org.excalibur.core.LoginCredentials;
import org.excalibur.core.cloud.api.Placement;
import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.domain.User;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class InstanceTemplate implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6579577698877243368L;

    /**
     * The instance template ID.
     */
    private Integer id;

    /**
     * The request ID.
     */
    private String requestId_;

    /**
     * The image id.
     */
    private String imageId_;

    /**
     * The instance's name. Might not be <code>null</code>.
     */
    private String instanceName_;

    /**
     * The instance type to use.
     */
    private InstanceType instanceType_;

    /**
     * The name of the key pair.
     */
    private String keyName_;

    /**
     * Minimum number of instances to launch. If the value is greater than the allowed by the provider, no instances are launched at all.
     */
    private Integer minCount_;

    /**
     * Maximum number of instances to launch. If the value is greater than the allowed by the provider, the largest possible number above
     * {@code minCount} will be launched instead.
     * <p>
     * Between 1 and the maximum number allowed by the cloud provider.
     */
    private Integer maxCount_;

    /**
     * The region to create the instance. When not specified the default region is used, and it depends of the provider.
     */
    private Region region_;

    /**
     * The cluster for this instance.
     */
    private Placement group;

    /**
     * The request status.
     */
    private InstanceTemplateStatus status_;

    /**
     * The cloud on where the request will be executed.
     */
    private Provider provider_;

    /**
     * The {@link User} who this request belongs to.
     */
    private User owner_;

    /**
     * The create time.
     */
    private Date createTime_;

    /**
     * The user credentials.
     */
    private LoginCredentials loginCredentials_;

    /**
     * The Base64-encoded MIME user data for the instances.
     */
    private String userData_;
    
    /**
     * The size of the main disk in GB.
     */
    private Integer diskSize_;

    /**
     * The instance tags.
     */
    private final Tags tags_ = new Tags();

    public InstanceTemplate()
    {
        super();
    }

    protected InstanceTemplate(InstanceTemplate that)
    {
        this.imageId_ = that.imageId_;
        this.instanceType_ = that.instanceType_;
        this.minCount_ = that.minCount_;
        this.maxCount_ = that.maxCount_;
        this.keyName_ = that.keyName_;
    }

    public InstanceTemplate setMaxCount(Integer value)
    {
        this.maxCount_ = value;
        return this;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public InstanceTemplate setId(Integer id)
    {
        this.id = id;
        return this;
    }

    /**
     * @return the requestId
     */
    public String getRequestId()
    {
        return requestId_;
    }

    /**
     * @param id
     *            the id to set
     */
    public InstanceTemplate setRequestId(String id)
    {
        this.requestId_ = id;
        return this;
    }

    /**
     * @return the imageId
     */
    public String getImageId()
    {
        return imageId_;
    }

    public InstanceTemplate setImageId(String imageId)
    {
        this.imageId_ = imageId;
        return this;
    }

    /**
     * @return the instanceName
     */
    public String getInstanceName()
    {
        return instanceName_;
    }

    public InstanceTemplate setInstanceName(String name)
    {
        name.matches("[a-z]([-a-z0-9]*[a-z0-9])?");
        this.instanceName_ = name;
        return this;
    }

    /**
     * @return the instanceType
     */
    public InstanceType getInstanceType()
    {
        return instanceType_;
    }

    public InstanceTemplate setInstanceType(InstanceType type)
    {
        this.instanceType_ = type;
        return this;
    }

    public InstanceTemplate setInstanceType(String instanceType)
    {
        this.instanceType_ = InstanceType.valueOf(instanceType);
        return this;
    }

    /**
     * @return the keyName
     */
    public String getKeyName()
    {
        return keyName_;
    }

    public InstanceTemplate setKeyName(String keyName)
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keyName));
        this.keyName_ = keyName;

        return this;
    }

    /**
     * @return the minCount
     */
    public Integer getMinCount()
    {
        return minCount_;
    }

    public InstanceTemplate setMinCount(Integer value)
    {
        this.minCount_ = value;
        return this;
    }

    /**
     * @return the maxCount
     */
    public Integer getMaxCount()
    {
        return maxCount_;
    }

    /**
     * @return the status_
     */
    public InstanceTemplateStatus getStatus()
    {
        return status_;
    }

    public InstanceTemplate setStatus(InstanceTemplateStatus status)
    {
        this.status_ = status;
        return this;
    }

    /**
     * @return the region_
     */
    public final Region getRegion()
    {
        return region_ == null ? (region_ = new Region()) : region_;
    }

    /**
     * @param region
     *            the region_ to set
     */
    public InstanceTemplate setRegion(Region region)
    {
        this.region_ = region;
        return this;
    }

    /**
     * @return the provider
     */
    public Provider getProvider()
    {
        return provider_;
    }

    /**
     * @param provider
     *            the provider to set
     */
    public InstanceTemplate setProvider(Provider provider)
    {
        this.provider_ = provider;
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
     * @param owner
     *            the owner to set
     */
    public InstanceTemplate setOwner(User owner)
    {
        this.owner_ = owner;
        return this;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime()
    {
        return createTime_;
    }

    public InstanceTemplate setCreateTime(Date date)
    {
        this.createTime_ = date;
        return this;
    }

    /**
     * @return the group
     */
    public Placement getGroup()
    {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public InstanceTemplate setGroup(Placement group)
    {
        this.group = group;
        return this;
    }

    /**
     * Convenient method to set the group name for the instance.
     * 
     * @param groupName
     *            The name of the group.
     * @return The same instance.
     * @see #setGroup(Placement)
     */
    public InstanceTemplate setGroup(String groupName)
    {
        return this.setGroup(new Placement().setGroupName(groupName));
    }

    /**
     * Convenient method to set the group name for the instance.
     * 
     * @param groupName
     *            The name of the group.
     * @return The same instance.
     * @see #setGroup(Placement)
     */
    public InstanceTemplate setGroupName(String groupName)
    {
        return this.setGroup(groupName);
    }

    /**
     * @return the tags
     */
    public Tags getTags()
    {
        return tags_;
    }

    public InstanceTemplate setTags(Tag... tags)
    {
        this.tags_.add(tags);
        return this;
    }

    public InstanceTemplate setTags(Tags tags)
    {
        tags_.copyFrom(tags);
        return this;
    }

    /**
     * @return the loginCredentials_
     */
    public LoginCredentials getLoginCredentials()
    {
        return loginCredentials_;
    }

    /**
     * @param loginCredentials_
     *            the loginCredentials_ to set
     */
    public InstanceTemplate setLoginCredentials(LoginCredentials loginCredentials)
    {
        this.loginCredentials_ = loginCredentials;
        return this;
    }

    /**
     * @return the userData
     */
    public String getUserData()
    {
        return userData_;
    }

    /**
     * @param userData
     *            the userData to set
     */
    public InstanceTemplate setUserData(String userData)
    {
        this.userData_ = userData;
        return this;
    }
    
    /**
     * @return the diskSize
     */
    public Integer getDiskSize()
    {
        return diskSize_;
    }

    /**
     * @param diskSize the diskSize to set
     */
    public InstanceTemplate setDiskSize(Integer diskSize)
    {
        this.diskSize_ = diskSize;
        return this;
    }

    @Override
    protected InstanceTemplate clone()
    {
        InstanceTemplate clone;
        try
        {
            clone = (InstanceTemplate) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new InstanceTemplate(this);
        }

        clone.requestId_ = null;
        clone.status_ = status_;

        return clone;
    }
}
