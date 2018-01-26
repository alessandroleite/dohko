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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.excalibur.core.io.BigFileReader;
import org.excalibur.core.util.AnyThrow;

import com.google.common.base.Strings;

public final class Files
{
    /**
     * Private constructor.
     */
    private Files()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates and returns an instance to iterate over the lines of the given file.
     * 
     * @param file the file to iterate over its lines.
     * @return a {@link Iterable} with the lines of the file.
     * @throws IOException if the file does not exist.
     */
    public static Iterable<String> readLines(File file) throws IOException
    {
        return new BigFileReader(file);
    }

    /**
     * Creates and returns an instance to iterate over the lines of the file converting the given pathname string into an abstract pathname.
     * 
     * @param pathname the file to iterate over its lines.
     * @return a {@link Iterable} with the lines of the file.
     * @throws IOException if the file does not exist.
     */
    public static Iterable<String> readLines(String pathname) throws IOException
    {
        return new BigFileReader(pathname);
    }

    /**
     * Returns the number of lines in a {@link File} represented by {@code filename}.
     * 
     * @param filename a pathname
     * @return The number of lines. It's greater or equals than zero.
     * @throws IOException if the file does not exists or can't be read.
     */
    public static int countLines(String filename) throws IOException
    {
        return countLines(new File(filename));
    }

    /**
     * Returns the number of lines in a {@code file}.
     * 
     * @param file a file to count its number of lines.
     * @return the number of lines. It's greater or equals than zero.
     * @throws IOException if the file does not exists or can't be read.
     */
    public static int countLines(File file) throws IOException
    {
        InputStream is = new BufferedInputStream(new FileInputStream(file));

        try
        {
            byte[] c = new byte[1024];

            int count = 0;
            int readChars = 0;

            boolean empty = true;

            while ((readChars = is.read(c)) != -1)
            {
                empty = false;
                for (int i = 0; i < readChars; ++i)
                {
                    if (c[i] == '\n')
                    {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        }
        finally
        {
            IOUtils2.closeQuietly(is);
        }
    }


    public static boolean copy(InputStream from, OutputStream to)
    {
        try
        {
            IOUtils.copy(from, to);
        }
        catch (IOException exception)
        {
            AnyThrow.throwUncheked(exception);
        }
        return true;
    }

    public static void copy(InputStream from, File to)
    {
        try
        {
            copy(from, new FileOutputStream(to));
        }
        catch (FileNotFoundException e)
        {
            AnyThrow.throwUncheked(e);
        }
    }
    
    public static String expandHomePrefixReference(String path)
    {
    
    	if (!Strings.isNullOrEmpty(path) && '~' == FilenameUtils.getPrefix(path.trim()).charAt(0))
    	{
    		return path.trim().replaceFirst("^~",System.getProperty("user.home"));
    	}
    	
    	return path;
    }
}
