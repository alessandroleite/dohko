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
package br.cic.unb.chord.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Closeables2
{
    private static final Logger LOG = LoggerFactory.getLogger(Closeables2.class.getName());

    private Closeables2()
    {
        throw new UnsupportedOperationException();
    }

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
                LOG.warn("Error on close a resource. The error message is: {}", e.getMessage());
            }
        }
    }

    public static void closeQuietly(Closeable closeable, Socket socket)
    {
        closeQuietly(closeable);
        closeQuietly(socket);
    }

    /**
     * 
     * @param socket
     * @param closeables
     */
    public static void closeQuietly(Socket socket, Closeable... closeables)
    {
        closeQuietly(closeables);
        closeQuietly(socket);
    }

    /**
     * <p>
     * Closes a socket without propagating {@link IOException}.
     * <p>
     * Null value is ignore. <strong>Notice:</strong> This method only exists because {@link Socket} does not implement {@link Closeable} in Java 6.
     * 
     * @param socket
     *            the socket to close.
     */
    public static void closeQuietly(Socket socket)
    {
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
            }
        }
    }

}
