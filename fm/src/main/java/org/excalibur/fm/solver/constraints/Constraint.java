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

import java.io.Serializable;


public class Constraint implements Serializable, Cloneable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -4929141033384697236L;
    
    private final Variable variable_;
    private final Operator operator_;
    
    
    public Constraint(Variable variable, Operator operator)
    {
        this.variable_ = variable;
        this.operator_ = operator;
    }

    /**
     * @return the variable
     */
    public Variable getVariable()
    {
        return variable_;
    }


    /**
     * @return the operator
     */
    public Operator getOperator()
    {
        return operator_;
    }
    
    public int getValue()
    {
        return this.getVariable().getValue();
    }
    
    @Override
    public String toString()
    {
        return variable_.getName() + " " + operator_.name() + " " + variable_.getValue();
    }

    @Override
    protected Constraint clone()  
    {
        Object clone;
        
        try 
        {                        
            clone = super.clone();            
        } catch (CloneNotSupportedException ex) 
        {
            clone = new Constraint(getVariable(), getOperator());
        }
        return (Constraint) clone;
    }    
}
