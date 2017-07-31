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
package org.excalibur.core.io.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.excalibur.core.util.Strings2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

public final class IOUtils2
{
    private static final Logger LOG = LoggerFactory.getLogger(IOUtils2.class.getName());

    private IOUtils2()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads all of the lines from an {@link InputStream} object. The lines include the line-termination characters, and also include other leading
     * and trailing whitespace.
     * <p>
     * Does not close the {@code InputStream}.
     * 
     * @param is The stream to read from.
     * @throws IOException if an I/O error occurs.
     */
    public static String readLines(InputStream is) throws IOException
    {
        InputStreamReader isr = new InputStreamReader(is);
        try
        {
            return CharStreams.readLines(isr, new LineProcessor<String>()
            {
                final StringBuilder lines = new StringBuilder();

                @Override
                public boolean processLine(String line) throws IOException
                {
                    lines.append(line).append(Strings2.NEW_LINE);
                    return true;
                }

                @Override
                public String getResult()
                {
                    return lines.toString();
                }
            });
        }
        finally
        {
            closeQuietly(isr);
        }
    }

    /**
     * Reads all of the lines from an {@link InputStream} object. The lines include the line-termination characters, and also include other leading
     * and trailing whitespace.
     * 
     * @param file the {@link File} to read. Might not be <code>null</code>.
     * @return the file's content.
     * @throws IOException if an I/O error occurs.
     * @see #readLines(InputStream)
     */
    public static String readLines(File file) throws IOException
    {
        try (InputStream in = new FileInputStream(file))
        {
            return readLines(in);
        }
    }

    /**
     * Reads all of the lines from an {@link InputStream} object. The lines include the line-termination characters, and also include other leading
     * and trailing whitespace.
     * 
     * @param fileName the file to read. Might not be <code>null</code>.
     * @return The file's content.
     * @throws IOException if an I/O error occurs.
     * @see #readLines(File)
     */
    public static String readLines(String fileName) throws IOException
    {
        return readLines(new File(fileName));
    }
    
    public static String readLinesQuietly(File file)
    {
        try
        {
            return readLines(file);
        }
        catch (IOException e)
        {
            if (LOG.isTraceEnabled())
            {
                LOG.trace("Error on reading the file [{}]", file.getAbsolutePath());
            }
            return null;
        }
    }

    /**
     * Closes the resources without throwing an {@link IOException} in case of an error.
     * @param closeables The resources to close.
     */
    public static void closeQuietly(Closeable... closeables)
    {
        for (Closeable c : closeables)
        {
            try
            {
                if (c != null)
                {
                    c.close();
                }
            }
            catch (IOException e)
            {
                LOG.warn("Error on closing a resource. Error message: [{}]", e.getMessage());
            }
        }
    }

    public static void closeQuietly(Iterable<? extends Closeable> iterable)
    {
        if (iterable != null)
        {
            for (Closeable  c: iterable)
            {
                closeQuietly(c);
            }
        }
    }
}
