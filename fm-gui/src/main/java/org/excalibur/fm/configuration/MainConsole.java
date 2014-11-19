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
package org.excalibur.fm.configuration;

import org.excalibur.core.cloud.api.InstanceType;
import org.excalibur.core.cloud.api.InstanceTypes;
import org.excalibur.core.cloud.api.domain.GeographicRegion.GeographicRegions;
import org.excalibur.core.services.ProviderService;
import org.excalibur.fm.solver.constraints.Constraint;
import org.excalibur.fm.solver.constraints.Operator;
import org.excalibur.fm.solver.constraints.Variable;
import org.excalibur.fm.solver.constraints.problem.InstanceSelection;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Strings;

import solver.ResolutionPolicy;

import static org.excalibur.fm.solver.constraints.Vars.CORES;
import static org.excalibur.fm.solver.constraints.Vars.COST;
import static org.excalibur.fm.solver.constraints.Vars.MEMORY;

public class MainConsole 
{
	
	public static void main(String[] args) 
	{
		 ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/applicationContext.xml");
		 final ProviderService providerService = context.getBean(ProviderService.class);
		 
		 // get all instance types offered at the North America region of all providers
		 InstanceTypes instanceTypes = new InstanceTypes(providerService.getInstanceTypesAvailableOnRegion(GeographicRegions.NORTH_AMERICA.toType()));
        
        // get all instance types of all regions of all providers
        instanceTypes = new InstanceTypes(providerService.getAllInstanceTypesOfAllRegions());
        
        Constraint[] constraints = new Constraint[] 
        { 
                new Constraint(new Variable("cores", 9), Operator.GE),
                new Constraint(new Variable("memory", 4 * 1024), Operator.GE), 
                new Constraint(new Variable("cost", 1000), Operator.LE)
        };
        
        InstanceTypes allSolutions  = new InstanceSelection(instanceTypes).findAllSolutions(constraints);
        
        InstanceTypes allOptimalSolutions = new InstanceSelection(instanceTypes).findAllOptimalSolutions(ResolutionPolicy.MINIMIZE, constraints, COST);
        
        InstanceTypes paretoFront = new InstanceSelection(instanceTypes).findParetoFront(ResolutionPolicy.MAXIMIZE, constraints, CORES.getName(), MEMORY.getName());
        
        System.out.format("\n%36s%13s%36s\n\n", " ", "All solutions", " ");
        print(allSolutions);
        
        System.out.format("\n%33s%17s%33s\n\n", " ", "Optimal solutions", " ");
        print(allOptimalSolutions);
        
        System.out.format("\n%36s%13s%36s\n\n", " ", "Pareto front", " ");
        print(paretoFront); 
	}
	
	static void print(Iterable<InstanceType> instanceTypes)
    {//https://code.google.com/p/j-text-utils/ http://docs.oracle.com/javase/6/docs/api/java/util/Formatter.html http://stackoverflow.com/questions/2745206/output-in-a-table-format-in-javas-system-out
        
        System.out.format("%10s|%15s|%8s|%8s|%10s|%12s|%15s|", "Provider", "Instance type", "# cores", "RAM (GB)", "Cost (US)", "Family type", "Region");        
        System.out.printf("\n%s\n", Strings.repeat("~", 85));
        
        for (InstanceType type: instanceTypes)
        {
            System.out.format("%10s|%15s|%8s|%8s|%10s|%12s|%15s|", type.getProvider().getName(), type.getName(), type.getConfiguration().getNumberOfCores(), 
                    type.getConfiguration().getRamMemorySizeGb(), type.getCost(), type.getFamilyType().name(), type.getRegion().getName());
            System.out.printf("\n%s\n", Strings.repeat("-", 85));
        }
        System.out.printf("\n%s\n", Strings.repeat("=", 85));
    }
}
