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
package org.excalibur.core.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allow replayable request to be retried a limited number of times, and impose an exponential back-off delay before returning.
 * <p>
 * The back-off delay grows rapidly according to the formula <code>50 * (<i>{@link TransformingHttpCommand#getFailureCount()}</i> ^ 2)</code>. For
 * example:
 * <table>
 * <tr>
 * <th>Number of Attempts</th>
 * <th>Delay in milliseconds</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>50</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>200</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>450</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>800</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>1250</td>
 * </tr>
 * </table>
 * <p>
 * This implementation has two side-effects. It increments the command's failure count with
 * because this failure count value is used to determine how many times the command has already been tried. It also closes the response's content
 * input stream to ensure connections are cleaned up.
 * 
 * @author James Murty
 */
public class BackoffLimitedRetryHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(BackoffLimitedRetryHandler.class.getName());
    
    public static final BackoffLimitedRetryHandler INSTANCE = new BackoffLimitedRetryHandler();

    private final int retryCountLimit = 30;
    private final long delayStart = 10000;

    public void imposeBackoffExponentialDelay(int failureCount, String commandDescription)
    {
        imposeBackoffExponentialDelay(delayStart, 2, failureCount, retryCountLimit, commandDescription);
    }

    public void imposeBackoffExponentialDelay(long period, int pow, int failureCount, int max, String commandDescription)
    {
        imposeBackoffExponentialDelay(period, period * 10l, pow, failureCount, max, commandDescription);
    }

    public void imposeBackoffExponentialDelay(long period, long maxPeriod, int pow, int failureCount, int max, String commandDescription)
    {
        long delayMs = (long) (period * Math.pow(failureCount, pow));
        // Add random delay to avoid thundering herd problem when multiple
        // simultaneous failed requests retry after sleeping for the same delay.
        
        delayMs += new Random().nextInt((int) (delayMs / 10));
        delayMs = delayMs > maxPeriod ? maxPeriod : delayMs;
        LOG.debug("Retry [{}/{}]: delaying for [{}] ms: [{}]", failureCount, max, delayMs, commandDescription);
        
        try
        {
            Thread.sleep(delayMs);
        }
        catch (InterruptedException e)
        {
        	AnyThrow.throwUncheked(e);
//            Throwables.propagateIfPossible(e, RuntimeException.class);
        }
    }
}
