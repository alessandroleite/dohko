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

public enum VmFeatures
{
    CLUSTER(1, "Cluster"),
    
    FREE_TIER (2, "Free Tier"),
    
    MONITORING (3, "Monitoring");
    
    private final Integer id_;
    private final String value_;
    
    private VmFeatures(Integer id, String value)
    {
        this.id_ = id;
        this.value_ = value;
    }

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id_;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value_;
    }
    
    /**
     * Returns a {@link VmFeatures} of the given {@code id}.
     * 
     * @param id feature id.
     */
    public static VmFeatures valueOf(int id)
    {
        for (VmFeatures type: values())
        {
            if (type.getId().equals(id))
            {
                return type;
            }
        }
        
        throw new IllegalArgumentException(String.format("Invalid [%s]-[%s]", VmFeatures.class.getName(), id));
    }
    
    public static VmFeatures valueOfFromValue(String value)
    {
        for (VmFeatures type: values())
        {
            if (type.getValue().equals(value))
            {
                return type;
            }
        }
        
        throw new IllegalArgumentException(String.format("Invalid [%s]-[%s]", VmFeatures.class.getName(), value));
    }
}
