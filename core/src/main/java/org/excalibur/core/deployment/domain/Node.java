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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.excalibur.core.Identifiable;
import org.excalibur.core.cloud.api.domain.Tag;
import org.excalibur.core.cloud.api.domain.Tags;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "node")
@XmlType(name = "node", propOrder = { "name_", "count_", "group_", "region_", "zone_", "provider_", "plugins_", "dependencies_", "credential_",
        "tags_", "userData_" })
public class Node implements Comparable<Node>, Identifiable<String>
{
    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlAttribute(name = "count", required=true)
    private Integer count_ = 1;

    @XmlAttribute(name = "group")
    private String group_;

    @XmlAttribute(name = "region", required = true)
    private String region_;

    @XmlAttribute(name = "zone")
    private String zone_;

    @XmlElement(name = "provider", required = true, nillable = false)
    private Provider provider_;

    @XmlElement(name = "plugin")
    private final List<Plugin> plugins_ = new ArrayList<Plugin>();

    @XmlElement(name = "depends")
    private final List<Dependency> dependencies_ = new ArrayList<Dependency>();

    @XmlElement(name = "tags")
    private final Tags tags_ = new Tags();

    @XmlElement(name = "credential")
    private Credential credential_;

    @XmlElement(name = "data")
    private String userData_;

    public Node addDependencies(Dependency... dependencies)
    {
        for (Dependency dependency : dependencies)
        {
            this.dependencies_.add(dependency);
        }

        return this;
    }

    public Node addPlugin(Plugin plugin)
    {
        return this.addPlugins(plugin);
    }

    public Node addPlugins(Plugin... plugins)
    {
        for (Plugin plugin : plugins)
        {
            this.plugins_.add(plugin);
        }
        return this;
    }

    public Node addTags(Tag... tags)
    {
        this.tags_.add(tags);

        return this;
    }

    /**
     * @return the credential
     */
    public Credential getCredential()
    {
        return credential_;
    }

    /**
     * Assigns the credential to access the node(s).
     * 
     * @param credential
     *            The credential to use.
     * @return The same reference.
     */
    public Node setCredential(Credential credential)
    {
        this.credential_ = credential;
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
    public Node setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the count
     */
    public int getCount()
    {
        return count_;
    }

    /**
     * @param count
     *            the count to set
     */
    public Node setCount(int count)
    {
        this.count_ = count;
        return this;
    }

    /**
     * @return the group
     */
    public String getGroup()
    {
        return group_;
    }

    /**
     * @param group
     *            the group to set
     */
    public Node setGroup(String group)
    {
        this.group_ = group;
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
    public Node setProvider(Provider provider)
    {
        this.provider_ = provider;
        return this;
    }

    /**
     * @return the plugins
     */
    public List<Plugin> getPlugins()
    {
        return Collections.unmodifiableList(plugins_);
    }

    /**
     * @return the depends
     */
    public List<Dependency> getDependencies()
    {
        return Collections.unmodifiableList(dependencies_);
    }

    /**
     * @return the region_
     */
    public String getRegion()
    {
        return region_;
    }

    /**
     * @param region_
     *            the region_ to set
     */
    public Node setRegion(String region)
    {
        this.region_ = region;
        return this;
    }

    /**
     * @return the tags
     */
    public Tags getTags()
    {
        return this.tags_;
    }

    /**
     * @return the zone_
     */
    public String getZone()
    {
        return zone_;
    }

    /**
     * @param zone_
     *            the zone_ to set
     */
    public Node setZone(String zone_)
    {
        this.zone_ = zone_;
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
    public Node setUserData(String userData)
    {
        this.userData_ = userData;
        return this;
    }

    public boolean hasDependencies()
    {
        synchronized (dependencies_)
        {
            return !this.dependencies_.isEmpty();
        }
    }

    @Override
    public int compareTo(Node other)
    {
        return this.getName().compareTo(other.getName());
    }

    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof Node))
        {
            return false;
        }
        
        Node other = (Node) obj;
        return Objects.equal(this.getName(), other.getName());
    }

    @Override
    public String getId()
    {
        return this.getName();
    }
}
