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
package org.excalibur.service.aws;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class CommandExample
{
    public static void main(String[] args) throws ExecuteException, IOException, InterruptedException
    {
        CommandLine command = new CommandLine("/bin/bash");
        command.addArgument("-c", false);
        command.addArgument("iperf3 -t 30 -c iperf.scottlinux.com >> output.txt", false);
        
        //Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "iperf3 -t 60 -c localhost"});
       // System.out.println(new Mirror().on(process).get().field("pid"));
        
        //process.waitFor();
        
//        System.out.println(process.exitValue());
//        ManagementFactory.getRuntimeMXBean().getName();  
//        System.out.println(IOUtils.readLines(process.getInputStream()));
        
        //String command = "iperf3 -t 30 -c iperf.scottlinux.com";
        
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10);
        
        final DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(new PumpStreamHandler());
        executor.setExitValue(1);
        
        executor.execute(command, new ExecuteResultHandler()
        {
            @Override
            public void onProcessFailed(ExecuteException e)
            {
                e.printStackTrace();
            }
            
            @Override
            public void onProcessComplete(int exitValue)
            {
                System.out.println(exitValue);
            }
        });
    }
}
