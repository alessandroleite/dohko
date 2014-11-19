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
package org.excalibur.fm.solver.constraints.problem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypes;
import org.excalibur.fm.solver.constraints.Constraint;
import org.excalibur.fm.solver.constraints.DomainVar;
import org.excalibur.fm.solver.constraints.Objective;
import org.excalibur.fm.solver.constraints.Vars;
import org.excalibur.fm.solver.solutions.ParetoSolutionsRecorder;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.ICF;
import solver.search.loop.monitors.IMonitorSolution;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.VF;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

public class InstanceSelection
{
    public static final BigDecimal COST_FACTOR = new BigDecimal("1000");
    public static final BigDecimal SUSTAINABLE_PERFORMANCE_FACTOR = new BigDecimal("1000000");
    public static final String[] VAR_NAMES = { "cpu", "cores", "memory", "cost" };
    private final Solver solver_;

    private final InstanceTypes types_;
    private final Map<String, DomainVar> variables_ = newHashMap();

    private IntVar index_;

    public InstanceSelection(InstanceTypes types)
    {
        this.types_ = checkNotNull(types);
        solver_ = new Solver();
        createDomainVariables();
    }

    private void createDomainVariables()
    {
        for (int i = 0; i < types_.size(); i++)
        {
            InstanceType type = types_.get(i);

            //getVariable(Vars.CPU.getName()).setDomainValueAt(i, type.getConfiguration().getNumberOfComputeUnits());
            getVariable(Vars.CPU.getName()).setDomainValueAt(i, type.getConfiguration().getSustainablePerformanceGflops().multiply(SUSTAINABLE_PERFORMANCE_FACTOR).intValue());
            getVariable(Vars.CORES.getName()).setDomainValueAt(i, type.getConfiguration().getNumberOfCores());
            getVariable(Vars.MEMORY.getName()).setDomainValueAt(i, (int) (type.getConfiguration().getRamMemorySizeGb() * 1024));

            // getVariable(VariableType.DISK.getVarName()).setDomainValueAt(i, (int) (type.getConfiguration().getDiskSizeGb() * ONE_GB));
            // getVariable(VAR_NAMES[j++]).setDomainValueAt(i, type.getConfiguration().getNumberOfDisks());
            // getVariable(VariableType.NET_THROUGHPUT.getVarName()).setDomainValueAt(i, type.getConfiguration().getNetworkThroughput().intValue());
            // getVariable(VariableType.NET_THROUGHPUT.getVarName()).setDomainValueAt(i, type.getConfiguration().getNetworkLatency().intValue());
            getVariable(Vars.COST.getName()).setDomainValueAt(i, type.getCost().multiply(COST_FACTOR).intValue());

        }

        index_ = VF.bounded("i", 0, types_.size(), solver_);

        for (DomainVar var : variables_.values())
        {
            solver_.post(ICF.element(var.getVar(), var.getDomainValues(), index_));
        }
    }

    @SuppressWarnings("serial")
    public InstanceTypes findAllSolutions(Constraint... constraints)
    {
        InstanceTypes instanceTypes = new InstanceTypes();
        addConstraints(constraints);
        
        //https://github.com/chocoteam/choco3/issues/121
        final List<Solution> sols = newArrayList();
        
        solver_.getSearchLoop().plugSearchMonitor(new IMonitorSolution()
        {            
            @Override
            public void onSolution()
            {
               Solution solution = new Solution();
               solution.record(solver_);
               sols.add(solution);
            }
        });

        long ns = solver_.findAllSolutions();

        if (ns > 0)
        {
            instanceTypes = getSolutions(sols);
        }

        return instanceTypes;
    }
    
    public InstanceTypes findAllOptimalSolutions(ResolutionPolicy policy, Constraint[] constraints, Vars variable)
    {
        addConstraints(constraints);
        IntVar objective = variables_.get(variable.getName()).getVar();
        
        /**
         * <code>true</code> means: CHOCO will call two resolution: (a) It finds and prove the optimum (b) It reset search and enumerates all
         * solutions of optimal cost. On the other hand, when <code>false</code>, it performs only one resolution but which does impose to find
         * strictly better solutions. This means it will spend time enumerating intermediary solutions equal to the the best cost found so far (but
         * not necessarily optimal).
         */
        solver_.findAllOptimalSolutions(policy, objective, true);
        
        return getSolutions();
    }
    
    public InstanceTypes findAllOptimalSolutions(Objective objective, Constraint ... constraints)
    {
        return this.findAllOptimalSolutions(objective.getResolutionPolicy(), constraints, objective.getObjective());
    }

    public InstanceTypes findParetoFront(ResolutionPolicy policy, Constraint[] constraints, String... objectives)
    {
        checkState(objectives != null && objectives.length > 1);
        addConstraints(constraints);
        
        IntVar[] objs = getObjectiveVariables(objectives);
        checkState(objs.length == objectives.length);

        solver_.findParetoFront(policy, objs);              

        return getSolutions();
    }
    
    public InstanceTypes findParetoFront(ResolutionPolicy policy, Constraint[] constraints, Vars ... objectives)            
    {
        checkState(objectives != null && objectives.length > 1);
        
        String[] vars = new String[objectives.length];
        
        for (int i = 0; i < objectives.length; i++)
        {
            vars[i] = objectives[i].getName();
        }
        
        return findParetoFront(policy, constraints, vars);
    }
    
    public InstanceTypes findParetoFront(Constraint[] constraints, Objective ... objectives)
    {
        checkState(objectives != null && objectives.length > 1);
        addConstraints(constraints);
        
        for (int i = 0; i < objectives.length; i++)
        {
            objectives[i].setObjectiveVariable(getVariable(objectives[i].getObjective().getName()).getVar());
        }
        
        solver_.set(new ParetoSolutionsRecorder(objectives));
        
        solver_.findAllSolutions();
        
        return getSolutions(solver_.getSolutionRecorder().getSolutions());
    }

    private IntVar[] getObjectiveVariables(String... objectives)
    {
        IntVar[] objs = new IntVar[objectives.length];

        for (int i = 0; i < objectives.length; i++)
        {
            objs[i] = variables_.get(objectives[i]).getVar();
            checkState(objs[i] != null);
        }

        return objs;
    }

    /**
     * Returns all the available solutions or an empty {@link List} if there isn't a solution.
     * 
     * @return all the solutions. The returned {@link List} is never <code>null</code>.
     */
    private InstanceTypes getSolutions()
    {
        final List<InstanceType> types = newArrayList();

        List<Solution> solutions = solver_.getSolutionRecorder().getSolutions();

        for (int i = 0; i < solutions.size(); i++)
        {
            Solution s = solutions.get(i);

            InstanceType instanceType = this.types_.get(s.getIntVal(index_));
            types.add(instanceType);
        }
        
        return new InstanceTypes(types);
    }
    
    private InstanceTypes getSolutions(List<Solution> solutions)
    {
        final List<InstanceType> types = newArrayList();
        
        for (int i = 0; i < solutions.size(); i++)
        {
            Solution s = solutions.get(i);

            InstanceType instanceType = this.types_.get(s.getIntVal(index_));
            types.add(instanceType);
        }
        
        return new InstanceTypes(types);
    }

    private void addConstraints(Constraint[] constraints)
    {
        for (Constraint c : constraints)
        {
            IntVar var = variables_.get(c.getVariable().getName()).getVar();
            checkState(var != null, String.format("Invalid variable: [%s]!", c.getVariable().getName()));

            solver_.post(ICF.arithm(var, c.getOperator().getSymbol(), c.getValue()));
        }
    }

    /**
     * Returns a {@link DomainVar} with the given name. If the variable had already declared its reference is returned, otherwise it is declared.
     * 
     * @param name The name of the variable. Might not be <code>null</code> of empty.
     * @return A {@link DomainVar} with the given name. It's never <code>null</code>.
     */
    protected DomainVar getVariable(String name)
    {
        DomainVar var = this.variables_.get(name);

        if (var == null)
        {
            var = new DomainVar(name, types_.size(), solver_);
            this.variables_.put(name, var);
        }

        return checkNotNull(var);
    }
}
