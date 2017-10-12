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

import com.google.common.base.MoreObjects;

import solver.ResolutionPolicy;
import solver.variables.IntVar;
import static com.google.common.base.Preconditions.*;
import static solver.ResolutionPolicy.*;

public class Objective implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -1107717125358067352L;

    /**
     * The variable to optimize.
     */
    private final Vars var_;

    /**
     * The optimization objective.
     */
    private final ResolutionPolicy resolutionPolicy;

    /***
     * The solver's variable.
     */
    private IntVar objective_;

    public Objective(Vars var, ResolutionPolicy goal)
    {
        this.var_ = checkNotNull(var);
        this.resolutionPolicy = checkNotNull(goal);
    }

    public Objective(Vars objective)
    {
        this(objective, MAXIMIZE);
    }
    
    public static Objective valueOf(Constraint constraint)
    {
        checkNotNull(constraint);
        
        Objective obj;
        
        switch(constraint.getOperator())
        {
            case GE:                
            case GT:
                obj = maximize(Vars.valueOfFrom(constraint.getVariable().getName()));
                break;
            case LE:
            case LT:
                obj = minimize(Vars.valueOfFrom(constraint.getVariable().getName()));
                break;
            default:
                throw new IllegalStateException("Invalid operator: " + constraint.getOperator().getSymbol());
        }
        
        return obj;
    }

    /**
     * Creates and returns a minimize {@link Objective} for the given variable.
     * 
     * @param var
     *            The objective to minimize. Might not be <code>null</code>.
     * @return A minimize objective.
     */
    public static Objective minimize(Vars var)
    {
        return new Objective(var, MINIMIZE);
    }

    /**
     * Creates and returns a maximize {@link Objective} for the given variable.
     * 
     * @param var
     *            The objective to maximize. Might not be <code>null</code>.
     * @return A minimize objective.
     */
    public static Objective maximize(Vars var)
    {
        return new Objective(var, MAXIMIZE);
    }

    /**
     * @return the var
     */
    public Vars getObjective()
    {
        return var_;
    }

    /**
     * @return the resolutionPolicy
     */
    public ResolutionPolicy getResolutionPolicy()
    {
        return resolutionPolicy;
    }

    /**
     * @return the objective
     */
    public IntVar getObjectiveVar()
    {
        return objective_;
    }

    /**
     * @param objective
     *            the objective to set
     */
    public Objective setObjectiveVariable(IntVar objective)
    {
        this.objective_ = objective;
        return this;
    }

    /**
     * Returns the {@link Operator} based on the resolution policy. In other words, this method returns {@link Operator#LT} if this is a minimization
     * objective otherwise, it returns {@link Operator#GT}.
     * 
     * @return A operator based on the resolution policy.
     */
    public Operator getOperator()
    {
        return MINIMIZE.equals(this.resolutionPolicy) ? Operator.LT : Operator.GT;
    }
    
    @Override
    public String toString() 
    {
    	return MoreObjects.toStringHelper(this).add("objective", getObjective())
    			      .add("objective-var", getObjectiveVar())
    			      .add("operator", getOperator())
    			      .add("policy", getResolutionPolicy())
    			      .omitNullValues()
    			      .toString();
    }
}
