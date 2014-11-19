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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Output implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -6813139243423960265L;

    /**
     * The query name.
     */
    private String query_;

    /**
     * The query accession.
     */
    private String accession_;

    /**
     * The query description.
     */
    private String description_;

    /**
     * The score of a query.
     */
    private final List<HitScore> scores_ = new ArrayList<HitScore>();

    /**
     * Default constructor
     */
    public Output()
    {
        super();
    }

    /**
     * 
     * @param query
     *            The query name.
     * @param accession
     *            The query accession.
     * @param description
     *            The query description.
     */
    public Output(String query, String accession, String description)
    {
        this(query, accession);
        this.description_ = description;
    }

    /**
     * @param query
     *            The query's name. Might not be <code>null</code>.
     * @param accession
     *            The query's accession.
     */
    public Output(String query, String accession)
    {
        this.query_ = query;
        this.accession_ = accession;
    }

    public void addHitScore(HitScore score)
    {
        this.addHitScores(Collections.singletonList(score));
    }

    public void addHitScores(List<HitScore> scores)
    {
        synchronized (scores_)
        {
            for (HitScore hitScore : scores)
            {
                if (hitScore != null)
                {
                    this.scores_.add(hitScore);
                }
            }
        }
    }

    /**
     * @return the query
     */
    public String getQuery()
    {
        return query_;
    }

    /**
     * @return the accession_
     */
    public String getAccession()
    {
        return accession_;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description_;
    }

    /**
     * @return the scores
     */
    public synchronized List<HitScore> getScores()
    {
        return Collections.unmodifiableList(scores_);
    }

}
