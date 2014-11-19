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
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//http://stackoverflow.com/questions/17687240/spring-delegate-to-custom-proxy-wrapper-for-interface-injection
public class AutowireCandidateResolverConfigurer implements ApplicationContextAware, BeanFactoryPostProcessor
{
    private ApplicationContext applicationContext_;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) beanFactory;
        System.out.println(bf.getBeanDefinitionCount());
        System.err.println(bf);
        // adds a postprocessor to configure the repository of the services.
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        //see 
        // http://stackoverflow.com/questions/11606504/registering-beansprototype-at-runtime-in-spring
        //http://stackoverflow.com/questions/15328904/dynamically-declare-beans-at-runtime-in-spring
        //http://spring.io/blog/2011/02/14/spring-3-1-m1-introducing-profile
        //http://spring.io/blog/2011/06/21/spring-3-1-m2-testing-with-configuration-classes-and-profiles/
//        AbstractApplicationContext context = (AbstractApplicationContext) context;
//        BeanDefinitionRegistry registry = ((BeanDefinitionRegistry) context.getBeanFactory());
//        registry.registerBeanDefinition("userRepository", BeanDefinitionBuilder.genericBeanDefinition());
        applicationContext_ = applicationContext;
    }
}
