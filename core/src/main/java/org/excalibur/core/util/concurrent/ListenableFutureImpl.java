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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.*;

public final class ListenableFutureImpl<T> extends ListenableFutureBase<T> implements NotifyListenableFuture<T>
{
    private T                    futureValue_;
    private Throwable            futureException_;
    private volatile Future<T>   future_;
    private final CountDownLatch latch_ = new CountDownLatch(1);
    
    protected Future<T> getFuture() throws InterruptedException
    {
        latch_.await();
        return this.future_;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        boolean cancelled = false;
        try
        {
            cancelled = getFuture().cancel(mayInterruptIfRunning);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return cancelled;
    }
    
    @Override
    public boolean isCancelled()
    {
        boolean isCancelled = false;
        try
        {
            isCancelled = getFuture().isCancelled();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return isCancelled;
    }
    
    @Override
    public boolean isDone()
    {
        if (this.isFutureCompleted.get())
        {
            return true;
        }
        
        try
        {
            return getFuture().isDone();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    @Override
    public T get() throws InterruptedException, ExecutionException
    {
        if (!isFutureCompleted.get())
        {
            future_.get();
        }
        
        if (futureException_ != null)
        {
            throw new ExecutionException(futureException_);
        }
        
        return futureValue_;
    }
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
    {
        if (!isFutureCompleted.get())
        {
            future_.get(timeout, unit);
        }
        
        if (futureException_ != null)
        {
            throw new ExecutionException(futureException_);
        }
        
        return futureValue_;
    }
    
    @Override
    public ListenableFuture<T> setFuture(Future<T> future)
    {
        this.future_ = checkNotNull(future);
        latch_.countDown();
        return this;
    }

    @Override
    public void notifyDone(T result)
    {
       this.futureValue_ = result;
       fireListeners();
    }

    @Override
    public void notifyFailure(Throwable exception)
    {
        this.futureException_ = exception;
        fireListeners();
    }
}
