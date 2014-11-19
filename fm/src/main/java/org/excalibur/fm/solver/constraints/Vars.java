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
package org.excalibur.fm.solver.constraints;

public enum Vars 
{
    /**
     * 
     */
    CPU(0, "gflops", "The sustainable performance in Gigaflops(GFlops)"),
    
    /**
     * 
     */
    CORES(1, "cores", "The number of CPU cores"),
    
    /**
     * 
     */
    MEMORY(2, "memory", "The Amount of RAM in GB"),
    
    /**
     * 
     */
    DISK(3, "disk", "The amount of disk"),        
    
    /**
     * 
     */
    NET_THROUGHPUT(4, "netth", "Network throughput"),
    
    /**
     * 
     */
    COST(5, "cost", "Financial cost");
    
    private final Integer id_;    
    private final String varName_;
    private final String description_;
    
    private Vars(Integer id, String varName, String description)
    {
        this.id_ = id;
        this.varName_ = varName;
        this.description_ = description;
    }

    public Integer getId() 
    {
        return id_;
    }

    public String getName() 
    {
        return varName_;
    }

    public String getDescription() 
    {
        return description_;
    }  
    
    public static Vars valueOfFrom(String name)
    {
        for (Vars var: values())
        {
            if (var.getName().equalsIgnoreCase(name))
            {
                return var;
            }
        }
        throw new IllegalArgumentException("Invalid var name: " + name);
    }
}
