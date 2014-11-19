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
package org.excalibur.core.compute.monitoring.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarProxy;

import com.google.common.io.Files;

public final class SigarFactory
{
    static
    {
        final String name = "libsigar-amd64-linux.so";

        final String path = SigarFactory.class.getName().replace('.', '/').trim();
        String fullPath = SigarFactory.class.getResource("/" + path + ".class").toString();

        boolean injar = fullPath.startsWith("jar:");

        if (injar)
        {
            InputStream in = getDefaultClassLoader().getResourceAsStream(name);
//            String jar = fullPath.substring(fullPath.lastIndexOf(":") + 1, fullPath.lastIndexOf("!"));
//            File libDir = new File(jar.substring(0, jar.lastIndexOf("/") + 1));
            File sigarHome = new File(System.getProperty("user.home"), String.format("/.sigarLib/%s", name));
            
            if (!sigarHome.exists())
            {
                FileOutputStream fos = null;
                try
                {
                    Files.createParentDirs(sigarHome);
                    fos = new FileOutputStream(sigarHome);
                    copyLarge(in, fos, new byte[1024 * 4]);
                }
                catch (IOException e)
                {
                    if (fos != null)
                    {
                        try
                        {
                            fos.close();
                        }
                        catch (IOException e1)
                        {
                        }
                    }
                }
            }
            
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + sigarHome.getPath().replaceAll(name, ""));
        }
        else
        {
            String libPath = getDefaultClassLoader().getResource(name).getPath();
            libPath = libPath.substring(0, libPath.indexOf(name) - 1);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + libPath);
        }
        System.out.println(">======= java.library.path =======>" + System.getProperty("java.library.path"));
    }

    private static final Sigar SIGAR_INSTANCE = new Sigar();

    private SigarFactory()
    {
        throw new UnsupportedOperationException();
    }

    public static SigarProxy getInstance()
    {
        return SIGAR_INSTANCE;
    }

    /**
     * Return the default ClassLoader to use: typically the thread context ClassLoader, if available; the ClassLoader that loaded the ClassUtils class
     * will be used as fallback.
     * 
     * @return the default ClassLoader (never <code>null</code>)
     * @see java.lang.Thread#getContextClassLoader()
     */
    private static ClassLoader getDefaultClassLoader()
    {
        ClassLoader cl = null;
        try
        {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex)
        {
            if (Logger.getLogger(SigarFactory.class).isDebugEnabled())
            {
                Logger.getLogger(SigarFactory.class).debug(ex.getMessage(), ex);
            }
        }
        if (cl == null)
        {
            cl = SigarFactory.class.getClassLoader();
        }
        return cl;
    }

    private static final int EOF = -1;

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an <code>OutputStream</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a <code>BufferedInputStream</code>.
     * <p>
     * 
     * @param input
     *            the <code>InputStream</code> to read from
     * @param output
     *            the <code>OutputStream</code> to write to
     * @param buffer
     *            the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException
     *             if the input or output is null
     * @throws IOException
     *             if an I/O error occurs
     * @since 2.2
     * Copied from Apache org.apache.commons.io.IOUtils. 
     */
    private static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException
    {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
