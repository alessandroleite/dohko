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
package org.excalibur.core.deployment.domain.engine;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.excalibur.core.cloud.api.VirtualMachine;
import org.excalibur.core.execution.domain.ScriptStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptStatementProcessor
{
    private static final Logger LOG = LoggerFactory.getLogger(ScriptStatementProcessor.class.getName());
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd-HH-mm-ss");
    
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{\\[(.+?)\\]\\}");

    public static void assignVariableValues(VirtualMachine instance, ScriptStatement statement)
    {
        Matcher matcher = VARIABLE_PATTERN.matcher(statement.getStatement());
        String text = statement.getStatement();

        while (matcher.find())
        {
            int start = matcher.start(1) - 3, end = matcher.end(1) + 2;
            String name = matcher.group(1);
            String var = statement.getStatement().substring(start, end);
            
            LOG.debug("Found the var [{}] on script [{}]", var, statement.getName());

            try
            {
                Object value;
                
                if (name.equalsIgnoreCase("date"))
                {
                    value = DATE_FORMAT.format(new Date());
                }
                else
                {
                    value = new PropertyUtilsBean().getProperty(instance, name);
                }

                if (value != null)
                {
                    text = text.replaceAll(Pattern.quote(var), value.toString());
                    
                    LOG.debug("The value for variable [{}] on script [{}] is [{}]", var, statement.getName(), value);
                }
                else
                {
                    LOG.debug("Ignoring [null-value] for variable [{}]", var);
                }
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
            {
                LOG.error("Invalid variable {}. Error {}", var, e.getMessage(), e);
            }
        }

        statement.setStatement(text);
    }
}
