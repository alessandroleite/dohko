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
package org.excalibur.aqmp.handler;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Collection;
import java.util.Properties;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.AMQP.Queue;

public class QueueStatsProcessor
{
    @Autowired
    private RabbitAdmin admin_;

    @Autowired(required = false)
    private Collection<Queue> queues_;

    /**
     * Returns the number of enqueued messages in the given queue or <code>null</code> if it does not exist.
     * 
     * @param queueName
     *            the name of the queue. Might not be <code>null</code> or empty.
     * @return the number of enqueued messages or <code>null</code> if the queue does not exist.
     */
    public Integer getMessageCount(String queueName)
    {
        return getIntegerQueuePropertyValue(queueName, "QUEUE_MESSAGE_COUNT", null);
    }

    /**
     * Returns the number of consumers of a given queue or <code>null</code> if the queue does not exist.
     * 
     * @param queueName
     *            the name of the queue. Might not be <code>null</code> or empty.
     * @return The number of consumers or <code>null</code> if the queue does not exist.
     */
    public Integer getNumberOfConsumers(String queueName)
    {
        return getIntegerQueuePropertyValue(queueName, "QUEUE_CONSUMER_COUNT", null);
    }

    protected Integer getIntegerQueuePropertyValue(String queueName, String propertyName, Integer defaultValue)
    {
        Properties properties = admin_.getQueueProperties(queueName);

        return properties != null ? properties.get(propertyName) == null ? defaultValue : Integer.parseInt(properties.getProperty(propertyName))
                : null;
    }
    
    /**
     * Purges the contents of the given queue and waits the completion of the purge.
     * @param queueName the queue name. Might not be <code>null</code> or empty.
     */
    public void purgeQueue(String queueName)
    {
        checkArgument(!isNullOrEmpty(queueName));
        this.admin_.purgeQueue(queueName, false);
    }
}
