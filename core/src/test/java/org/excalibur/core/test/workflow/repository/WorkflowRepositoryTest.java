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
package org.excalibur.core.test.workflow.repository;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.excalibur.core.test.TestSupport;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.repository.WorkflowRepository;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class WorkflowRepositoryTest extends TestSupport
{
    protected WorkflowRepository workflowRepository;
    private WorkflowDescription workflow;
    
    
    @Before
    public void setup() throws IOException
    {
        super.setup();
        workflowRepository = openRepository(WorkflowRepository.class);
        workflow = new WorkflowDescription()
               .setCreatedIn(new Date())
               .setName("wk-1")
               .setStartActivityId(1)
               .setUser(user);
    }

    @Test
    public void must_insert_one_workflow()
    {
        Integer id = workflowRepository.insert(workflow);
        assertThat(id, equalTo(workflowRepository.findWorkflowById(id).getId()));
        List<WorkflowDescription> workflows = workflowRepository.getUserWorkflows(1);
        assertThat(1, equalTo(workflows.size()));
    }

    @Test
    public void must_insert_one_workflow_activity()
    {
        Integer workflowId = workflowRepository.insert(workflow);
        Integer workflowActivityId = 1;
        
        WorkflowActivityDescription activity = new WorkflowActivityDescription()
                   .setWorkflow(new WorkflowDescription(workflowId))
                   .setId(workflowActivityId)
                   .setLabel("a1")
                   .setType("foo.Bar");
        
        workflowRepository.insert(Collections.singletonList(activity));
        assertThat(workflowActivityId, equalTo(workflowRepository.findWorkflowActivityById(workflowId, workflowActivityId).getId()));
        
        List<WorkflowActivityDescription> workflowActivities = workflowRepository.getWorkflowActivities(workflowId);
        assertThat(1, equalTo(workflowActivities.size()));
    }
    
    @Test
    public void must_be_null()
    {
        WorkflowDescription nullWorkflow = workflowRepository.findWorkflowById(1000);
        assertNull(nullWorkflow);
    }
}
