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
package org.excalibur.bio.sequencing.tasks;


import java.util.List;
import java.util.concurrent.Callable;

import org.biojava3.alignment.Alignments;
import org.biojava3.alignment.Alignments.PairwiseSequenceScorerType;
import org.biojava3.alignment.SimpleGapPenalty;
import org.biojava3.core.sequence.ProteinSequence;
import org.excalibur.bio.sequencing.SequencePair;

import com.google.common.collect.Lists;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import static org.biojava3.alignment.SubstitutionMatrixHelper.getBlosum65;


public class LocalPairsScoreCallable implements Callable<double[]>
{
    public final SequencePair pair_;

    public LocalPairsScoreCallable(SequencePair pair)
    {
        checkNotNull(pair);
        checkNotNull(pair.getQuery());
        checkNotNull(pair.getTarget());

        checkState(!isNullOrEmpty(pair.getQuery().getValue()));
        checkState(!isNullOrEmpty(pair.getTarget().getValue()));

        this.pair_ = pair;
    }

    @Override
    public double[] call() throws Exception
    {
        List<ProteinSequence> sequences = Lists.newArrayList(new ProteinSequence(pair_.getQuery().getValue()), 
                                                             new ProteinSequence(pair_.getTarget().getValue()));
        
        return Alignments.getAllPairsScores(sequences, PairwiseSequenceScorerType.LOCAL, new SimpleGapPenalty(), getBlosum65());
    }
}
