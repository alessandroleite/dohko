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
package org.excalibur.bio.sequencing;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.excalibur.core.io.BigFileReader;
import org.excalibur.core.io.utils.IOUtils2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FastaReader implements Iterable<FastaEntry>, Closeable
{
    /**
     * 
     */
    private static final Logger LOG = LoggerFactory.getLogger(BigFileReader.class.getName());

    /**
     * A read/write lock to control the accessing to the method {@link #count()}.
     */
    private static Lock LOCK = new ReentrantLock();

    /**
     * A reference to the file reader.
     */
    private final BigFileReader reader_;

    /**
     * Reference to the fasta file.
     */
    private final File file_;

    /**
     * The reference for the iterator.
     */
    private final Iterator<FastaEntry> iterator_;

    /**
     * The number of sequences available. It's compute just one time.
     */
    private volatile int numberOfSequences_;

    /**
     * The number of sequences read.
     */
    private volatile int numberOfSequencesRead_;

    /**
     * Creates a new instance of a fasta reader.
     * 
     * @param file
     *            The fasta file.
     * @throws IOException
     *             If the file does not exist.
     */
    public FastaReader(File file) throws IOException
    {
        reader_ = new BigFileReader(Preconditions.checkNotNull(file));
        this.file_ = file;
        iterator_ = new FastaReaderIterator();
    }

    /**
     * Returns all sequences available.
     * 
     * @return A {@link List} with all sequences.
     */
    public List<FastaEntry> all()
    {
        List<FastaEntry> entries = new ArrayList<FastaEntry>();

        for (FastaEntry entry : this)
        {
            entries.add(entry);
        }

        return entries;
    }

    /**
     * Returns the number of sequences available in the file.
     * 
     * @return The number of sequences in the file.
     */
    public int count()
    {
        LOCK.lock();

        try
        {
            if (numberOfSequences_ == 0)
            {
                int n = 0;

                try
                {
                    try (FastaReader reader = new FastaReader(file_))
                    {
                        while (reader.iterator().hasNext())
                        {
                            n++;
                        }
                    }
                }
                catch (IOException exception)
                {
                    LOG.warn(exception.getMessage(), exception);
                }

                numberOfSequences_ = n;
            }
        }
        finally
        {
            LOCK.unlock();
        }

        return numberOfSequences_;
    }

    @Override
    public Iterator<FastaEntry> iterator()
    {
        return this.iterator_;
    }

    /**
     * @return the numberOfSequencesRead
     */
    public int getNumberOfSequencesRead()
    {
        return numberOfSequencesRead_;
    }

    private final class FastaReaderIterator implements Iterator<FastaEntry>
    {
        /**
         * An instance for a file iterator.
         */
        private final Iterator<String> iterator_;

        /**
         * The lines to be processed.
         */
        private final List<String> lines_ = new ArrayList<String>();

        /**
         * 
         */
        private FastaEntry cursor_;

        /**
         * Creates a new instance of the {@link FastaReaderIterator}.
         */
        public FastaReaderIterator()
        {
            this.iterator_ = reader_.iterator();
        }

        @Override
        public boolean hasNext()
        {
            if (this.iterator_.hasNext())
            {
                FastaEntry sequence = null;

                String line = iterator_.next();

                if (line != null && !line.trim().isEmpty())
                {
                    lines_.add(line);

                    while (iterator_.hasNext() && !Strings.isNullOrEmpty(line = iterator_.next()) && line.charAt(0) != FastaEntry.SEQUENCE_HEADER)
                    {
                        lines_.add(line);
                        line = null;
                    }

                    sequence = FastaEntry.valueOf(lines_);
                    lines_.clear();

                    numberOfSequencesRead_++;

                    if (!Strings.isNullOrEmpty(line) && line.charAt(0) == FastaEntry.SEQUENCE_HEADER)
                    {
                        lines_.add(line);
                    }
                }

                cursor_ = sequence;
            }
            else
            {
                cursor_ = null;
            }

            return this.cursor_ != null;
        }

        @Override
        public FastaEntry next()
        {
            return this.cursor_;
        }

        @Override
        public void remove()
        {
            // do nothing
        }
    }
    
    @Override
    public void close() throws IOException
    {
        IOUtils2.closeQuietly(this.reader_);
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.close();
        super.finalize();
    }
}
