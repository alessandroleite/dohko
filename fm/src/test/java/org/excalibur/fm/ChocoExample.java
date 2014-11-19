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
package org.excalibur.fm;

import solver.Solver;
import solver.constraints.ICF;
import solver.search.strategy.ISF;
import solver.variables.IntVar;
import solver.variables.VF;


@SuppressWarnings("rawtypes")
public class ChocoExample
{

    public static void main(String[] args)
    {
        int k = 3, n = 9;
        Solver solver = new Solver();

        IntVar[] p = VF.enumeratedArray("p", n * k, 0, k * n - 1, solver);

        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n * (k - 1); j += n)
            {
                solver.post(ICF.arithm(VF.offset(p[i + j], i + 2), "=", p[i + (j + n)]));
            }
        }

        solver.post(ICF.arithm(p[0], "<", p[n * k - 1]));
        solver.post(ICF.alldifferent(p, "AC"));
//        solver.set(ISF.firstFail_InDomainMax(p));

        if (solver.findSolution())
        {
            while (solver.nextSolution())
            {
                print(p);
            }
        }
    }

    private static void print(IntVar[] p)
    {
        for (int i = 0; i < p.length; i++)
        {
            System.out.print(p[i].getValue() + " ");
        }
        System.out.println();
    }
}
