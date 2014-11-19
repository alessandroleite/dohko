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

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.google.common.collect.Maps;

public class Memoize<K, V>
{
    private final ConcurrentMap<K, Future<V>> entries_ = Maps.newConcurrentMap();

    public Memoize()
    {
    }
    
    public static <K,V> Memoize <K,V> newInstance()
    {
        return new Memoize<K, V>();
    }

    public V get(K key, Callable<V> call)
    {
        V value = null;
        Future<V> f = entries_.get(key);

        if (f == null)
        {
            FutureTask<V> ft = new FutureTask<V>(call);
            f = entries_.putIfAbsent(key, ft);

            if (f == null)
            {
                f = ft;
                ft.run();
            }
        }

        try
        {
            value = f.get();
        }
        catch (InterruptedException e)
        {
            AnyThrow.throwUncheked(e);
        }
        catch (CancellationException e)
        {
            this.entries_.remove(key, f);
        }
        catch (ExecutionException e)
        {
            AnyThrow.throwUncheked(e);
        }
        
        return value;
    }
    
    public void remove(K key)
    {
        this.entries_.remove(key);
    }
}
