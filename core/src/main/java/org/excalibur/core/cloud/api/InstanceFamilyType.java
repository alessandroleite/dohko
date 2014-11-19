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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import static org.excalibur.core.cloud.api.VmFamilyTypeSize.*;

@XmlType(name = "family-type")
@XmlEnum(String.class)
public enum InstanceFamilyType
{
    /**
     * General instances provide a balance of compute, memory, and network resources.
     */
    @XmlEnumValue("GENERAL")
    GENERAL(1, "General purpose")
    {
        @Override
        public VmFamilyTypeSize[] sizeTypes()
        {
            return new VmFamilyTypeSize[] { SMALL, MEDIUM, LARGE };
        }
    },

    /**
     * Compute-optimized instances are optimized for applications that benefit from high compute power. They have a higher ratio of vCPUs to memory
     * than other families.
     */
    @XmlEnumValue("COMPUTE")
    COMPUTE(2, "Compute-optimized")
    {
        @Override
        public VmFamilyTypeSize[] sizeTypes()
        {
            return new VmFamilyTypeSize[] { SMALL, MEDIUM, LARGE, XLARGE };
        }
    },

    /**
     * Provides GPU computing power.
     */
    @XmlEnumValue("GPU")
    GPU(3, "GPU")
    {
        @Override
        public VmFamilyTypeSize[] sizeTypes()
        {
            return new VmFamilyTypeSize[] { SMALL, MEDIUM, LARGE, XLARGE };
        }
    },

    /**
     * Memory-optimized instances are optimized for memory-intensive applications.
     */
    @XmlEnumValue("MEMORY")
    MEMORY(4, "Memory-optimized")
    {
        @Override
        public VmFamilyTypeSize[] sizeTypes()
        {
            return new VmFamilyTypeSize[] { SMALL, MEDIUM, LARGE, XLARGE };
        }
    },

    /**
     * Storage-optimized instances are optimized for I/O-intensive applications.
     */
    @XmlEnumValue("STORAGE")
    STORAGE(5, "Storage-optimized")
    {
        @Override
        public VmFamilyTypeSize[] sizeTypes()
        {
            return new VmFamilyTypeSize[] { SMALL, MEDIUM, LARGE, XLARGE };
        }
    },

    /**
     * Bootstrap instances for tasks that do not demand powerful computational resources but have to remain online for long time.
     */
    @XmlEnumValue("SHARED")
    SHARED(6, "Shared")
    {
        @Override
        public VmFamilyTypeSize[] sizeTypes()
        {
            return new VmFamilyTypeSize[] { MILLI, MICRO };
        }
    };

    /**
     * 
     * @return
     */
    public abstract VmFamilyTypeSize[] sizeTypes();

    /**
     * The family id.
     */
    private final int id_;

    /**
     * The family description.
     */
    private final String description_;

    private InstanceFamilyType(int id, String description)
    {
        this.id_ = id;
        this.description_ = description;
    }

    /**
     * @return the id
     */
    public int getId()
    {
        return id_;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * Returns a {@link InstanceFamilyType} of the given {@code id}.
     * 
     * @param id
     *            The family type id.
     * @return The enum value or <code>null</code> if the {@code id} is invalid.
     */
    public static InstanceFamilyType valueOf(int id)
    {
        InstanceFamilyType type = null;
        int i = 0;

        while (i < values().length)
        {
            if (values()[i].getId() == id)
            {
                type = values()[i];
            }

            i++;
        }

        return type;
    }

}
