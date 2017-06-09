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
import org.excalibur.fm.solver.constraints.Constraint;
import org.excalibur.fm.solver.constraints.DomainVar;
import org.excalibur.fm.solver.constraints.Vars;

import solver.Solver;
import solver.constraints.ICF;
import solver.constraints.LCF;
import solver.variables.BoolVar;
import solver.variables.IntVar;
import solver.variables.VF;

import static com.google.common.base.Preconditions.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.unmodifiableList;

import static org.excalibur.fm.solver.constraints.Vars.CORES;
import static org.excalibur.fm.solver.constraints.Vars.COST;
import static org.excalibur.fm.solver.constraints.Vars.CPU;
import static org.excalibur.fm.solver.constraints.Vars.MEMORY;


public class InstanceSelection2
{
    public static final BigDecimal COST_FACTOR = new BigDecimal("1000");
    private final Solver solver_;

    private final List<InstanceType> types_;
    private final Map<String, DomainVar> variables_ = newHashMap();

    private IntVar index_;

    public InstanceSelection2(List<InstanceType> types)
    {
        this.types_ = unmodifiableList(checkNotNull(types));
        solver_ = new Solver();
        createDomainVariables();
    }

    private void createDomainVariables()
    {
        for (int i = 0; i < types_.size(); i++)
        {
            InstanceType type = types_.get(i);

            getVariable(CPU).setDomainValueAt(i, type.getConfiguration().getNumberOfComputeUnits());
            getVariable(CORES).setDomainValueAt(i, type.getConfiguration().getNumberOfCores());
            getVariable(MEMORY).setDomainValueAt(i, (int) (type.getConfiguration().getRamMemorySizeGb() * 1024));
            getVariable(COST).setDomainValueAt(i, type.getCost().multiply(COST_FACTOR).intValue());

            // START extra variables for guided improvement algorithm
            getVariable("lb" + CPU.getName()).setDomainValueAt(i, type.getConfiguration().getNumberOfComputeUnits());
            getVariable("lb" + CORES.getName()).setDomainValueAt(i, type.getConfiguration().getNumberOfCores());
            getVariable("lb" + MEMORY.getName()).setDomainValueAt(i, (int) (type.getConfiguration().getRamMemorySizeGb() * 1024));
            getVariable("lb" + COST.getName()).setDomainValueAt(i, type.getCost().multiply(COST_FACTOR).intValue());

            // END extra variables for guided improvement algorithm
        }

        index_ = VF.bounded("i", 0, types_.size(), solver_);

        for (DomainVar var : variables_.values())
        {
            solver_.post(ICF.element(var.getVar(), var.getDomainValues(), index_));
        }
    }

    public List<InstanceType> findAllOptimalSolutions(Constraint[] constraints)
    {
        final List<InstanceType> solutions = newArrayList();
        
//        final List<Solution> sols = newLinkedList();
        
        addConstraints(constraints);
        
        List<solver.constraints.Constraint> stack = newArrayList();
        final solver.constraints.Constraint strictlyBetter = createGuidedImprovementConstraints(stack);
        
        final IntVar lbCPU = var("lb" + CORES.getName());
        final IntVar lbRAM = var("lb" + MEMORY.getName());
        final IntVar lbCost = var("lb" + COST.getName());
        
        final IntVar cpu = var(CORES);
        final IntVar ram = var(MEMORY);
        final IntVar cost = var(COST);
        
        while (solver_.findSolution())
        {
            int bestCPU, bestRAM, bestCost;
            
            do 
            {
                bestCPU = var(CORES).getValue();
                bestRAM = var(MEMORY).getValue();
                bestCost = var(COST).getValue();
                
                System.out.printf("bestCPU=%s, bestRAM=%s, bestCost=%s\n", bestCPU, bestRAM, bestCost);
                
                popAll(stack);
                
                push(ICF.arithm(lbCPU, "=", bestCPU), stack);
                push(ICF.arithm(lbRAM, "=", bestRAM), stack);
                push(ICF.arithm(lbCost, "<=", bestCost), stack);
                
                push(strictlyBetter, stack);
                
            }while(solver_.nextSolution());
            
            popAll(stack);
            
            push(ICF.arithm(cpu, "=", bestCPU), stack);
            push(ICF.arithm(ram, "=", bestRAM), stack);
            push(ICF.arithm(cost, "=", bestCost), stack);
            
            push(ICF.arithm(lbCPU, "=", bestCPU), stack);
            push(ICF.arithm(lbRAM, "=", bestRAM), stack);
            push(ICF.arithm(lbCost, "<=", bestCost), stack);
            
            solver_.getEngine().flush();
            solver_.getSearchLoop().reset();
            
//            solver_.set(new AllSolutionsRecorder(solver_)
//            {
//                @Override
//                protected IMonitorSolution createRecMonitor()
//                {
//                    return new IMonitorSolution()
//                    {
//                        @Override
//                        public void onSolution()
//                        {
//                            int vals[] = { cpu.getValue(), ram.getValue(), cost.getValue() };
//                            
//                            for (int i = sols.size() - 1; i >= 0; i--)
//                            {
//                                if (dominatedSolution(sols.get(i), vals))
//                                {
//                                    sols.remove(i);
//                                }
//                            }
//                            
//                            Solution solution = new Solution();
//                            solution.record(solver_);
//                            sols.add(solution);
//                        }
//                    };
//                }
//                
//                protected boolean dominatedSolution(Solution solution, int[] vals)
//                {
//                    boolean dominated = (vals[0] - solution.getIntVal(var(CORES)) > 0)  || 
//                                        (vals[1] - solution.getIntVal(var(MEMORY)) > 0) || 
//                                        (vals[2] - solution.getIntVal(var(COST)) < 0);
//                    return dominated;
//                }
//            });
            
            if (solver_.findSolution())
            {
                do
                {
                    solutions.add(this.types_.get(this.index_.getValue()));
                }while(solver_.nextSolution());
            }
            
            popAll(stack);
            
            solver_.getEngine().flush();
            solver_.getSearchLoop().reset();
            
            solver_.post
            (
                    LCF.and
                    (
                            LCF.or
                            (
                                    ICF.arithm(cpu, ">", bestCPU), 
                                    ICF.arithm(ram, ">", bestRAM)
                            ),
                            ICF.arithm(cost, "<", bestCost)
                    )
            );
        }
        System.out.println(solutions.size());
        //return getSolutions(sols);
        return solutions;
    }
    
    private void popAll(List<solver.constraints.Constraint> stack)
    {
//        checkState(solver_.getSearchLoop().getCurrentDepth() == 0);
        for (solver.constraints.Constraint c: stack)
        {
            solver_.unpost(c);
        }
        
        stack.clear();
    }
    
    
    private void push(solver.constraints.Constraint c, List<solver.constraints.Constraint> stack)
    {
        stack.add(c);
        solver_.post(c);
    }

    private solver.constraints.Constraint createGuidedImprovementConstraints(List<solver.constraints.Constraint> stack)
    {
        BoolVar cpuSBetter = ICF.arithm(var(CORES), ">", var("lb" + CORES.getName())).reif();
        BoolVar ramSBetter = ICF.arithm(var(MEMORY), ">", var("lb" + MEMORY.getName())).reif();
        
        BoolVar costSBetter = ICF.arithm(var(COST), "<", var("lb" + COST.getName())).reif();
        
        BoolVar cpuBetter = ICF.arithm(var(CORES), ">=", var("lb" + CORES.getName())).reif();
        BoolVar ramBetter = ICF.arithm(var(MEMORY), ">=", var("lb" + MEMORY.getName())).reif();
        BoolVar costBetter = ICF.arithm(var(COST), "<", var("lb" + COST.getName())).reif();
        
        push(ICF.arithm(var("lb" + CORES.getName()), "=", var(CORES)), stack);
        push(ICF.arithm(var("lb" + MEMORY.getName()), "=", var(MEMORY)), stack);
        push(ICF.arithm(var("lb" + COST.getName()), "<=", var(COST)), stack);
        
        // cost?
        return LCF.or
                (
                        LCF.and(cpuSBetter, ramBetter),
                        LCF.and(cpuBetter, ramSBetter),
                        LCF.and(cpuSBetter, costBetter),
                        LCF.and(cpuBetter, costSBetter),
                        LCF.and(ramSBetter, costBetter),
                        LCF.and(ramBetter, costSBetter)                        
                );        
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
    
    protected IntVar var(Vars name)
    {
        return var(name.getName());
    }
    
    protected IntVar var(String name)
    {
        return getVariable(name).getVar();
    }

    protected DomainVar getVariable(Vars var)
    {
        return this.getVariable(var.getName());
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
    
//    private List<InstanceType> getSolutions(List<Solution> solutions)
//    {
//        final List<InstanceType> types = newArrayList();
//        
//        for (int i = 0; i < solutions.size(); i++)
//        {
//            Solution s = solutions.get(i);
//
//            InstanceType instanceType = this.types_.get(s.getIntVal(index_));
//            types.add(instanceType);
//        }
//        
//        return types;
//    }
}
