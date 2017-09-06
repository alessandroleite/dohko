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
package org.excalibur.service.compute.executor;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.excalibur.core.execution.domain.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class Worker
{
    private static final Logger LOG = LoggerFactory.getLogger(Worker.class.getName());
    
    public void execute(final Application application, ExecuteResultHandler executeResultHandler, ExecuteStreamHandler streamHandler) throws ExecuteException, IOException
    {
        final String commandLine = application.getCommandLine();
        
        DefaultExecutor executor = new DefaultExecutor();
        
        CommandLine command = new CommandLine("/bin/sh");
        command.addArgument("-c", false);
        command.addArgument(commandLine, false);
        executor.setStreamHandler(streamHandler);
        executor.execute(command, System.getenv(), executeResultHandler);
        
        LOG.debug("Launched the execution of task: [{}], uuid: [{}]", commandLine, application.getId());
    }
}
