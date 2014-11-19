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
package org.excalibur.core.util.process;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ShutdownHookCloseableDestroyer implements Runnable
{
    /**
     * The {@link Closeable} resources to close when the JVM is shutting down.
     */
    private final List<Closeable>  resources = new LinkedList<Closeable>();
    
    /**
     * The thread registered at the JVM to execute the shutdown handler.
     */
    private CloseableResouceThread closeableResouceThread;
    
    /**
     * Indicates if the JVM is in process of shutdown.  
     */
    private AtomicBoolean          isShuttingDown = new AtomicBoolean(false);
    /**
     * Whether or not this {@link ShutdownHookCloseableDestroyer} has been registered as a shutdown hook.
     */
    private AtomicBoolean          added = new AtomicBoolean(false);

    private class CloseableResouceThread extends Thread
    {
        private boolean shouldDestroy = true;

        public CloseableResouceThread()
        {
            super("Shutdown-Hook-Thread");
        }

        public void run()
        {
            if (shouldDestroy)
            {
                ShutdownHookCloseableDestroyer.this.run();
            }
        }

        public void setShouldDestroy(final boolean shouldDestroy)
        {
            this.shouldDestroy = shouldDestroy;
        }
    }

    public ShutdownHookCloseableDestroyer()
    {
    }

    /**
     * Registers this <code>ProcessDestroyer</code> as a shutdown hook, uses reflection to ensure pre-JDK 1.3 compatibility.
     */
    private void addShutdownHook()
    {
        if (!isShuttingDown.get())
        {
            closeableResouceThread = new CloseableResouceThread();
            Runtime.getRuntime().addShutdownHook(closeableResouceThread);
            added.set(true);
        }
    }
    
    /**
     * Returns <code>true</code> if the specified <code>resource</code> was successfully removed from the list of resources to close upon VM exit.
     * 
     * @param resource
     *            the resource to close.
     * @return <code>true</code> if the specified resource was successfully removed.
     */
    public boolean remove(final Closeable resource)
    {
        synchronized (resources)
        {
            boolean processRemoved = resources.remove(resource);
            if (processRemoved && resources.size() == 0)
            {
                removeShutdownHook();
            }
            return processRemoved;
        }
    }
    
    private void removeShutdownHook()
    {
        if (added.get() && !this.isShuttingDown.get())
        {
            boolean removed = Runtime.getRuntime().removeShutdownHook(closeableResouceThread);
            if (!removed)
            {
                System.err.println("Could not remove shutdown hook");
            }
            
            closeableResouceThread.setShouldDestroy(false);
            closeableResouceThread.start();
            
            try
            {
                closeableResouceThread.join(20000);
            }
            catch (InterruptedException ie)
            {
            }
            closeableResouceThread = null;
            added.set(false);
        }
    }


    /**
     * Returns <code>true</code> if the specified resource was successfully added to the list of resources to close upon VM exit.
     * 
     * @param resource
     *            the process to add
     * @return <code>true</code> if the specified <code>Process</code> was successfully added
     */
    public boolean add(final Closeable resource)
    {
        synchronized (resources)
        {
            // if this list is empty, register the shutdown hook
            if (resources.size() == 0)
            {
                addShutdownHook();
            }
            resources.add(resource);
            return resources.contains(resource);
        }
    }
    
    /**
     * Returns whether or not the {@link ShutdownHookCloseableDestroyer} is registered as as shutdown hook.
     * 
     * @return true if this is currently added as shutdown hook
     */
    public boolean isAddedAsShutdownHook()
    {
        return added.get();
    }
    
    /**
     * Returns the number of registered resources.
     * 
     * @return the number of register resources.
     */
    public int size()
    {
        synchronized (this.resources)
        {
            return resources.size();
        }
    }
    
    @Override
    public void run()
    {
        synchronized (resources)
        {
            isShuttingDown.set(true);
            for (Closeable resource : resources)
            {
                try
                {
                    resource.close();
                }
                catch (IOException e)
                {
                    System.err.println(String.format("Unable to close the resource %s during the process shutdown", resource.getClass().getName()));
                }
            }
        }
    }
}
