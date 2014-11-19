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
package org.excalibur.bio.sequencing.tasks;


import org.excalibur.bio.sequencing.SequencePair;
import org.excalibur.core.task.TaskContext;
import org.excalibur.core.task.TaskResult;
import org.excalibur.core.task.TaskState;
import org.excalibur.core.task.impl.AbstractTaskTypeSupport;
import org.excalibur.core.task.impl.TaskResultBuilder;
import org.excalibur.core.util.JAXBContextFactory;
import org.excalibur.core.workflow.domain.TaskDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.currentTimeMillis;

public class LocalPairsScoreTask extends AbstractTaskTypeSupport<Double>
{
    private static final Logger LOG = LoggerFactory.getLogger(LocalPairsScoreTask.class.getName());
    
    public LocalPairsScoreTask(TaskDescription task)
    {
        super(task);
    }

    @Override
    protected TaskResult<Double> doExecute(TaskContext context)
    {
        TaskResultBuilder<Double> builder = new TaskResultBuilder<Double>();

        try
        {
            JAXBContextFactory<SequencePair> jxb = new JAXBContextFactory<SequencePair>(SequencePair.class);
            
            SequencePair pair = jxb.unmarshal(this.getDescription().getExecutable());
            
            LocalPairsScoreCallable task = new LocalPairsScoreCallable(pair);

            builder.setStartTimeInMillis(currentTimeMillis());
            builder.setState(TaskState.EXECUTING);
            
            double[] score = task.call();
            
            builder.setFinishTimeInMillis(currentTimeMillis());
            builder.setState(TaskState.SUCCESS);
            
            builder.setTaskResult(score[0]);
        }
        catch (Exception e)
        {
            builder.setState(TaskState.ERROR, e);
            builder.setFinishTimeInMillis(currentTimeMillis());
            LOG.error("Error on executing task [{}]; error: [{}]", this.getClass().getName(), e.getMessage(), e);
        }

        TaskResult<Double> result = builder.build();

        return result;
    }
}
