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
package org.excalibur.core.deployment.utils;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;

import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.util.JAXBContextFactory;
import org.springframework.util.ClassUtils;
import org.xml.sax.SAXException;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DeploymentUtils
{
    private static final Schema DEPLOYMENT_XSD_SCHEMA;
    private static final JAXBContextFactory<?> DEPLOYMENT_JAXB_CONTEXT_FACTORY;
    
    public static final String DEFAULT_DEPLOYMENT_TASK_TYPE = System.getProperty("org.excalibur.service.deployment.default.task.type",
            "org.excalibur.core.deployment.domain.task.DeploymentTask");

    static
    {
        try
        {
            DEPLOYMENT_XSD_SCHEMA = JAXBContextFactory.getSchema(ClassUtils.getDefaultClassLoader().getResource(
                    "org/excalibur/core/deployment/domain/deployment.xsd"));
            DEPLOYMENT_JAXB_CONTEXT_FACTORY = new JAXBContextFactory(DEPLOYMENT_XSD_SCHEMA, Deployment.class.getPackage().getName());
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private DeploymentUtils()
    {
        throw new UnsupportedOperationException();
    }
    
    public static Schema getDeploymentSchema()
    {
        return deploymentSchema();
    }
    
    public static Schema deploymentSchema()
    {
        return DEPLOYMENT_XSD_SCHEMA;
    }
    
    public static <T> String marshall(T object) throws JAXBException
    {
        return DEPLOYMENT_JAXB_CONTEXT_FACTORY.marshal(checkNotNull(object));
    }
    
    public static <T> String marshalQuietly(T object)
    {
        return DEPLOYMENT_JAXB_CONTEXT_FACTORY.marshalQuietly(object);
    }
                             
    public static <T> T unmarshal(String xml) throws JAXBException
    {
        checkState(!isNullOrEmpty(xml));
        return (T) DEPLOYMENT_JAXB_CONTEXT_FACTORY.unmarshal(xml);
    }

    public static <T> T unmarshal(InputStream input) throws JAXBException, SAXException
    {
        return (T) DEPLOYMENT_JAXB_CONTEXT_FACTORY.unmarshal(input);
    }
}
