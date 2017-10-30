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
package org.excalibur.core.workflow.flow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.domain.repository.RegionRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.util.ThreadUtils;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.workflow.definition.Activity;
import org.excalibur.core.workflow.repository.WorkflowTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class WorkflowExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger(WorkflowExecutor.class.getName());

    private final WorkflowContext context_;
//    private final AtomicBoolean started_ = new AtomicBoolean();
    private final AtomicBoolean stopped_ = new AtomicBoolean();
    private final ExecutorService executor_;
    private final WorkflowTaskRepository taskRepository_;
    private final UserRepository userRepository_;
    private final RegionRepository regionRepository_;
    private final VirtualMachine node_;

    public WorkflowExecutor(WorkflowContext context, WorkflowTaskRepository taskRepository, UserRepository userRepository, RegionRepository regionRepository,
            VirtualMachine node)
    {
        this.context_ = checkNotNull(context);
        checkNotNull(context.getWorkflowCoordinator(), "The coordinator might not be null!");
        checkNotNull(context.getWorkflowExecutionStrategy(), "The workflow execution policy might not be null!");

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("wket-" + context.getWorkflow().getDescription().getName() + "-%d")
                .build();

        this.taskRepository_ = taskRepository;
        this.userRepository_ = userRepository;
        this.regionRepository_ = regionRepository;

        this.executor_ = DynamicExecutors.newScalingThreadPool(1, context.getWorkflow().getActivities().length, 5, TimeUnit.MINUTES, threadFactory);
        context.registerExecutors(this.executor_);
        this.node_ = node;
    }

    public List<ActivityExecutionContext> execute()
    {
        List<Future<ActivityExecutionContext>> futures = Lists.newArrayList();
        
        while (!stopped_.get() && context_.getWorkflowExecutionStrategy().hashNext())
        {
            try
            {
                final Activity activities[] = context_.getWorkflowExecutionStrategy().nextActivities();

                for (final Activity activity : activities)
                {
                    Future<ActivityExecutionContext> future = executor_.submit(new Callable<ActivityExecutionContext>()
                    {
                        @Override
                        public ActivityExecutionContext call() throws Exception
                        {
                            ActivityExecutionContext context = new DefaultActivityExecutionContext(context_, activity, taskRepository_,
                                    userRepository_, regionRepository_, node_);
                            activity.execute(context);
                            return context;
                        }
                    });
                    futures.add(future);
                }
            }
            catch (InterruptedException e)
            {
                LOG.debug("Thread [{}] was interrupted. The error message is [{}]", Thread.currentThread().getName(), e.getMessage());
            }
        }
        
        List<ActivityExecutionContext> contexts = Lists.newArrayList();
        
        for(Future<ActivityExecutionContext> future: futures)
        {
            try
            {
                ActivityExecutionContext ctx = future.get();
                contexts.add(ctx);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException e)
            {
                LOG.error("Error on getting the result of a workflow task activity. Error message [{}]", e.getMessage(), e);
            }
        }
        
        LOG.info("Finished the execution of workflow [{}]", context_.getWorkflow().getDescription().getName());
//        shutdown();
        
        return contexts;
    }

    void shutdown()
    {
        List<Runnable> awaitingTasks = new ArrayList<Runnable>();
        
        LOG.debug("There are [{}] executors to shutdown", context_.getRegisteredWorkflowExecutors().length);

        for (ExecutorService executor : context_.getRegisteredWorkflowExecutors())
        {
            ThreadUtils.awaitTerminationAndShutdownAndIgnoreInterruption(executor, 1, TimeUnit.MINUTES);
        }

        LOG.debug("[{}] finished with [{}] awaiting tasks.", this.getClass().getSimpleName(), awaitingTasks.size());
    }
}
