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

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This class can be used as {@link OutputStream} provided to a {@link PrintStream} that can be used to substitute {@link System#out}. The output is
 * saved in a buffer that can be printed by invocation of {@link #printOutputTo(PrintStream)} to the provided {@link PrintStream}. The content of the
 * buffer can be obtained as {@link String} by the {@link #getOutput()} method. {@link #clearBuffer()} empties the buffer and all output, that has
 * been saved before, is deleted.
 * 
 */
public class MemoryOutputStream extends java.io.OutputStream
{

    /**
     * The internal buffer of this output stream.
     */
    private StringBuffer bufferedOutput;

    /**
     * Creates a new instance of MemoryOutputStream
     */
    public MemoryOutputStream()
    {
        this.bufferedOutput = new StringBuffer();
    }

    /**
     * Clears the internal buffer of this output stream.
     */
    public synchronized void clearBuffer()
    {
        this.bufferedOutput = new StringBuffer();
    }

    /**
     * Overwritten from {@link OutputStream}. Writes the byte <code>b</code> to the internal buffer.
     * 
     * @param b
     *            The byte to write to the internal buffer.
     */
    public synchronized void write(int b)
    {
        /* Convert b to byte and put it into a byte array, */
        byte[] barray = new byte[] { (byte) b };
        /* so that it can be converted to a readable String. */
        String bAsString = new String(barray);
        /* Save to the current buffer. */
        this.bufferedOutput.append(bAsString);
    }

    /**
     * Get the content of the internal buffer.
     * 
     * @return The output that has been saved before.
     */
    public synchronized String getOutput()
    {
        return this.bufferedOutput.toString();
    }

    /**
     * Print the content of the internal buffer to the given {@link PrintStream} <code>out</code>.
     * 
     * @param out
     *            The {@link PrintStream} to print the saved output to.
     */
    public synchronized void printOutputTo(PrintStream out)
    {
        out.print(this.getOutput());
    }
}
