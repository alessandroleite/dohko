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
package org.excalibur.service.test;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import org.junit.Before;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSupport
{
    AbstractApplicationContext context;
    
    @Before
    public void setUp()
    {
        context = new ClassPathXmlApplicationContext("classpath*:META-INF/applicationContext.xml");
    }
    
    protected <T> T getBean(Class<T> requiredType)
    {
        return context.getBean(requiredType);
    }
    
    @SuppressWarnings("unchecked")
    protected <T> T getBean(String name)
    {
        checkState(!isNullOrEmpty(name));
        return (T) this.context.getBean(name);
    }
}
