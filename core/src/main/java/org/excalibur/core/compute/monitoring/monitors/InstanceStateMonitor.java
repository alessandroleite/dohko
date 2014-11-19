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
package org.excalibur.core.compute.monitoring.monitors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import net.vidageek.mirror.dsl.Mirror;

import org.excalibur.core.cloud.api.Provider;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.cloud.api.InstanceStateType;
import org.excalibur.core.cloud.api.compute.ComputeService;
import org.excalibur.core.cloud.api.domain.Instances;
import org.excalibur.core.cloud.service.provisioning.InstancesEvent;
import org.excalibur.core.domain.UserProviderCredentials;
import org.excalibur.core.domain.repository.ProviderRepository;
import org.excalibur.core.domain.repository.UserRepository;
import org.excalibur.core.services.InstanceService;
import org.excalibur.core.util.Memoize;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class InstanceStateMonitor
{
    private static final Logger logger = LoggerFactory.getLogger(InstanceStateMonitor.class.getName());
    
    private ConcurrentMap<Integer, InstanceMonitorTask> MONITORS_TASKS = Maps.newConcurrentMap();

    private final EventBus listenerNotifier = new EventBus();

    private final InstanceService instanceService_;
    private final UserRepository userRepository_;
    private final ProviderRepository providerRepository_;
    private final ListeningExecutorService executor;

    private final Memoize<UserProviderCredentials, InstanceMonitorTask> tasks_ = Memoize.newInstance();

    public InstanceStateMonitor(InstanceService instanceService, ProviderRepository providerRepository, UserRepository userRepository)
    {
        this.instanceService_ = instanceService;
        this.providerRepository_ = providerRepository;
        this.userRepository_ = userRepository;

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("instance-state-monitor-%d").build();

        executor = DynamicExecutors.newListeningDynamicScalingThreadPool(1, 100, 1, TimeUnit.MINUTES, threadFactory);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void monitorUserInstances(String providerName) throws InterruptedException
    {
        logger.info("Starting the job to get the states of the instances available on provider {}!", providerName);

        final Provider provider = this.providerRepository_.findByExactlyProviderName(providerName);
        
        List<UserProviderCredentials> users = this.userRepository_.listUsersLoginCredentialsOfProvider(provider.getId());

        for (final UserProviderCredentials userCredentials : users)
        {
            if (MONITORS_TASKS.get(userCredentials.getUserId()) == null)
            {
                InstanceMonitorTask task = tasks_.get(userCredentials, new Callable<InstanceStateMonitor.InstanceMonitorTask>()
                {
                    @Override
                    public InstanceMonitorTask call() throws Exception
                    {
                        return new InstanceMonitorTask(userCredentials);
                    }
                });

                MONITORS_TASKS.putIfAbsent(userCredentials.getId(), task);
            }
        }

        List<ListenableFuture<List<VirtualMachine>[]>> futures = (List) executor.invokeAll(MONITORS_TASKS.values());

        for (ListenableFuture<List<VirtualMachine>[]> future : futures)
        {
            try
            {
                List<VirtualMachine>[] lists = future.get();

                if (!lists[0].isEmpty())
                {
                    this.listenerNotifier.post(new InstancesEvent(this, lists[0], InstanceStateType.CREATED, new Date()));
                }

                if (!lists[1].isEmpty())
                {
                    this.listenerNotifier.post(new InstancesEvent(this, lists[1], InstanceStateType.UPDATED, new Date()));
                }

            }
            catch (ExecutionException e)
            {
                logger.error(e.getMessage());
            }
        }

        logger.info("Finished the job to get the states of the instances available on provider {}!", providerName);
    }

    class InstanceMonitorTask implements Callable<List<VirtualMachine>[]>
    {
        private final ComputeService compute_;
        private final UserProviderCredentials credentials_;

        public InstanceMonitorTask(ComputeService compute, UserProviderCredentials userCredentials)
        {
            this.compute_ = checkNotNull(compute);
            this.credentials_ = checkNotNull(userCredentials);
        }

        public InstanceMonitorTask(UserProviderCredentials userCredentials)
        {
            this((ComputeService) new Mirror().on(userCredentials.getProvider().getServiceClass()).invoke().constructor().withArgs(userCredentials),
                    userCredentials);
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<VirtualMachine>[] call() throws Exception
        {
            Instances providerInstances = compute_.listInstances();

            List<VirtualMachine> instances2Update = newArrayList();
            List<VirtualMachine> newInstances = newArrayList();

            for (VirtualMachine instance : providerInstances)
            {
                VirtualMachine vm = instanceService_.getInstanceByNameOnProvider(instance.getName(), credentials_.getProvider());

                if (vm == null)
                {
                    newInstances.add(instance);
                }
                else if (!vm.getState().getState().equals(instance.getState().getState()))
                {
                    instances2Update.add(instance);
                }
            }

            instanceService_.insertInstances(newInstances);
            instanceService_.updateInstances(instances2Update);

            return new List[] { newInstances, instances2Update };
        }
    }
}
