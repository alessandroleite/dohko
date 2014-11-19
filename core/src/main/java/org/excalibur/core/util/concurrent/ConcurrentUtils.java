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
package org.excalibur.core.util.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConcurrentUtils
{
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentUtils.class.getName());

    private ConcurrentUtils()
    {
        throw new UnsupportedOperationException();
    }

    public static void awaitQuietly(CountDownLatch latch)
    {
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            logger.warn("The thread was interrupted before or while it was waiting for a notification. Message: {}", e.getMessage());
        }
    }
    
    /**
     * 
     * @param latch
     * @param timeout the maximum time to wait in milliseconds.
     */
    public static void awaitQuietly(CountDownLatch latch, long timeout)
    {
        try
        {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            logger.warn("The thread was interrupted before or while it was waiting for a notification. Message: {}", e.getMessage());
        }
    }
}
