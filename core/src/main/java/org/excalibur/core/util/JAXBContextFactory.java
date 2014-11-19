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
package org.excalibur.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@SuppressWarnings("unchecked")
public final class JAXBContextFactory<E>
{
    private static final Logger LOG = LoggerFactory.getLogger(JAXBContextFactory.class.getName());
    public static final String DEFAULT_XML_ENCODING = "UTF-8";
    protected static final ConcurrentMap<Object, Future<JAXBContext>> JAXB_CONTEXTS = new ConcurrentHashMap<Object, Future<JAXBContext>>();

    protected final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

    private Schema schema;
    private String[] contextPath;

    public static Schema getSchema(final URL url)
    {
        Schema schema = null;
        try
        {
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(url);
        }
        catch (SAXException e)
        {
            LOG.warn(e.getMessage(), e);
        }

        return schema;
    }

    private static JAXBException throwAsJAXBException(Throwable e)
    {
        return new JAXBException(e.getMessage(), e);
    }

    private static final class JAXBContextBuildingTask
    {
        <T> JAXBContext build(final Object key, final T value) throws JAXBException
        {
            Future<JAXBContext> f = JAXB_CONTEXTS.get(key);

            if (f == null)
            {
                Callable<JAXBContext> eval = new Callable<JAXBContext>()
                {
                    @Override
                    public JAXBContext call() throws Exception
                    {
                        JAXBContext context;
                        if (value.getClass().isArray())
                        {
                            context = JAXBContext.newInstance((Class[]) value);
                        }
                        else
                        {
                            context = JAXBContext.newInstance((String) value);
                        }
                        return context;
                    }
                };

                FutureTask<JAXBContext> ft = new FutureTask<JAXBContext>(eval);
                f = JAXB_CONTEXTS.putIfAbsent(key, ft);

                if (f == null)
                {
                    f = ft;
                    ft.run();
                }
            }

            try
            {
                return f.get();
            }
            catch (CancellationException ex)
            {
                JAXB_CONTEXTS.remove(key);
                return null;
            }
            catch (InterruptedException ex)
            {
                throw throwAsJAXBException(ex);
            }
            catch (ExecutionException ex)
            {
                throw throwAsJAXBException(ex);
            }
        }
    }

    private static final JAXBContextBuildingTask CONTEXT_BUILDER = new JAXBContextBuildingTask();

    public JAXBContextFactory() throws JAXBException
    {
        super();
    }

    public JAXBContextFactory(final Class<?>... types) throws JAXBException
    {
        if (types != null && types.length > 0)
        {
            final String packageName = types[0].getPackage().getName();

            this.contextPath = new String[] { packageName };
            CONTEXT_BUILDER.build(packageName, types);
        }
    }

    public JAXBContextFactory(Schema schema, String... contextPath) throws JAXBException
    {
        this.contextPath = contextPath;
        this.setSchema(schema);

        if (contextPath != null && contextPath.length > 0)
        {
            StringBuilder sb = new StringBuilder(contextPath[0]);

            for (int i = 1; i < contextPath.length; i++)
            {
                sb.append(":" + contextPath[i]);
            }

            CONTEXT_BUILDER.build(contextPath, sb.toString());
        }
    }

    protected void setSchema(Schema schema)
    {
        this.schema = schema;
        if (schema != null)
        {
            this.documentFactory.setSchema(schema);
            this.documentFactory.setValidating(Boolean.TRUE);
            documentFactory.setNamespaceAware(Boolean.TRUE);
            documentFactory.setIgnoringComments(Boolean.TRUE);
        }
    }

    public JAXBContextFactory(Schema schema)
    {
        setSchema(schema);
    }

    protected DocumentBuilderFactory getDocumentFactory()
    {
        return this.documentFactory;
    }

    protected Document getDocument(E type) throws ParserConfigurationException, JAXBException
    {
        Document document = this.getDocumentFactory().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(type.getClass()).createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_XML_ENCODING);
        marshaller.marshal(type, document);
        return document;
    }

    protected Schema getSchema()
    {
        return schema;
    }

    private String getContextPath()
    {
        return getContextPath(contextPath[0]);
    }

    private static String getContextPath(String... path)
    {
        StringBuilder sb = new StringBuilder(path[0]);
        for (int i = 1; i < path.length; i++)
        {
            sb.append(":" + path[i]);
        }
        return sb.toString();
    }

    public static JAXBContext getContext(String... contextPath) throws JAXBException
    {
        JAXBContext context;
        if (contextPath != null && contextPath.length > 0)
        {
            Future<JAXBContext> f = JAXBContextFactory.JAXB_CONTEXTS.get(contextPath);

            if (f == null)
            {
                context = CONTEXT_BUILDER.build(contextPath, getContextPath(contextPath));
            }
            else
            {
                try
                {
                    context = f.get();
                }
                catch (InterruptedException e)
                {
                    throw throwAsJAXBException(e);
                }
                catch (ExecutionException e)
                {
                    throw throwAsJAXBException(e);
                }
            }
        }
        else 
        {
            context = JAXBContext.newInstance();
        }
        return context;
    }

    protected JAXBContext getContext() throws JAXBException
    {
        return getContext(this.contextPath);
    }

    protected Marshaller createMarshaller(String packageName) throws JAXBException
    {
        final Marshaller marshaller = getContext(packageName).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_XML_ENCODING);

        if (!(this.getSchema() == null))
        {
            marshaller.setSchema(getSchema());
        }

        return marshaller;
    }

    protected Marshaller createMarshaller() throws JAXBException
    {
        return createMarshaller(getContextPath());
    }

    protected Unmarshaller createUnmarshaller(String contextPath) throws JAXBException
    {

        final Unmarshaller unmarshaller = getContext(contextPath).createUnmarshaller();

        if (!(getSchema() == null))
        {
            unmarshaller.setSchema(getSchema());
        }

        return unmarshaller;
    }

    protected Unmarshaller createUnmarshaller() throws JAXBException
    {
        return this.createUnmarshaller(getContextPath());
    }

    public E unmarshal(final String xml, final String charsetName) throws JAXBException
    {
        ByteArrayInputStream input = new ByteArrayInputStream(
                xml.getBytes(Charset.forName((charsetName != null && charsetName.isEmpty()) ? charsetName : DEFAULT_XML_ENCODING)));
        try
        {
            return (E) this.createUnmarshaller().unmarshal(input);
        }
        finally
        {
            IOUtils.closeQuietly(input);
        }
    }

    public E unmarshal(final String xml) throws JAXBException
    {
        return this.unmarshal(xml, DEFAULT_XML_ENCODING);
    }

    public final String marshal(Object obj) throws JAXBException
    {
        StringWriter writer = null;
        try
        {
            writer = new StringWriter();
            createMarshaller().marshal(obj, writer);
            return writer.toString();
        }
        finally
        {
            IOUtils.closeQuietly(writer);
        }
    }

    public final String marshalQuietly(Object obj)
    {
        if (obj == null)
        {
            LOG.debug("Type to marshall was null. Returning null...");
            return null;
        }

        try
        {
            return marshal(obj);
        }
        catch (JAXBException e)
        {
            LOG.warn(e.getMessage(), e);
            return null;
        }
    }

    public Document loadFromXmlFile(File xmlFile) throws IOException, ParserConfigurationException, SAXException
    {
        Document document = this.documentFactory.newDocumentBuilder().parse(xmlFile);
        document.normalizeDocument();
        return document;
    }

    public final E unmarshal(final File file) throws JAXBException, SAXException
    {
        return (E) this.createUnmarshaller().unmarshal(file);
    }

    public final E unmarshal(final InputStream input) throws JAXBException, SAXException
    {
        return (E) this.createUnmarshaller().unmarshal(input);
    }

    public Document getXmlDocumentFromText(final String xmlText) throws IOException, ParserConfigurationException, SAXException
    {
        Document document = this.getDocumentFactory().newDocumentBuilder().parse(new ByteArrayInputStream(xmlText.getBytes(DEFAULT_XML_ENCODING)));
        document.normalizeDocument();
        return document;
    }

}
