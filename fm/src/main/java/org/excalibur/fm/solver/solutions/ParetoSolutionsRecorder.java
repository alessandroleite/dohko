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
package org.excalibur.fm.solver.solutions;

import java.util.List;

import org.excalibur.fm.solver.constraints.Objective;
import org.excalibur.fm.solver.monitors.ParetoMonitorSolution;

import solver.exception.ContradictionException;
import solver.search.loop.monitors.IMonitorClose;
import solver.search.solution.ISolutionRecorder;
import solver.search.solution.Solution;

@SuppressWarnings("serial")
public class ParetoSolutionsRecorder implements ISolutionRecorder
{
    private final ParetoMonitorSolution paretoMonitorSolution_;

    public ParetoSolutionsRecorder(final Objective[] objectives)
    {
        paretoMonitorSolution_ = new ParetoMonitorSolution(objectives);
        paretoMonitorSolution_.getSolver().plugMonitor(new IMonitorClose()
        {
            @Override
            public void beforeClose()
            {
                Solution last = getLastSolution();

                if (last != null)
                {
                    try
                    {
                        paretoMonitorSolution_.getSolver().getSearchLoop().restoreRootNode();
                        paretoMonitorSolution_.getSolver().getEnvironment().worldPush();

                        last.restore();
                    }
                    catch (ContradictionException e)
                    {
                        throw new IllegalStateException("Restoring the last solution ended in a failure!");
                    }
                    
                    paretoMonitorSolution_.getSolver().getEngine().flush();
                }
            }

            @Override
            public void afterClose()
            {
            }
        });
    }
   
    @Override
    public Solution getLastSolution()
    {
        return this.paretoMonitorSolution_.last();
    }
    
    @Override
    public List<Solution> getSolutions()
    {
        return this.paretoMonitorSolution_.solutions();
    }
}
