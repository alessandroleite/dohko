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
package org.excalibur.core.exec;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents a subprocess in execution. In a local execution it's just an abstraction of {@link java.lang.Process}. The
 * {@link java.io.Serializable} means that the can interrupt the execution and re-start it later.
 */
public interface Process
{
    /**
     * Causes the current thread to wait until the given timeout. This methods returns immediately if the subprocess has already terminated. If the
     * subprocess has not yet terminated, the calling thread will be blocked until the timeout.
     * 
     * @param timeout
     *            The timeout to wait in milliseconds. The value -1 means infinite time.
     * @return the exit value of the subprocess represented by this Process object. By convention, the value 0 indicates normal termination.
     * @throws InterruptedException
     *             If the current thread is interrupted by another thread while it is waiting, then the wait is ended and an InterruptedException is
     *             thrown.
     */
    int waitFor(long timeout) throws InterruptedException;

    /**
     * Causes the current thread to wait until the process has terminated. This methods returns immediately if the subprocess has already terminated.
     * If the subprocess has not yet terminated, the calling thread will be blocked until the subprocess exits.
     * 
     * @return the exit value of the subprocess represented by this Process object. By convention, the value 0 indicates normal termination.
     * @throws InterruptedException
     *             If the current thread is interrupted by another thread while it is waiting, then the wait is ended and an InterruptedException is
     *             thrown.
     */
    int waitFor() throws InterruptedException;

    /**
     * Returns the exit value for the process.
     * 
     * @return the exit value of the subprocess represented by this Process object. By convention, the value 0 indicates normal termination.
     * @throws IllegalStateException
     *             if the subprocess has not yet terminated.
     */
    int exitValue() throws IllegalStateException;

    /**
     * Kills the subprocess and this {@link Process}.
     */
    void destroy();

    /**
     * Returns the input stream connected to the normal output of the subprocess. <strong>Implementation note</strong>: it is a good idea for the
     * returned input stream to be buffered.
     * 
     * @return the input stream connected to the normal output of the subprocess.
     */
    InputStream getInputStream();

    /**
     * Returns the output stream connected to the normal input of the subprocess. <strong>Implementation note</strong>: it is a good idea for the
     * returned output stream to be buffered.
     * 
     * @return the output stream connected to the normal input of the subprocess.
     */
    OutputStream getOutputStream();
    
    /**
     * Returns this process ID.
     * @return This process ID.
     */
    int getPID();

}
