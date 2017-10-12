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
package org.excalibur.bio.sequencing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sequence-pair")
@XmlType(name = "sequence-pair", propOrder = { "query_", "target_" })
public class SequencePair implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -9064495068393900205L;

    @XmlElement(name = "query", nillable = false, required = true)
    private Sequence query_;
    
    @XmlElement(name = "target", nillable = false, required = true)
    private Sequence target_;

    /**
     * @return the query
     */
    public Sequence getQuery()
    {
        return query_;
    }

    /**
     * @param query
     *            the query to set
     */
    public SequencePair setQuery(Sequence query)
    {
        this.query_ = query;
        return this;
    }

    /**
     * @return the target
     */
    public Sequence getTarget()
    {
        return target_;
    }

    /**
     * @param target
     *            the target to set
     */
    public SequencePair setTarget(Sequence target)
    {
        this.target_ = target;
        return this;
    }
    
    @Override
    public int hashCode()
    {
       return Objects.hashCode(this.getQuery(), this.getTarget());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        
        if (!(obj instanceof SequencePair))
        {
            return false;
        }
        SequencePair other = (SequencePair) obj;
        
        return Objects.equal(this.getQuery(), other.getQuery()) && Objects.equal(this.getTarget(), other.getTarget());
    }
    
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("query", getQuery())
        		.add("target", getTarget())
        		.omitNullValues()
        		.toString();
    }
    
    @Override
    public SequencePair clone()
    {
        SequencePair clone;
        
        try
        {
            clone = (SequencePair) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new SequencePair().setQuery(this.getQuery() != null ? this.getQuery().clone() : null)
                                      .setTarget(this.getTarget() != null ? this.getTarget().clone() : null);
        }
        return clone;
    }
}
