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
package org.excalibur.bio.tools.infernal.parser;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;

import org.excalibur.bio.tools.infernal.HitScore;
import org.excalibur.bio.tools.infernal.HitScore.Sequence;
import org.excalibur.bio.tools.infernal.Output;
import org.excalibur.core.util.concurrent.DynamicExecutors;
import org.excalibur.core.util.concurrent.Futures2;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Parser for tabular output format (option: <em>--tblout</em>) of <em>cmsearch</em>. <quote>The target hits table consists of one line for each
 * different query/target comparison that met the reporting thresholds, ranked by decreasing statistical significance (increasing E-value). Each line
 * consists of 18 space-delimited fields followed by a free text target sequence description. </quote> <a
 * href="ftp://selab.janelia.org/pub/software/infernal/Userguide.pdf">Tabular output formats</a>, page 63.
 * 
 */
public class Tabular
{
    @SuppressWarnings("unchecked")
    public Output parser(final List<String> lines, FutureCallback<HitScore> callback)
    {
        if (lines == null || lines.isEmpty())
        {
            return new Output();
        }

        final List<String> hits = lines.subList(2, lines.size());

        String[] tokens = hits.get(0).split("\\s+");
        final Output query = new Output(tokens[2], tokens[3]);

        ListeningExecutorService executor = DynamicExecutors.newListeningDynamicScalingThreadPool("tabular-infernal-output-parser-%d");
        List<Callable<HitScore>> tasks = Lists.newArrayList();

        for (final String line : hits)
        {
            tasks.add(new Callable<HitScore>()
            {
                @Override
                public HitScore call() throws Exception
                {
                    String[] tokens = line.split("\\s+");
                    return new HitScore()
                            .withBias(Double.parseDouble(tokens[13]))
                            .withEvalue(new BigDecimal(tokens[15]))
                            .withScore(Double.valueOf(tokens[14]))
                            .withSequence(new Sequence(tokens[0], Integer.parseInt(tokens[7]), Integer.parseInt(tokens[8])));
                }
            });
        }
        
//        FutureCallbackList<HitScore> f = new FutureCallbackList<HitScore>();
        FutureCallback<HitScore>[] callbacks = new FutureCallback[]{callback/*, f*/};
        
        List<HitScore> hitScores = Futures2.invokeAllAndShutdownWhenFinish(tasks, executor, callbacks);
        query.addHitScores(hitScores);

        return query;
    }

    public Output parser(final List<String> lines)
    {
        return parser(lines, null);
    }
}
