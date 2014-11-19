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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sequences")
public class Sequences implements Serializable, Iterable<Sequence>
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -188170622630572318L;
    
    @XmlElement(name = "sequence")
    private final List<Sequence> sequences_ = Lists.newCopyOnWriteArrayList();

    public Sequences()
    {
    }

    public Sequences addSequence(Sequence sequence)
    {
        if (sequence != null)
        {
            this.sequences_.add(sequence);
        }

        return this;
    }

    public List<Sequence> getSequences()
    {
        return Collections.unmodifiableList(sequences_);
    }

	@Override
	public Iterator<Sequence> iterator() 
	{
		return this.getSequences().iterator();
	}
}
