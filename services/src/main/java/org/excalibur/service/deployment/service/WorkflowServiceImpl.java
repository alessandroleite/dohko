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
package org.excalibur.service.deployment.service;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import org.excalibur.core.workflow.domain.TaskDescription;
import org.excalibur.core.workflow.domain.WorkflowActivityDescription;
import org.excalibur.core.workflow.domain.WorkflowDescription;
import org.excalibur.core.workflow.repository.TaskRepository;
import org.excalibur.core.workflow.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowServiceImpl implements WorkflowService
{
    @Autowired
    private WorkflowRepository workflowRepository_;
    
    @Autowired
    private TaskRepository taskRepository_;
    
    @Override
    public Integer insert(final WorkflowDescription metadata)
    {
        checkState(metadata != null);

        List<WorkflowActivityDescription> activities = metadata.getActivities();
        checkState(!activities.isEmpty());

        metadata.setId(workflowRepository_.insert(metadata));

        for (WorkflowActivityDescription activity : activities)
        {
           activity.setInternalId(workflowRepository_.insert(activity));

            for (TaskDescription task : activity.getTasks())
            {
                task.setId(taskRepository_.insert(task));
            }
        }
        
        return metadata.getId();
    }
}
