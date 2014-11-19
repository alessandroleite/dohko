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
package org.excalibur.service.status.heartbeat;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.excalibur.core.util.SystemUtils2.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


import org.excalibur.discovery.ws.ext.ObjectMapperProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HeartbeatSender
{
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatSender.class.getName());

    private final Object monitor_ = new Object();

    private final String host_;
    private final String uri_;

    private ScheduledExecutorService executor_;

    private ScheduledFuture<?> future_;

    private boolean shutdown_;

    private volatile long lastActivityTime_ = -1;

    public HeartbeatSender(String host)
    {
        this.host_ = host;
        checkArgument(!isNullOrEmpty(host_));
        this.uri_ = String.format("http://%s:%s/status", host_, getIntegerProperty("org.excalibur.server.port", 8080));
    }
    
    /**
     * @return the lastActivityTime
     */
    public long getLastActivityTime()
    {
        return lastActivityTime_;
    }



    protected void signalActivity()
    {
        this.lastActivityTime_ = System.nanoTime();
    }

    /**
     * Sets the heartbeat in seconds.
     */
    public HeartbeatSender setHeartbeat(int heartbeatSeconds)
    {
        synchronized (this.monitor_)
        {
            if (this.shutdown_)
            {
                return this;
            }

            // cancel any existing heartbeat task
            if (this.future_ != null)
            {
                this.future_.cancel(true);
                this.future_ = null;
            }

            if (heartbeatSeconds > 0)
            {
                // wake every heartbeatSeconds / 2 to avoid the worst case
                // where the last activity comes just after the last heartbeat
                long interval = SECONDS.toNanos(heartbeatSeconds) / 2;
                lastActivityTime_ = -1;
                ScheduledExecutorService executor = createExecutorIfNecessary();
                Runnable task = new HeartbeatRunnable(interval);
                this.future_ = executor.scheduleAtFixedRate(task, interval, interval, TimeUnit.NANOSECONDS);
            }
        }
        
        return this;
    }

    /**
     * Shutdown the heartbeat process, if any.
     */
    public void shutdown()
    {
        ExecutorService executorToShutdown = null;
        synchronized (this.monitor_)
        {
            if (this.future_ != null)
            {
                this.future_.cancel(true);
                this.future_ = null;
            }

            if (this.executor_ != null)
            {
                // to be safe, we shouldn't call shutdown holding the monitor.
                executorToShutdown = this.executor_;

                this.shutdown_ = true;
                this.executor_ = null;
            }
        }

        if (executorToShutdown != null)
        {
            executorToShutdown.shutdown();
        }
    }
    
    private ScheduledExecutorService createExecutorIfNecessary()
    {
        synchronized (this.monitor_)
        {
            if (this.executor_ == null)
            {
                this.executor_ = Executors.newSingleThreadScheduledExecutor();
            }
            return this.executor_;
        }
    }

    private final class HeartbeatRunnable implements Runnable
    {
        private final long heartbeatNanos;

        private HeartbeatRunnable(long heartbeatNanos)
        {
            this.heartbeatNanos = heartbeatNanos;
        }

        public void run()
        {
            try
            {
                long now = System.nanoTime();

                if (now > (lastActivityTime_ + this.heartbeatNanos))
                {
                    Client client = ClientBuilder.newClient().register(ObjectMapperProvider.class).register(JacksonFeature.class);
                    WebTarget target = client.target(uri_);

                    Response response = target.request().get();

                    LOG.debug("Received the answer [{}] from host [{}]", response.readEntity(Long.class), host_);
                    
                    signalActivity();
                }
            }
            catch (Exception e)
            {
                lastActivityTime_ = -1; 
                LOG.debug("Failed to reach uri [{}]", uri_);
            }
        }
    }
}
