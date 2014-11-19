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

import java.io.Serializable;

/**
 * This class represents a <href="http://genome.ucsc.edu/FAQ/FAQformat.html#format1">BED format</a>.
 */
public class BedEntry implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = 6124028536139545645L;

    /**
     * The name of the chromosome (e.g. chr3, chrY, chr2_random) or scaffold (e.g. scaffold10671).
     */
    private String chromosome_;

    /**
     * The starting position of the feature in the chromosome or scaffold. The first base in a chromosome is numbered 0.
     */
    private long start_;

    /**
     * The ending position of the feature in the chromosome or scaffold. The chromEnd base is not included in the display of the feature. For example,
     * the first 100 bases of a chromosome are defined as chromStart=0, chromEnd=100, and span the bases numbered 0-99.
     */
    private long end_;

    /**
     * Defines the name of the BED line. This label is displayed to the left of the BED line in the Genome Browser window when the track is open to
     * full display mode or directly to the left of the item in pack mode.
     */
    private String name_;

    /**
     * A score between 0 and 1000. If the track line useScore attribute is set to 1 for this annotation data set, the score value will determine the
     * level of gray in which this feature is displayed (higher numbers = darker gray).
     */
    private int score_;

    /**
     * Defines the strand - either '+' or '-'.
     */
    private char strand_;

    /**
     * Creates a new {@link BedEntry}.
     */
    public BedEntry()
    {
        super();
    }

    /**
     * Creates a new {@link BedEntry}.
     * 
     * @param chromosome
     *            The name of the chromosome. Might not be <code>null</code>.
     * @param start
     *            The starting position of the feature in the chromosome or scaffold. The first base in a chromosome is numbered 0.
     * @param end
     *            The ending position of the feature in the chromosome or scaffold. The chromEnd base is not included in the display of the feature.
     */
    public BedEntry(String chromosome, long start, long end)
    {
        this.chromosome_ = chromosome;
        this.start_ = start;
        this.end_ = end;
    }

    /**
     * @return the chromosome
     */
    public String getChromosome()
    {
        return chromosome_;
    }

    /**
     * @param chromosome
     *            the chromosome to set
     */
    public void setChromosome(String chromosome)
    {
        this.chromosome_ = chromosome;
    }

    /**
     * @return the start
     */
    public long getStart()
    {
        return start_;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(long start)
    {
        this.start_ = start;
    }

    /**
     * @return the end
     */
    public long getEnd()
    {
        return end_;
    }

    /**
     * @param end
     *            the end to set
     */
    public void setEnd(long end)
    {
        this.end_ = end;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name_ = name;
    }

    /**
     * @return the score
     */
    public int getScore()
    {
        return score_;
    }

    /**
     * @param score
     *            the score to set
     */
    public void setScore(int score)
    {
        this.score_ = score;
    }

    /**
     * @return the strand
     */
    public char getStrand()
    {
        return strand_;
    }

    /**
     * @param strand
     *            the strand to set
     */
    public void setStrand(char strand)
    {
        this.strand_ = strand;
    }
}
