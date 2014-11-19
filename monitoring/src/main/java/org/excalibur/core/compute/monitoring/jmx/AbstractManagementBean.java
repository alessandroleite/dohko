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
package org.excalibur.core.compute.monitoring.jmx;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractManagementBean implements DynamicMBean
{
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Returns the object name the MBean is registered within the {@link javax.management.MBeanServer}. May be <code>null</code> in case the instance
     * is not registered to an MBeanServer, but used standalone.
     * 
     * @return The object name or <code>null</code> if not registered to an {@link javax.management.MBeanServer}.
     */
    protected abstract String getObjectName();

    @Override
    public AttributeList getAttributes(String[] attributes)
    {
        final AttributeList result = new AttributeList();

        for (String attribute : attributes)
        {
            try
            {
                result.add(new Attribute(attribute, getAttribute(attribute)));
            }
            catch (AttributeNotFoundException e)
            {
                LOG.warn("Attribute {} does not exist.", attribute);
            }
            catch (MBeanException | ReflectionException e)
            {
                LOG.warn(e.getMessage());
            }
        }

        return result;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes)
    {
        final AttributeList result = new AttributeList();

        for (int i = 0; i < attributes.size(); i++)
        {
            Attribute attribute = (Attribute) attributes.get(i);
            try
            {
                setAttribute(attribute);
                result.add(attribute);
            }
            catch (AttributeNotFoundException e)
            {
                LOG.warn("Attribute {} not found!", e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public void setAttribute(Attribute attr) throws AttributeNotFoundException
    {
        throw new AttributeNotFoundException(attr.getName());
    }
}
