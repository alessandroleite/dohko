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
package org.excalibur.core.workflow.listener;

import org.excalibur.core.workflow.flow.ActivityStateChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWorkflowActivityStateListener implements WorkflowActivityStateListener
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void stateChanged(ActivityStateChangedEvent event)
    {
        if (event != null)
        {
//            logger.info("Received the event: {} from the activity: {}", event.getType().name(), event.getActivity().getLabel());
            // WorkflowRepository repository = ((WorkflowContext)event.getContext().getParentContext()).getWorkflowRepository();

            // repository.update(event.getActivity().getDescription());
            // WorkflowActivityState activityState = new WorkflowActivityState(event.getActivity().getDescription());
            // activityState.setUpdateTime(event.getStateTime()).setState(event.getActivity().getState().get().name());
            //
            // repository.insertActivityState(activityState);
        }
        else
        {
            logger.info("Ignored a <null> event!");
        }
    }
}
