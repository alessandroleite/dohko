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
package org.excalibur.discovery.service.zoo;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.excalibur.core.util.JAXBContextFactory;

import com.google.common.base.Preconditions;

public class JAXBInstanceSerializer<T> implements InstanceSerializer<T>
{
    private final JAXBContext context;
    @SuppressWarnings("unused")
    private final Class<T> payloadClass;
    private final Schema schema;

    public JAXBInstanceSerializer(Schema schema, JAXBContext context, Class<T> payloadClass)
    {
        this.schema = schema;
        this.context = Preconditions.checkNotNull(context);
        this.payloadClass = payloadClass;
    }

    public JAXBInstanceSerializer(Schema schema, Class<T> payloadClass) throws JAXBException
    {
        this(schema, JAXBContextFactory.getContext(Preconditions.checkNotNull(payloadClass).getPackage().getName()), payloadClass);
    }

    public JAXBInstanceSerializer(Class<T> payloadClass) throws JAXBException
    {
        this(null, payloadClass);
    }

    @Override
    public byte[] serialize(ServiceInstance<T> instance) throws Exception
    {
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        if (schema != null)
        {
            marshaller.setSchema(schema);
        }

        try (StringWriter writer = new StringWriter())
        {
            marshaller.marshal(instance, writer);
            return writer.toString().getBytes();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ServiceInstance<T> deserialize(byte[] bytes) throws Exception
    {
        Unmarshaller unmarshaller = context.createUnmarshaller();

        if (schema != null)
        {
            unmarshaller.setSchema(schema);
        }

        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes))
        {
            return (ServiceInstance<T>) unmarshaller.unmarshal(bais);
        }
    }
}
