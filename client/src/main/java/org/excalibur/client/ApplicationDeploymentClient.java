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
package org.excalibur.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.excalibur.client.commands.DeployCommand;
import org.excalibur.core.Command;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ApplicationDeploymentClient 
{
    private static final Map<String, Command> COMMANDS = new HashMap<>();
    
    static
    {
        COMMANDS.put(DeployCommand.NAME, new DeployCommand());
    }
    
        
    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException
    {
       JCommander jc = new JCommander();
       
       for (Command command: COMMANDS.values())
       {
           jc.addCommand(command.getName(), command);
       }
       
       try
       {
           jc.parse(args);
           Command command = COMMANDS.get(jc.getParsedCommand());

           if (command == null)
           {
               jc.usage();
               System.exit(1);
           }

           command.execute(System.out);
       }
       catch (ParameterException ex)
       {
           jc.usage();
       }
    }
}
