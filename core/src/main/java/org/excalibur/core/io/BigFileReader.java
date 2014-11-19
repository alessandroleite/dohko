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
package org.excalibur.core.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

public class BigFileReader implements Iterable<String>, Closeable
{
    /**
     * The buffer to read the file.
     */
    private final BufferedReader reader_;

    /**
     * A reference to the {@link File}.
     */
    private final File file_;

    /**
     * A flag to indicate that the file must be closed when reached the end.
     */
    private final boolean autocloseable_;

    /**
     * The file iterator.
     */
    private final Iterator<String> interator_;

    /**
     * Creates a new {@link BigFileReader} instance by converting the given pathname string into an abstract pathname.
     * 
     * @param filePath
     *            A pathname of the file to be read.
     * @throws IOException
     *             If the file does not exist.
     */
    public BigFileReader(final String filePath) throws IOException
    {
        this(new File(filePath));
    }

    /**
     * 
     * Creates a new {@link BigFileReader} instance by converting the given pathname string into an abstract pathname.
     * 
     * @param filePath
     *            A pathname of the file to be read.
     * @param closeOnEnd
     *            A flat to indicates if the file must be closed when reached the end.
     * @throws IOException
     *             If the file does not exist.
     */
    public BigFileReader(final String filePath, boolean closeOnEnd) throws IOException
    {
        this(new File(filePath), closeOnEnd);
    }

    /**
     * Creates a new {@link BigFileReader} instance to read the given {@link File}.
     * 
     * @param file
     *            The file to be read. Might not be <code>null</code>.
     * @throws IOException
     *             If the file does not exist.
     */
    public BigFileReader(File file) throws IOException
    {
        this(file, true);
    }

    /**
     * Creates a new {@link BigFileReader} instance to read the given {@link File}.
     * 
     * @param file
     *            The file to be read. Might not be <code>null</code>.
     * @param closeOnEnd
     *            A flag to indicate that the file must be closed when reached the end.
     * @throws IOException
     *             If the file does not exist.
     */
    public BigFileReader(File file, boolean closeOnEnd) throws IOException
    {
        this.file_ = Preconditions.checkNotNull(file);
        reader_ = new BufferedReader(new FileReader(file));
        this.autocloseable_ = closeOnEnd;
        this.interator_ = new FileIterator();
    }

    /**
     * Count the number of lines. It does not move the cursor.
     * 
     * @return The number of lines in the file.
     * @throws IOException
     *             If file does not exists.
     */
    public int count() throws IOException
    {
        BigFileReader other = null;
        int cont = 0;

        try
        {
            other = new BigFileReader(this.file_);

            while (other.iterator().hasNext())
            {
                cont++;
            }
        }
        finally
        {
            if (other != null)
            {
                other.close();
            }
        }
        return cont;
    }

    @Override
    public void close() throws IOException
    {
        reader_.close();
    }

    @Override
    public Iterator<String> iterator()
    {
        return this.interator_;
    }

    private final class FileIterator implements Iterator<String>
    {
        /**
         * The value of the current line.
         */
        private String currentLine_;

        @Override
        public boolean hasNext()
        {
            try
            {
                currentLine_ = reader_.readLine();
            }
            catch (IOException exception)
            {
                currentLine_ = null;
            }
            finally
            {
                if (currentLine_ == null && autocloseable_)
                {
                    try
                    {
                        close();
                    }
                    catch (IOException ignore)
                    {
                        Logger.getLogger(BigFileReader.class.getName()).log(Level.FINEST, ignore.getMessage(), ignore);
                    }
                }
            }

            return currentLine_ != null;
        }

        @Override
        public String next()
        {
            return currentLine_;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.close();
        super.finalize();
    }
}
