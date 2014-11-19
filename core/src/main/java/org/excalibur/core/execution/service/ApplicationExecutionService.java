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
package org.excalibur.core.execution.service;

import java.util.List;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.ApplicationExecDescription;
import org.excalibur.core.execution.domain.ApplicationExecutionResult;
import org.excalibur.core.execution.domain.repository.ApplicationDescriptionRepository;
import org.excalibur.core.execution.domain.repository.ApplicationExecutionResultRepository;
import org.excalibur.core.execution.domain.repository.ScriptStatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Collections.*;
import static com.google.common.base.Preconditions.*;

@Service
public class ApplicationExecutionService
{
    @Autowired
    private ApplicationExecutionResultRepository applicationExecutionResultRepository_;

    @Autowired
    private ApplicationDescriptionRepository applicationDescriptionRepository_;

    @Autowired
    private ScriptStatementRepository scriptStatementRepository_;
    
    public List<ApplicationExecDescription> listPendentExecutions()
    {
        List<ApplicationExecDescription> result = this.applicationDescriptionRepository_.listPendentExecutions();
        
        for (ApplicationExecDescription app: result)
        {
            app.setApplication(this.scriptStatementRepository_.findById(app.getApplication().getId()));
        }
        
        return result;
    }

    public List<ApplicationExecDescription> listPendentExecutionsForInstance(VirtualMachine instance)
    {
        List<ApplicationExecDescription> pendentExecutions = this.applicationDescriptionRepository_.listExecutionsForResource(instance.getType().getName());
        pendentExecutions.addAll(this.applicationDescriptionRepository_.listExecutionsForResource("all-instance-types"));
        
//        pendentExecutions.addAll(this.applicationDescriptionRepository_.listPendentExecutionsForInstance(instance.getId()));
        
        for (ApplicationExecDescription app: pendentExecutions)
        {
            app.setApplication(this.scriptStatementRepository_.findById(app.getApplication().getId()));
        }
        
        return unmodifiableList(pendentExecutions);
    }

    public void insertApplicationExecutionResult(ApplicationExecutionResult execution)
    {
        checkNotNull(execution);
        
        if (execution.getApplication().getId() == null)
        {
            execution.setApplication(this.applicationDescriptionRepository_.findByName(execution.getApplication().getName()));
        }
        
        Integer id = this.applicationExecutionResultRepository_.insertApplicationExecution(execution);
        execution.setId(checkNotNull(id));
    }

    public void insertStartExecution(ApplicationExecutionResult execution)
    {
        this.insertApplicationExecutionResult(execution);
    }

    public void insertFinishedExecution(ApplicationExecutionResult execution)
    {
        this.applicationExecutionResultRepository_.updateApplicationExecution(execution);
    }
}
