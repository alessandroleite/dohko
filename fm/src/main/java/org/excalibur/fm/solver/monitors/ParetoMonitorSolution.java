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
package org.excalibur.fm.solver.monitors;

import static solver.ResolutionPolicy.MAXIMIZE;
import static solver.ResolutionPolicy.MINIMIZE;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.excalibur.fm.solver.constraints.Objective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.ICF;
import solver.constraints.LCF;
import solver.search.loop.monitors.IMonitorSolution;
import solver.search.solution.Solution;

import static com.google.common.base.Preconditions.*;

public class ParetoMonitorSolution implements IMonitorSolution
{
    private static final Logger LOG = LoggerFactory.getLogger(ParetoMonitorSolution.class.getName());
    
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6095236901417217490L;
    
    private final Solver solver_;
    private final Objective[] objectives_;
    private final LinkedList<Solution> solutions = new LinkedList<>();

    public ParetoMonitorSolution(Objective[] objectives)
    {
        this.solver_ = checkNotNull(objectives[0]).getObjectiveVar().getSolver();
        this.objectives_ = objectives;
        this.solver_.plugMonitor(this);
    }

    @Override
    public void onSolution()
    {
        for (Iterator<Solution> iter = solutions.iterator(); iter.hasNext();)
        {
            Solution sol = iter.next();

            if (isDominated(sol))
            {
                iter.remove();
            }
        }
        
        Solution solution = new Solution();
        solution.record(solver_);
        solutions.add(solution);

        Constraint[] better = new Constraint[objectives_.length];

        for (int i = 0; i < better.length; i++)
        {
            better[i] = ICF.arithm(objectives_[i].getObjectiveVar(), objectives_[i].getOperator().getSymbol(), 
                    objectives_[i].getObjectiveVar().getValue());
        }
        
        objectives_[0].getObjectiveVar().getSolver().post(LCF.or(better));
    }
    
    protected boolean isDominated(Solution solution)
    {
        boolean [] result = new boolean[objectives_.length];
        
        for (int i = 0; i < objectives_.length; i++)
        {
            int sol = solution.getIntVal(objectives_[i].getObjectiveVar());
            int obj = objectives_[i].getObjectiveVar().getValue();
            int delta = sol - obj;
            
            result[i] = (delta > 0 && objectives_[i].getResolutionPolicy() == MAXIMIZE) || 
            		    (delta < 0 && objectives_[i].getResolutionPolicy() == MINIMIZE);
            
            LOG.info("obj=%s, sol=%s, obj=%s, delta=%s, policy=%s, dominated=%s\n", 
                    objectives_[i].getObjectiveVar().getName(), sol, obj, 
                    delta, objectives_[i].getResolutionPolicy(), result[i]);
            
            if (result[i])
            {
                return false;
            }
        }
        
        return true;
    }

    public List<Solution> solutions()
    {
        return this.solutions;
    }
    
    public Solution last()
    {
        return this.solutions.isEmpty() ? null : this.solutions.getLast();
    }
    
    public Solver getSolver()
    {
        return this.solver_;
    }
}
