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
package org.excalibur.core.deployment.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.excalibur.core.Identifiable;
import org.excalibur.core.task.CommonContext;

import com.google.common.base.Strings;

public class ValidationContext implements CommonContext
{
    final static ThreadLocal<WeakHashMap<Object, Identifiable<?>>> REGISTRY = new ThreadLocal<WeakHashMap<Object, Identifiable<?>>>();

    private static Map<Object, Identifiable<?>> getRegistry()
    {
        return REGISTRY.get();
    }

    static boolean isRegistered(Identifiable<?> value)
    {
        Map<Object, Identifiable<?>> m = getRegistry();
        return m != null && m.containsKey(value.getId());
    }

    static void register(Identifiable<?> value)
    {
        if (value != null)
        {
            Map<Object, Identifiable<?>> m = getRegistry();
            if (m == null)
            {
                REGISTRY.set(new WeakHashMap<Object, Identifiable<?>>());
            }
            getRegistry().put(value.getId(), value);
        }
    }

    static void unregister(Identifiable<?> value)
    {
        if (value != null)
        {
            Map<Object, Identifiable<?>> m = getRegistry();
            if (m != null)
            {
                m.remove(value.getId());
                if (m.isEmpty())
                {
                    REGISTRY.remove();
                }
            }
        }
    }

    private boolean isCyclic_;
    private Map<String, Object> data_ = new ConcurrentHashMap<String, Object>();
    private final List<String> errors_ = new ArrayList<String>();
    private final Object lock_ = new Object();

    public void cyclic()
    {
        this.isCyclic_ = true;
    }

    public <T> Map<String, ?> getData()
    {
        synchronized (lock_)
        {
            return Collections.unmodifiableMap(this.data_);
        }
    }

    /**
     * @return the isCyclic
     */
    public boolean isCyclic()
    {
        return isCyclic_;
    }

    public boolean hasError()
    {
        synchronized (lock_)
        {
            return !this.errors_.isEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String key)
    {
        synchronized (lock_)
        {
            return (T) data_.get(key);
        }
    }

    public void addError(String error)
    {
        if (!Strings.isNullOrEmpty(error))
        {
            synchronized (lock_)
            {
                this.errors_.add(error);
            }
        }
    }

    public List<String> getErrors()
    {
        List<String> errors;
        synchronized (lock_)
        {
            errors = Collections.unmodifiableList(this.errors_);
        }
        return errors;
    }

    public void put(String key, Object data)
    {
        if (!Strings.isNullOrEmpty(key))
        {
            this.data_.put(key, data);
        }
    }
}
