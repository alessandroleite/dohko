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
import solver.constraints.Arithmetic;
import solver.constraints.ICF;
import solver.constraints.Operator;
import solver.variables.IntVar;
import solver.variables.VF;

public class FirstStep
{
    public static void main(String[] args)
    {
        Solver solver = new Solver();
        
        IntVar x = VF.bounded("x", 0, Integer.MAX_VALUE, solver);
        IntVar y = VF.bounded("y", 0, Integer.MAX_VALUE, solver);
        IntVar z = VF.fixed("z", 7, solver);
        
        solver.post(ICF.arithm(x, "+", y, "=", 3));
        
//        Arithmetic aa = ICF.arithm(VF.fixed(2, solver), "*", x);
        
//        solver.post(ICF.arithm());
        
     // 2x + 4y == 20
//        x.multiply(2).add(y.multiply(4)).equals(20);
        
        solver.post(ICF.arithm(x, "+", y));
    }
}
