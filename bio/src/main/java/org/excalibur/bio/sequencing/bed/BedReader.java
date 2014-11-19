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
package org.excalibur.bio.sequencing.bed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Preconditions;

import org.excalibur.core.io.BigFileReader;

public final class BedReader implements Iterable<BedEntry>
{
    /**
     */
    private static final Logger LOG = Logger.getLogger(BedReader.class.getName());

    /**
     * A read/write lock to control the accessing to the method {@link #count()}.
     */
    private static Lock LOCK = new ReentrantLock();

    /**
     * A reference to the file reader.
     */
    private final BigFileReader reader_;

    /**
     * Reference to the BED file.
     */
    private final File file_;

    /**
     * The reference for the iterator.
     */
    private final Iterator<BedEntry> iterator_;

    /**
     * The number of sequences available. It's compute just one time.
     */
    private volatile int numberOfSequences_;

    /**
     * The number of sequences read.
     */
    private volatile int numberOfSequencesRead_;

    /**
     * Creates a new instance of a BED reader.
     * 
     * @param file
     *            The BED file. Might not be <code>null</code>.
     * @throws IOException
     *             If the file does not exist.
     */
    public BedReader(File file) throws IOException
    {
        reader_ = new BigFileReader(Preconditions.checkNotNull(file));
        this.file_ = file;
        iterator_ = new BedFileReaderIterator();
    }

    /**
     * Returns all records available.
     * 
     * @return A {@link List} with all sequences.
     */
    public List<BedEntry> all()
    {
        List<BedEntry> entries = new ArrayList<BedEntry>();

        for (BedEntry entry : this)
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
                    BedReader reader = new BedReader(file_);
                    while (reader.iterator().hasNext())
                    {
                        n++;
                    }
                }
                catch (IOException exception)
                {
                    LOG.log(Level.FINEST, exception.getMessage(), exception);
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
    public Iterator<BedEntry> iterator()
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

    private final class BedFileReaderIterator implements Iterator<BedEntry>
    {
        private BedEntry cursor_;

        @Override
        public boolean hasNext()
        {
            Iterator<String> iter = reader_.iterator();

            if (iter.hasNext())
            {
                BedEntry entry = null;

                String line = iter.next();

                if (line != null && !line.trim().isEmpty())
                {
                    String[] fields = line.split("\\s+");

                    if (fields.length >= 3)
                    {
                        entry = new BedEntry(fields[0], Long.parseLong(fields[1]), Long.parseLong(fields[2]));
                        entry.setName(fields[3]);
                        entry.setScore(Integer.parseInt(fields[5]));
                        entry.setStrand(fields[6].charAt(0));
                    }
                }

                cursor_ = entry;
            }
            else
            {
                cursor_ = null;
            }

            return this.cursor_ != null;
        }

        @Override
        public BedEntry next()
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
    protected void finalize() throws Throwable
    {
        super.finalize();
        try
        {
            if (reader_ != null)
            {
                this.reader_.close();
            }
        }
        catch (IOException ignore)
        {
            LOG.log(Level.FINEST, ignore.getMessage(), ignore);
        }
    }
}
