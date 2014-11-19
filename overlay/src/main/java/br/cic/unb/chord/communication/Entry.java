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
package br.cic.unb.chord.communication;

import java.io.Serializable;

import com.google.common.base.Objects;

import br.cic.unb.chord.data.ID;

/**
 * Represents some object stored in a {@link Node}.
 */
public final class Entry implements Serializable
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -4601493757973145941L;

    /**
     * The id of this entry.
     */
    private ID id;

    /**
     * The stored value.
     * 
     */
    private Serializable value;

    /**
     * @param id
     * @param value1
     */
    public Entry(ID id, Serializable value1)
    {
        this.id = id;
        this.value = value1;
    }

    /**
     * @return Returns the id.
     */
    public ID getId()
    {
        return this.id;
    }

    /**
     * @return Returns the value.
     */
    public Serializable getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return "( key = " + this.id.toString() + ", value = " + this.value + ")";
    }

    @Override
    public int hashCode()
    {
        int result = 17;
        result += 37 * this.id.hashCode();
        result += 37 * this.value.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return Boolean.TRUE;
        }

        if (!(obj instanceof Entry))
        {
            return Boolean.FALSE;
        }

        Entry entry = (Entry) obj;

        return Objects.equal(this.getId(), entry.getId()) && Objects.equal(this.getValue(), entry.getValue());
    }
}
