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
package org.excalibur.bio.tools.infernal;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.common.base.Preconditions;

public final class HitScore implements Serializable
{
	/**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
	private static final long serialVersionUID = -4673781338293860856L;


    public static final class Sequence implements Serializable
    {
        /**
         * Serial code version <code>serialVersionUID</code> for serialization.
         */
		private static final long serialVersionUID = -728044711390829989L;

		/**
         * The sequence's name.
         */
        private final String name_;

        /**
         * The position where start the alignment. Must be greater than -1.
         */
        private final int start_;

        /**
         * The position where finish the alignment. Must be greater than {@link #getStart()}.
         */
        private final int end_;

        public Sequence(String name, int start, int end)
        {
            this.name_ = name;
            Preconditions.checkArgument(start >= 0);
            this.start_ = start;
            Preconditions.checkArgument(end > start);
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
         * @return the start
         */
        public int getStart()
        {
            return start_;
        }

        /**
         * @return the end
         */
        public int getEnd()
        {
            return end_;
        }
    }

    private long rank_;
    private BigDecimal evalue_;
    private double score_;
    private double bias_;
    private Sequence sequence_;

    public HitScore withRank(long rank)
    {
        this.rank_ = rank;
        return this;
    }

    public HitScore withEvalue(BigDecimal value)
    {
        this.evalue_ = value;
        return this;
    }

    public HitScore withScore(double score)
    {
        this.score_ = score;
        return this;
    }

    public HitScore withBias(double bias)
    {
        this.bias_ = bias;
        return this;
    }

    public HitScore withSequence(Sequence sequence)
    {
        this.sequence_ = sequence;
        return this;
    }

    /**
     * @return the rank
     */
    public long getRank()
    {
        return rank_;
    }

    /**
     * @return the evalue
     */
    public BigDecimal getEvalue()
    {
        return evalue_;
    }

    /**
     * @return the score
     */
    public double getScore()
    {
        return score_;
    }

    /**
     * @return the bias
     */
    public double getBias()
    {
        return bias_;
    }

    /**
     * @return the sequence
     */
    public Sequence getSequence()
    {
        return sequence_;
    }
}
