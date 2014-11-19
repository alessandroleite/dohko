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
package org.excalibur.core.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Rule implements Runnable
{
    protected final Logger LOG = LoggerFactory.getLogger(Rule.class.getName());

    /**
     * Returns the name of the rule. Might not be <code>null</code>, and it should be unique if a rule needs to be uninstalled.
     * 
     * @return the rule's name.
     */
    public abstract String name();

    /**
     * Called when the rule is installed.
     */
    public abstract void init();

    /**
     * Called when the rule is uninstalled.
     */
    public abstract void destroy();

    /**
     * Evaluates the condition. If <code>true</code> the trigger is triggered.
     * 
     * @return <code>true</code> if this {@link Rule} must be triggered.
     */
    public abstract boolean eval();

    /**
     * The action of this {@link Rule}. It's executed if {@link #eval()} returned <code>true</code>.
     * 
     * @throws Exception
     */
    public abstract void trigger() throws Exception;

    @Override
    public void run()
    {
        if (eval())
        {
            try
            {
                trigger();
            }
            catch (Exception e)
            {
                LOG.error("Failed to execute the rules {}. Error message: {}", name(), e.getMessage(), e);
            }
        }
    }

}
