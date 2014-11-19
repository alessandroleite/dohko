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
package org.excalibur.service.deployment.resource.validation;


import static org.excalibur.core.deployment.utils.DeploymentUtils.deploymentSchema;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.excalibur.core.deployment.domain.Deployment;
import org.excalibur.core.deployment.utils.DeploymentUtils;
import org.xml.sax.SAXException;

/**
 * Validates the deployment description with the XSD.
 * <p>
 * You can read more about on the following links:
 * <ul>
 *   <li>http://stackoverflow.com/questions/3428273/validate-jaxbelement-in-jpa-jax-rs-web-service/</li>
 *   <li>http://www.verborgh.be/articles/2009/11/21/easy-restfull-jax-rs-webservices-and-extended-wadl-on-glassfish-v3-using-ant-/</li>
 *   <li>http://stackoverflow.com/questions/10398119/practical-validation-of-jaxb-data-with-jax-rs</li>
 * <ul>
 * </p>
 */
@Provider
@Consumes("application/xml")
public class DeploymentResourceValidation implements MessageBodyReader<Deployment>
{
    @Context
    protected Providers providers;

    public DeploymentResourceValidation()
    {
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type == Deployment.class;
    }

    @Override
    public Deployment readFrom(Class<Deployment> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        Deployment deployment;
        try
        {
            JAXBContext jaxbContext = null;
            ContextResolver<JAXBContext> resolver = providers.getContextResolver(JAXBContext.class, mediaType);

            if (null != resolver)
            {
                jaxbContext = resolver.getContext(type);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                unmarshaller.setSchema(deploymentSchema());
                deployment = (Deployment) unmarshaller.unmarshal(entityStream);
            }
            else
            {
                deployment = DeploymentUtils.unmarshal(entityStream);
            }

            return deployment;
        }
        catch (JAXBException e)
        {
            throw new WebApplicationException(e);
        }
        catch (SAXException e)
        {
            throw new WebApplicationException(e);
        }
    }
}
