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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

import java.io.Serializable;

import com.google.common.base.Objects;

public class Variable implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 5965285668641459741L;

    /**
     * Var's name. Might not be <code>null</code> or empty.
     */
    private final String name_;
    
    /**
     * Var's value.
     */
    private Integer value_;

    public Variable(String name)
    {
        this.name_ = name;
        checkState(!isNullOrEmpty(name));
    }
    
    public Variable(Vars var, Integer value)
    {
        this(var.getName(), value);
    }

    public Variable(String name, Integer value)
    {
        this(name);
        this.value_ = value;
    }

    public static Variable valueOf(String name)
    {
        return new Variable(name);
    }

    public static Variable valueOf(String name, Integer value)
    {
        return new Variable(name, value);
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @return the value
     */
    public Integer getValue()
    {
        return value_;
    }

    /**
     * @param value
     *            the value to set
     */
    public Variable setValue(Integer value)
    {
        this.value_ = value;
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Variable))
        {
            return false;
        }

        Variable other = (Variable) obj;

        return Objects.equal(this.getName(), other.getName());
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(this.getName());
    }

    @Override
    public String toString()
    {
        return toStringHelper(this).add("name", getName()).add("value", getValue()).omitNullValues().toString();
    }

    @Override
    protected Variable clone() 
    {
        Object clone;
        
        try 
        {
             clone = super.clone();
        } catch (CloneNotSupportedException ex) 
        {
            clone = new Variable(this.getName(), this.getValue());
        }
        
        return (Variable) clone;
    }        
}
