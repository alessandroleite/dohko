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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sequence")
@XmlType(name="sequence", propOrder= {"name_", "description_", "value_"})
public final class Sequence implements Serializable, Cloneable, Comparable<Sequence>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 4151863133768153014L;

    /**
     * Sequence's name. Example: ERR135910.3.
     */
    @XmlAttribute(name = "name", required = true)
    private String name_;
    /**
     * Sequence's description. Example: 2405:1:1101:1234:1973:Y/1
     */
    @XmlElement(name = "description")
    private String description_;

    /**
     * Sequence value. For example: NAAGGGTTTGAGTAAGAGCATAGCTGTTGGGACCCGAAAGATGGTGAACT
     */
    @XmlElement(name = "value", required = true, nillable = false)
    private String value_;

    protected Sequence()
    {
        super();
    }

    /**
     * Creates a new {@link Sequence}.
     * 
     * @param name
     *            Sequence name. Might not be <code>null</code>.
     * @param description
     *            Sequence description.
     * @param value
     *            Sequence value.
     */
    public Sequence(String name, String description, String value)
    {
        this.name_ = checkNotNull(name);
        this.description_ = description;
        this.value_ = checkNotNull(value);
    }

    /**
     * Creates an instance of {@link Sequence} copying the state of another {@link Sequence}.
     * 
     * @param that
     *            Sequence to be cloned.
     */
    public Sequence(Sequence that)
    {
        this(checkNotNull(that).name_, that.description_, that.value_);
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value_;
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

        if (!(obj instanceof Sequence))
        {
            return false;
        }

        Sequence other = (Sequence) obj;
        return Objects.equal(this.getName(), other.getName());
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("description", getDescription())
                .add("content", getValue())
                .omitNullValues()
                .toString();
    }

    @Override
    public Sequence clone()
    {
        Object clone;

        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new Sequence(this);
        }
        return (Sequence) clone;
    }

    @Override
    public int compareTo(Sequence that)
    {
        return this.name_.compareTo(that.name_);
    }
}
