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
package org.excalibur.service.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

//http://stackoverflow.com/questions/16569091/is-it-possible-to-inject-a-bean-defined-with-component-as-an-argument-to-a-bean
//http://www.dineshonjava.com/2012/07/writing-beanfactorypostprocessor-in.html
//https://code.google.com/p/givwenzen/wiki/StepClassInstantiation
//http://docs.spring.io/spring/docs/1.2.9/reference/beans.html
public class DBIBeanFactoryPostprocessor implements BeanFactoryPostProcessor
{
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
//        beanFactory.addBeanPostProcessor(new BeanPostProcessor()
//        {
//            @Override
//            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
//            {
//                System.out.println(beanName);
//                return bean;
//            }
//            
//            @Override
//            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
//            {
//                System.out.println(beanName);
//                return bean;
//            }
//        });
        
        System.out.println(beanDefinitionCount);
    }
}
