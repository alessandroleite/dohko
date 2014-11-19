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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.fm.Configuration;
import org.excalibur.fm.Property;
import org.excalibur.fm.Resource;

import com.google.common.collect.Maps;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.ICF;
import solver.search.solution.Solution;
import solver.variables.IntVar;
import solver.variables.VF;

public class MOPChocoSolver
{
    /**
     * Matrix of the clouds' instance types. on(j,i) = 1 <=> instance type j is hosted on cloud i.
     */
    IntVar[][] vars;

    /**
     * Reverse matrix of clouds to instance types. on(i,j) = 1 <=> Cloud i hosts instance type i.
     */
    IntVar[][] varsInv;
    
    private BigDecimal COST_FACTOR = new BigDecimal("1000");
    private final int ONE_GB = (int) Math.pow(1024, 3);
    
    Solver solver = new Solver();

    private Resource[] allResources;

    @SuppressWarnings("rawtypes")
    public MOPChocoSolver(InstanceType [] instanceTypes)
    {
        final int mCost = 30;
        final int stabilityRatio = 85;

        solver.Solver solver = new solver.Solver();
        int [][] nodes = { { 10, 20, 90 }, { 80, 35, 65}, { 20, 25, 85}, {45,37,75} };
//        int [] cost = new int[nodes.length];
//        int [] stability = new int[nodes.length];
        
//        IntVar performance = VF.enumerated("performance", new int[] { 10, 80, 20, 45 }, solver);
//        IntVar cost = VF.enumerated("cost", new int[] { 20, 35, 25, 37 }, solver);
//        IntVar stability = VF.enumerated("stability", new int[] { 90, 65, 85, 75 }, solver);
        
//        IntVar[] performance = new IntVar[nodes.length];
//        IntVar[] cost = new IntVar[performance.length];
//        IntVar[] stability = new IntVar[performance.length];
        
//        for (int i = 0, j = 0; i < nodes.length; i++, j = 0)
//        {
//            performance[i] = VF.fixed("p_" + i, nodes[i][j++], solver);            
////            cost[i] = VF.fixed("c_" + i, nodes[i][j++], solver);
////            stability[i] = VF.fixed("s_" + i, nodes[i][j++], solver);
//            cost[i] = nodes[i][j++];
//            stability[i] = nodes[i][j++];
//        }
        
        //objective function
//        IntVar cc = VF.bounded("cost", 0, mCost, solver);
//        IntVar scalar = VF.bounded("stability", stabilityRatio, 100, solver);
//
//        //solver.post(IntConstraintFactory.knapsack(objects, scalar, power, volumes, energies));
//        solver.post(knapsack(performance, scalar, cc, cost, stability));
//        
//        solver.findOptimalSolution(ResolutionPolicy.MINIMIZE, cc);
//        
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < performance.length; i++) {
//            sb.append(String.format("\t#%d: %d\n", i, performance[i].getValue()));
//        }
        
//        sb.append(String.format("\n\tcc: %d", cc.getValue()));
//        
//        System.out.println(sb);
        
//        System.out.println(solver.isSatisfied());
        
//        AbstractStrategy strat = IntStrategyFactory.lexico_LB(performance);
        // trick : top-down maximization
//        solver.set(new ObjectiveStrategy(cc, OptimizationPolicy.TOP_DOWN, true), strat);

//        if (solver.findSolution())
//        {
//            do
//            {
//                for (int i = 0; i < performance.length; i++)
//                {
//                    System.out.println(performance[i] + ", " + cost[i] + ", " + stability[i]);   
//                }                               
//            } while (solver.nextSolution());
//        }
        
//        model(solver, nodes, mCost, stabilityRatio);
//        
//        solver = new Solver();
//        model2(solver, nodes,mCost, stabilityRatio);
        model();
        
    }
    
    public static void model(solver.Solver solver, int nodes [][], int maximumCost, int minimumStabilityRatio)
    {
        int [] cost = new int[nodes.length];
        int [] stability = new int[nodes.length];
        int [] per = new int[nodes.length];
        
        for (int i = 0, j = 0; i < nodes.length; i++, j = 0)
        {
            per[i] = nodes[i][j++];            
            cost[i] = nodes[i][j++];            
            stability[i] = nodes[i][j++];
        }
        
        IntVar p = VF.enumerated("p", per, solver);
        IntVar c = VF.enumerated("c", cost, solver);
        IntVar s = VF.bounded("st", minimumStabilityRatio, 100, solver);
//        IntVar s = VF.enumerated("s", stability, solver);
        
        solver.post(ICF.arithm(c, "<=", maximumCost));
        solver.post(ICF.arithm(s, ">=", minimumStabilityRatio));
        
        solver.findAllSolutions(); //80,25,100
//        solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, p); //80,20,85
        
        for(Solution sol: solver.getSolutionRecorder().getSolutions())
        {
            System.out.println(sol.getIntVal(p) + "," + sol.getIntVal(c) + "," + sol.getIntVal(s));
        }
    }
    
    public void model()
    {

        // each node represents a tuple with <performance, cost, stability>
        int [][] nodes = { {10, 20, 90 }, { 80, 35, 65}, { 20, 25, 85}, {45,37,75} };

        int maximumCost = 30, minimumStabilityRatio = 85;

        int [] cost = new int[nodes.length];
        int [] stability = new int[nodes.length];
        int [] per = new int[nodes.length];
        
        for (int i = 0, j = 0; i < nodes.length; i++, j = 0)
        {
            per[i] = nodes[i][j++];            
            cost[i] = nodes[i][j++];            
            stability[i] = nodes[i][j++];
        }
        
        IntVar p = VF.enumerated("p", per, solver);
        IntVar c = VF.enumerated("c", cost, solver);
        IntVar s = VF.bounded("st", minimumStabilityRatio, 100, solver);
        
        IntVar index = VF.bounded("i", 0, cost.length, solver);
        
        solver.post(ICF.arithm(c, "<=", maximumCost));
        solver.post(ICF.arithm(s, ">=", minimumStabilityRatio));
        
        solver.post(ICF.element(c, cost, index));
        solver.post(ICF.element(s, stability, index));
        solver.post(ICF.element(p, per, index));
        
//       solver.findAllSolutions();
        solver.findParetoFront(ResolutionPolicy.MAXIMIZE, p, s);
//        solver.findOptimalSolution(ResolutionPolicy.MAXIMIZE, p);
        List<Solution> solutions = solver.getSolutionRecorder().getSolutions();
        
        for(int i = 0; i < solutions.size(); i++) 
        {
            Solution sol = solutions.get(i);
            System.out.print("Configuration[" + sol.getIntVal(index));
            System.out.println("]:" + ArrayUtils.toString(nodes[sol.getIntVal(index)]));
        }
    }
    
    
    
    private void makeResources(List<InstanceType> types, int ncpu, int nmem, int mcost)
    {
        int[] cpus = new int[types.size()];
        int[] nCpus = new int[types.size()];
        int[] memories = new int[types.size()];
        int[] disks = new int[types.size()];
        int[] nDisks = new int[types.size()];
        int[] costs = new int[types.size()];
        int[] networkCapacities = new int[types.size()];
        int[] networkLatency = new int[types.size()];
        
        for (int i = 0; i < types.size(); i++)
        {
            InstanceType type = types.get(i);
            
            cpus[i] = type.getConfiguration().getNumberOfComputeUnits();
            memories[i] = (int) (type.getConfiguration().getRamMemorySizeGb() * ONE_GB);
            nCpus[i] = type.getConfiguration().getNumberOfCores();
            disks[i] = (int) (type.getConfiguration().getDiskSizeGb() * ONE_GB);
            nDisks[i] = type.getConfiguration().getNumberOfDisks();            
            networkCapacities[i] = type.getConfiguration().getNetworkThroughput().intValue();
            networkLatency[i] = type.getConfiguration().getNetworkLatency().intValue();
            
            costs[i] = type.getCost().multiply(COST_FACTOR).intValue();
        }
        
        IntVar cpuCapacity = VF.enumerated("cpu", cpus, solver);
        IntVar numberOfCores  = VF.enumerated("cores", nCpus, solver);
        IntVar memorySize  = VF.enumerated("memory", memories, solver);
        IntVar network = VF.enumerated("network", networkCapacities, solver);
        IntVar latency = VF.enumerated("latency", networkLatency, solver);
        IntVar cost = VF.enumerated("cost", costs, solver);
        
        IntVar index = VF.bounded("i", 0, cpus.length, solver);
        
        solver.post(ICF.arithm(numberOfCores, ">=", ncpu));
        solver.post(ICF.arithm(memorySize, ">=", nmem));
        solver.post(ICF.arithm(cost, "<=", cost));
        
        solver.post(ICF.element(cpuCapacity, cpus, index));
        solver.post(ICF.element(numberOfCores, nCpus, index));
        solver.post(ICF.element(memorySize, memories, index));
        solver.post(ICF.element(network, networkCapacities, index));
        solver.post(ICF.element(latency, networkLatency, index));
        
        long ns = solver.findAllSolutions();
        
        List<Solution> solutions = solver.getSolutionRecorder().getSolutions();
        
        for(int i = 0; i < solutions.size(); i++) 
        {
            Solution sol = solutions.get(i);
            System.out.print("Configuration [" + sol.getIntVal(index));
            System.out.println("]:" + types.get(sol.getIntVal(index)));
        }
    }

    public static void main(String[] args)
    {
        new MOPChocoSolver(new InstanceType[0]);
        //multi();
    }
    
    public static void model2(solver.Solver solver, int nodes [][], int maximumCost, int minimumStabilityRatio)
    {
        int [] cost = new int[nodes.length];
        int [] stability = new int[nodes.length];
        int [] per = new int[nodes.length];
        
        for (int i = 0, j = 0; i < nodes.length; i++, j = 0)
        {
            per[i] = nodes[i][j++];
            cost[i] = nodes[i][j++];
            stability[i] = nodes[i][j++];
        }
        
        IntVar p = VF.enumerated("p", per, solver);
        IntVar c = VF.bounded("c", 0, maximumCost, solver);
        IntVar sc = VF.enumerated("s", stability, solver);
        
        solver.post(ICF.arithm(c, "<=", maximumCost));
        solver.post(ICF.arithm(sc, ">=", minimumStabilityRatio));
        
        solver.findAllSolutions();
        List<Solution> solutions = solver.getSolutionRecorder().getSolutions();
        
        for(int i = 0; i < solutions.size(); i++) 
        {
            Solution sol = solutions.get(i);
            
            System.out.println(sol.getIntVal(p) + "," + sol.getIntVal(c) + "," + sol.getIntVal(sc));
        }
    }
    
    private Map<String, List<IntVar[]>> variables = Maps.newHashMap();
    
    
    
    private void makeResources(Configuration source)
    {
        for (int i = 0; i < source.getResources().size(); i++)
        {
            Resource r = source.getResources().get(i);
            this.allResources [i] = r;
            
            for (int j = 0; j < r.getProperties().size(); j++)
            {
                Property p = r.getProperties().get(j);
                
                if (!variables.containsKey(p.getName()))
                {
                    variables.put(p.getName(), new ArrayList<IntVar[]>());
                    variables.get(p.getName()).add(new IntVar[i]);
                }
                
                variables.get(p.getName()).get(0)[i] = VF.bounded(r.getName() + "." + p.getName(), 0, p.getValue(), this.solver);
            }
        }
    }
}
