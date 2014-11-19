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
package br.cic.unb.chord.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ReflectionUtil
{
    private final static Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class.getName());

    public static Object newInstance(String clazz)
    {
       checkState(!Strings.isNullOrEmpty(clazz));
       
        try
        {
            return Class.forName(clazz).newInstance();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public static Object newInstance(final String clazzName, final Object... params)
    {
        try
        {
            Constructor<?> method = Class.forName(clazzName).getDeclaredConstructor(getArgsType(params));
            checkNotNull(method).setAccessible(true);
            return method.newInstance(params);
        }
        catch (Exception exception)
        {
            throw new RuntimeException("Error in create instance of class " + clazzName);
        }
    }

    /**
     * Execute the given static method with the params.
     * 
     * @param clazz
     * @param methodName
     *            - The name of the method to execute. May not be <code>null</code>
     * @param params
     *            The method params
     * @return
     * @throws NullPointerException
     *             Throw if the method name is null or if it not declared in the class.
     * @throws RuntimeException
     *             Throw if the method could not be executed or because of any exception throw during the given method execution.
     */
    public static Object executeStaticMethod(String clazz, String methodName, Object... params)
    {
        try
        {
            Class<?> instanceClass = Class.forName(clazz);
            Method method = instanceClass.getDeclaredMethod(methodName, getArgsType(params));
            
            checkNotNull(method, "Method " + methodName + " not declared in this class " + clazz);
            
            method.setAccessible(true);
            return method.invoke(null, params);
        }
        catch (Exception exception)
        {
            final String message = "Error on create new instance of type: " + clazz;
            LOG.error(message, exception);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * @param params
     * @return
     */
    private static Class<?>[] getArgsType(Object... params)
    {
        Class<?>[] paramsTypes = null;
        if (params != null && params.length > 0)
        {
            paramsTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++)
            {
                Class<?> superClassType = params[i].getClass().getSuperclass();
                if (superClassType != null && !superClassType.equals(Object.class))
                {
                    paramsTypes[i] = superClassType;
                }
                else
                {
                    paramsTypes[i] = params[i].getClass();
                }
            }
        }
        return paramsTypes;
    }

}
