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
package org.excalibur.driver.google.compute.aspects;

import java.io.IOException;

public aspect HandleIOException
{
    /**
     * Declare {@link IOException} soft to enable use by clients that not declared to handle {@link IOException}.
     */
    declare soft: IOException: throwsResourceException();

    /**
     * Pick out join points to convert {@link IOException} to {@link ResourceException}. This implementation picks out execution of any method declared to
     * throw {@link IOException} in this project.
     */
    pointcut throwsResourceException():
         (within(org.excalibur.driver.google.compute.GoogleCompute+) && (execution(* *(..)))) ||
         (within(org.excalibur.driver.google.compute.domain.ComputeCredentials.Builder+) && (execution(* identity(..)))); 

    /**
     * This around advice converts {@link IOException} to {@link ResourceException} at all join points picked out by
     * {@link #throwsResourceException()}. That means *no* {@link IOException} will be thrown from this join point, and thus that none will be
     * converted the AspectJ runtime to {@link SoftException}.
     * 
     * @return The reference of the target object.
     * @throws ResourceException
     *             If the target method throws {@link IOException}
     */
    Object around(): throwsResourceException()
    {
        try
        {
            return proceed();
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}
