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
package org.excalibur.core.execution.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.excalibur.core.cloud.api.Platform;
import org.excalibur.core.util.Strings2;
import org.excalibur.core.util.YesNoEnum;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "script-statement")
public class ScriptStatement implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 90839162392725786L;
    
    private final transient Object lock_ = new Object();

    @XmlAttribute(name = "id")
    private Integer id_;

    @XmlAttribute(name = "name", required = true)
    private String name_;

    @XmlElement(name = "platform")
    private Platform platform_ = Platform.LINUX;

    @XmlElement(name = "statement", required = true, nillable = false)
    private String statement_;

    @XmlElement(name = "active")
    private YesNoEnum active_;

    @XmlElement(name = "parents")
    private String parents_;
    
    @XmlElement(name="created-in")
    private Date createdIn_;

    @XmlTransient
    private final List<ScriptStatement> parentsList_ = Lists.newArrayList();

    public ScriptStatement()
    {
        super();
        this.createdIn_ = new Date();
    }

    public ScriptStatement(Integer id)
    {
        this.id_ = id;
    }

    /**
     * Creates a new {@link ScriptStatement} based on the state of other {@link ScriptStatement}. In other words, this is a clone constructor.
     * <p>
     * <strong>Notice:</strong> The id of the given object is not copied.
     * 
     * @param that The object to be cloned. Might not be <code>null</code>.
     * @throws NullPointerException if the given object to clone was <code>null</code>.
     */
    public ScriptStatement(ScriptStatement that)
    {
         setActive(checkNotNull(that).getActive())
        .setCreatedIn(that.getCreatedIn())
        .setName(that.getName())
        .setParents(that.getParents())
        .setPlatform(that.getPlatform())
        .setStatement(that.getStatement());
    }

    public static ScriptStatement valueOf(Integer id)
    {
        return new ScriptStatement(id);
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @param id
     *            the id to set
     */
    public ScriptStatement setId(Integer id)
    {
        this.id_ = id;
        return this;
    }

    /**
     * @return the statement
     */
    public String getStatement()
    {
        return statement_;
    }

    /**
     * @param statement
     *            the statement to set
     */
    public ScriptStatement setStatement(String statement)
    {
        this.statement_ = statement;
        return this;
    }

    /**
     * @return the active
     */
    public YesNoEnum getActive()
    {
        return active_;
    }

    /**
     * @param active
     *            the active to set
     */
    public ScriptStatement setActive(YesNoEnum active)
    {
        this.active_ = active;
        return this;
    }

    /**
     * @return the parents
     */
    public String getParents()
    {
        return parents_;
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
    public ScriptStatement setName(String name)
    {
        this.name_ = name;
        return this;
    }

    /**
     * @return the platform
     */
    public Platform getPlatform()
    {
        return platform_;
    }

    /**
     * @param platform
     *            the platform to set
     */
    public ScriptStatement setPlatform(Platform platform)
    {
        this.platform_ = platform;
        return this;
    }
    
    

    /**
     * @return the createdIn
     */
    public Date getCreatedIn()
    {
        return createdIn_;
    }

    /**
     * @param createdIn the createdIn to set
     */
    public ScriptStatement setCreatedIn(Date createdIn)
    {
        this.createdIn_ = createdIn;
        return this;
    }

    /**
     * @param parents
     *            the parents to set
     */
    public final ScriptStatement setParents(String parents)
    {
        synchronized (lock_)
        {
            this.parents_ = parents;

            this.parentsList_.clear();

            if (!Strings.isNullOrEmpty(parents))
            {
                final String[] parentIds = parents.split(Strings2.COMMA);

                for (String parent : parentIds)
                {
                    if (!Strings.isNullOrEmpty(parent))
                    {
                        Integer id = Integer.parseInt(parent);

                        if (!Objects.equal(this.id_, id))
                        {
                            parentsList_.add(valueOf(id));
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * @return the parentsList
     */
    public final List<ScriptStatement> getParentsList()
    {
        synchronized (lock_)
        {
            return Collections.unmodifiableList(parentsList_);
        }
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getId(), this.getName());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof ScriptStatement))
        {
            return false;
        }

        ScriptStatement other = (ScriptStatement) obj;

        return Objects.equal(this.getId(), other.getId()) || Objects.equal(this.getName(), this.getName());
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("name", this.getName())
                .add("platform", this.getPlatform())
                .add("is-active", this.getActive())
                .add("parents", this.getParents())
                .add("created-in", this.getCreatedIn())
                .omitNullValues().toString();
    }
    
    @Override
    public ScriptStatement clone()
    {
        Object clone;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new ScriptStatement(this);
        }
        
        return (ScriptStatement) clone;
    }
}
