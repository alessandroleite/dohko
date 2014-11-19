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
package org.excalibur.fm.clafer.model;

import java.io.File;
import java.io.IOException;

import org.clafer.ast.AstModel;
import org.clafer.collection.Triple;
import org.clafer.compiler.ClaferCompiler;
import org.clafer.compiler.ClaferUnsat;
import org.clafer.javascript.Javascript;
import org.clafer.objective.Objective;
import org.clafer.scope.Scope;

import org.clafer.ast.AstConcreteClafer;
import static org.clafer.ast.Asts.*;
import org.clafer.compiler.ClaferOptimizer;


public class ClaferFm
{
    public static void main(String[] args) throws IOException
    {
        Triple<AstModel, Scope, Objective[]> model = Javascript.readModel(new File("/home/alessandro/excalibur/source/fm/src/main/resources/fm2.js"));
        ClaferUnsat unsat = ClaferCompiler.compileUnsat(model.getFst(), Scope.defaultScope(1));
        System.out.println(unsat);

    }
}
