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
package org.excalibur.core.test.workflow;

import java.io.IOException;
import java.util.Date;

import org.excalibur.core.deployment.domain.task.DeploymentTask;
import org.excalibur.core.test.TestSupport;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.repository.WorkflowRepository;
import org.junit.Before;
import org.junit.Test;

public class WorkflowTaskTest extends TestSupport
{
    private WorkflowRepository workflowRepository;
    
    @Before
    public void setUp() throws ClassNotFoundException, IOException
    {
        super.setup();
        this.workflowRepository = openRepository(WorkflowRepository.class);
    }

    @Test
    public void test() throws Exception
    {
        WorkflowDescription workflow = new WorkflowDescription()
                  .setCreatedIn(new Date())
                  .setName("wk-1")
                  .setStartActivityId(1)
                  .setUser(user);
        
        Integer workflowId = workflowRepository.insert(workflow);
        workflow.setId(workflowId);
        
        Integer workflowActivityId = 1;

        workflowRepository.insert
        (       new WorkflowActivityDescription()
                .setLabel("a1")
                .setId(workflowActivityId++)
                .setType(DeploymentTask.class.getName())
                .setWorkflow(workflow)
        );

        workflowRepository.insert
        (       new WorkflowActivityDescription()
                .setParents(String.valueOf(1))
                .setId(workflowActivityId++)
                .setLabel("a2")
                .setType(DeploymentTask.class.getName())
                .setWorkflow(workflow)
        );
        
//        WorkflowTask workflowTask = new WorkflowTask(workflowRepository, workflowId);
//        workflowTask.call();
    }
}
