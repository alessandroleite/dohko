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
package org.excalibur.aqmp.handler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import org.excalibur.bio.sequencing.SequencePair;
import org.excalibur.bio.sequencing.tasks.LocalPairsScoreCallable;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.util.concurrent.Futures2;
import org.excalibur.service.manager.NodeManagerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningExecutorService;

@Component("taskHandler")
public class SequenceScorePairHandler
{
    public void handle(SequencePair pair)
    {
        this.handle(Collections.singletonList(pair));
    }

    @SuppressWarnings("unchecked")
    public void handle(List<SequencePair> pairs)
    {
        NodeManagerFactory.getManagerReference();
        
        ListeningExecutorService executor = DynamicExecutors.newListeningDynamicScalingThreadPool("score-pairs-%d", pairs.size());

        List<Callable<double[]>> tasks = Lists.transform(pairs, new Function<SequencePair, Callable<double[]>>()
        {
            @Override
            @Nullable
            public Callable<double[]> apply(@Nullable SequencePair input)
            {
                return new LocalPairsScoreCallable(input);
            }
        });

        List<double[]> scores = Futures2.invokeAllAndShutdownWhenFinish(tasks, executor, new FutureCallback[0]);
        System.out.println(scores.size() == pairs.size());
    }
}
