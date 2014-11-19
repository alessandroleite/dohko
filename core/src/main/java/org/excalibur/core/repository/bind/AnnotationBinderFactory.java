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
package org.excalibur.core.repository.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.vidageek.mirror.dsl.Mirror;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationBinderFactory implements BinderFactory
{
    @SuppressWarnings("rawtypes")
    @Override
    public Binder build(Annotation annotation)
    {
        final BindBean bind = (BindBean) annotation;
        Binder<BindBean, ?> binder = new Mirror().on(bind.binder()).invoke().constructor().withoutArgs();
        return binder;
    }

    // see ColonPrefixNamedParamStatementRewriter, ColonPrefixNamedParamStatementRewriter, ConcreteStatementContext
    public static class FieldBinder implements Binder<BindBean, Object>
    {
        private static final Logger LOG = LoggerFactory.getLogger(FieldBinder.class.getName());

        /**
         * http://kasparov.skife.org/jdbi/api/index.html
         * 
         * Parameterized Queries
         * 
         * jDBI support positional and named parameterized queries. Parameterized statements can be described in the form select id, name from bar
         * where id = ? or select id, name from bar where id = :id. The advantage to using the second form is that you can then use named parameters
         * to execute the statement, as well as positional. Named parameters are matched via \s+(:\w+) outside of quotes, so basically :id, :foo_id,
         * or :id1 type constructions.
         * 
         * Executing a statement with named parameters requires passing in a map with the name ("id" in the above example) as a key, and the value to
         * substitute as the matching value. Calling a positionally parameterized query involves passing in an object array, or collection, of
         * arguments which will bind the elements to the statement in iteration order. There are convenience methods for common cases, as well as a
         * convenient class for building maps of named arguments (Args).
         */
        private static final Pattern PATTERN = Pattern.compile("\\w*:\\S+");

        @Override
        public void bind(SQLStatement<?> q, BindBean bind, Object arg)
        {
            String[] params = bind.params();
            
            if (params == null || params.length == 0)
            {
                params = extractParams(q.getContext().getRawSql());
            }

            for (String param : params)
            {
                try
                {
                    String[] paramParts = extractParamParts(param);
                    Object propertyValue = new PropertyUtilsBean().getProperty(arg, paramParts[1]);
                    
                    if (propertyValue instanceof Map)
                    {
                        Map<?, ?> mapValue = (Map<?, ?>) propertyValue;
                        
                        StringBuilder sb = new StringBuilder();
                        
                        for(Object key :mapValue.keySet())
                        {
                            sb.append(key).append("=").append(mapValue.get(key)).append(";");
                        }
                        
                        propertyValue = sb.toString().substring(0, sb.length() > 1 ? sb.length() - 1 : 0);
                    }
                    
                    q.bind(paramParts[0], propertyValue);
                }
                catch (IllegalAccessException e)
                {
                    LOG.error(e.getMessage(), e);
                }
                catch (InvocationTargetException e)
                {
                    LOG.error(e.getMessage(), e);
                }
                catch (NoSuchMethodException e)
                {
                    LOG.error(e.getMessage(), e);
                }
            }
        }

        private String[] extractParamParts(String param)
        {
            String[] result = new String[] { param, param };
            
            String[] parts = param.split(":");
            if (parts != null && parts.length == 2)
            {
                result = parts;
            }
            
            return result;
        }

        String[] extractParams(final String sql)
        {
            final Matcher m = PATTERN.matcher(sql);
            final ArrayList<String> tokens = new ArrayList<String>();

            while (m.find())
            {
                tokens.add(m.group().substring(1).replaceAll(",", "").replaceAll(Pattern.quote(")"), "").trim());
            }

            return tokens.toArray(new String[tokens.size()]);
        }
    }
}
