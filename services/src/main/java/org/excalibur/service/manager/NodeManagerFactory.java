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
package org.excalibur.service.manager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.excalibur.core.util.AnyThrow;

import com.google.common.base.Preconditions;

public final class NodeManagerFactory
{
    private final static AtomicReference<NodeManager> manager_ = new AtomicReference<NodeManager>();
    private static final CountDownLatch LATCH = new CountDownLatch(1);
    
    private NodeManagerFactory()
    {
        throw new UnsupportedOperationException();
    }
    
    public static void setManager(NodeManager manager)
    {
        if (manager_.compareAndSet(null, manager))
        {
            LATCH.countDown();
        }
    }
    
    public static NodeManager getManagerReference()
    {
        try
        {
            LATCH.await();
            NodeManager manager = manager_.get();
            Preconditions.checkArgument(manager!= null, "call setManager(manager)");
            
            return manager_.get();
            
        }
        catch (InterruptedException e)
        {
            Thread.interrupted();
            AnyThrow.throwUncheked(e);
        }
        
        return null;
    }
}
