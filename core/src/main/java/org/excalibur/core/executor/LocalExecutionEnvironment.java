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
package org.excalibur.core.executor;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.excalibur.core.cloud.api.VirtualMachine;

public class LocalExecutionEnvironment implements ExecutionEnvironment
{
    private final Map<String, Serializable> variables;
    private final VirtualMachine            node_;
    private final File                      workDir_;

    public LocalExecutionEnvironment(VirtualMachine node, File workDir)
    {
        this.node_ = node;
        this.workDir_ = workDir;
        
        Map<String, Serializable> vars = new HashMap<String, Serializable>();
        
        for(String key : System.getenv().keySet())
        {
            vars.put(key, System.getenv(key));
        }
        
        variables = Collections.unmodifiableMap(vars);
    }

    @Override
    public Map<String, Serializable> getEnviromentMap()
    {
        return variables;
    }

    @Override
    public VirtualMachine getLocation()
    {
        return node_;
    }

    @Override
    public File getWorkingDirectory()
    {
        return workDir_;
    }
}
