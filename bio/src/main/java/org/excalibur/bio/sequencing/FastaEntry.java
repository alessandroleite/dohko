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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class FastaEntry implements Serializable
{
    /**
     * The sequence header id.
     */
    public static final char SEQUENCE_HEADER = '>';

    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 337956254588590482L;

    /**
     * Header of FASTA file.
     */
    private final String header_;

    /**
     * Bio sequence of FASTA file.
     */
    private final String sequence_;

    /**
     * 
     * @param header
     *            The sequence's header (name).
     * @param sequence
     *            The sequence's value.
     */
    public FastaEntry(String header, String sequence)
    {
        this.header_ = Preconditions.checkNotNull(header);
        this.sequence_ = Preconditions.checkNotNull(sequence);
    }

    /**
     * Factory method to create a new instance of this class using the header and the sequence value.
     * 
     * @param header
     *            The sequence's header. Might not be <code>null</code>.
     * @param sequence
     *            The sequence's value. Might not be <code>null</code>.
     * @return A new {@link FastaEntry} instance.
     */
    public static FastaEntry valueOf(String header, String sequence)
    {
        return new FastaEntry(header, sequence);
    }

    /**
     * Factory method to create a new instance of this class using lines. The first line must be the header and others the sequence's value.
     * 
     * @param lines
     *            Header and value of the sequence.
     * @return A new {@link FastaEntry} instance.
     */
    public static FastaEntry valueOf(List<String> lines)
    {
        Preconditions.checkArgument(lines != null && lines.size() >= 2);

        String header = lines.get(0).substring(1);
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < lines.size(); i++)
        {
            String line = lines.get(i).trim();
            Preconditions.checkArgument(line.charAt(0) != SEQUENCE_HEADER);

            sb.append(line);
        }

        return FastaEntry.valueOf(header, sb.toString());
    }

    /**
     * Factory method to create a new instance of this class using lines. The first line must be the header and others the sequence's value.
     * 
     * @param lines
     *            Header and value of the FASTA.
     * @return A new {@link FastaEntry} with the header and value assigned.
     */
    public static FastaEntry valueOf(String[] lines)
    {
        return valueOf(Arrays.asList(lines));
    }

    /**
     * @return the header
     */
    public String getHeader()
    {
        return header_;
    }

    /**
     * @return the sequence
     */
    public String getSequence()
    {
        return sequence_;
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString()
    {
        return String.format(">%s\n%s\n", this.getHeader(), this.getSequence());
    }
}
