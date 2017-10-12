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
package org.excalibur.fm.solver.constraints;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.MoreObjects;

import solver.Solver;
import solver.variables.IntVar;
import solver.variables.VF;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;

public class DomainVar
{
    private final Object lock_ = new Integer(1);

    private final String name_;
    private final Solver solver_;

    private int[] domain_ = new int[0];
    private IntVar var;

    public DomainVar(String name, int length, Solver solver)
    {
        checkState(!isNullOrEmpty(name));
        this.name_ = name;
        this.solver_ = checkNotNull(solver);

        this.domain_ = new int[length];
    }

    public DomainVar(String name, int[] domain, Solver solver)
    {
        this(name, domain.length, solver);
        this.domain_ = domain;
    }

    /**
     * @return the domain
     */
    public int[] getDomainValues()
    {
        return domain_;
    }

    public DomainVar setDomainValueAt(int index, Integer value)
    {
        checkPositionIndex(index, domain_.length);

        synchronized (lock_)
        {
            this.domain_[index] = value;
            this.var = null;
        }

        return this;
    }

    public Integer getDomainValueAt(int index)
    {
        checkPositionIndex(index, domain_.length);
        return this.domain_[index];
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name_;
    }

    public IntVar getVar()
    {
        synchronized (lock_)
        {
            if (var == null)
            {
                this.var = VF.enumerated(name_, this.domain_, solver_);
            }
        }

        return var;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
        		.add("name", getName())
        		.add("domain", ArrayUtils.toString(domain_))
        		.omitNullValues()
        		.toString();
    }
}
