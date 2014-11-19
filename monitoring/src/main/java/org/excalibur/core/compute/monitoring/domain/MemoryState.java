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
package org.excalibur.core.compute.monitoring.domain;


import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.excalibur.core.compute.monitoring.adapters.MemoryStateAdapter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "memory-state")
@XmlType(name = "memory-state", factoryMethod = "valueOf")
@XmlJavaTypeAdapter(MemoryStateAdapter.class)
public class MemoryState implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 3848358407688996118L;

    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Workaround to allow this class to be unmarshall using JAXB.
     * @return An instance of this class with null values.
     */
    @SuppressWarnings("unused")
    private static MemoryState valueOf()
    {
        return new MemoryState(0l, 0l, 0l, MemoryType.RAM, new Date());
    }

    public Builder toBuilder()
    {
        return new Builder().free(this.getFree()).size(this.getSize()).type(this.getType()).sampleTime(getSampleTime()).used(this.getUsed());
    }

    public static class Builder
    {
        private long size;
        private long used;
        private long free;
        private MemoryType type;
        private Date sampleTime;

        public Builder size(long size)
        {
            checkArgument(size >= 0);

            this.size = size;
            return this;
        }

        public Builder used(long used)
        {
            checkArgument(used >= 0);

            this.used = used;
            return this;
        }

        public Builder free(long free)
        {
            checkArgument(free >= 0);

            this.free = free;
            return this;
        }

        public Builder swap()
        {
            this.type = MemoryType.SWAP;
            return this;
        }

        public Builder ram()
        {
            this.type = MemoryType.RAM;
            return this;
        }

        public Builder type(MemoryType type)
        {
            this.type = type;
            return this;
        }

        public MemoryState build()
        {
            if (sampleTime == null)
            {
                sampleTime = new Date();
            }

            return new MemoryState(size, used, free, type, sampleTime);
        }

        public Builder sampleTime(Date date)
        {
            this.sampleTime = date;
            return this;
        }
    }

    @XmlElement(name = "size")
    private final long size_;

    @XmlElement(name = "used")
    private final long used_;

    @XmlElement(name = "free")
    private final long free_;

    @XmlElement(name = "type")
    private final MemoryType type_;

    @XmlElement(name = "sampleTime")
    private final Date sampleTime_;

    MemoryState(long size, long used, long free, MemoryType type, Date sampleTime)
    {
        this.size_ = size;
        this.used_ = used;
        this.free_ = free;
        this.type_ = checkNotNull(type);
        this.sampleTime_ = checkNotNull(sampleTime);
    }

    /**
     * @return the size
     */
    public long getSize()
    {
        return size_;
    }

    /**
     * @return the used
     */
    public long getUsed()
    {
        return used_;
    }

    /**
     * @return the free
     */
    public long getFree()
    {
        return free_;
    }

    /**
     * @return the type
     */
    public MemoryType getType()
    {
        return type_;
    }

    /**
     * @return the updateTime
     */
    public Date getSampleTime()
    {
        return sampleTime_;
    }
    
    @Override
    public String toString()
    {
        return com.google.common.base.Objects.toStringHelper(this).omitNullValues().toString();
    }
}
