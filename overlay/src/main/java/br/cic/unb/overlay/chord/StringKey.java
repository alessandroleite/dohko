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
package br.cic.unb.overlay.chord;

import com.google.common.base.Preconditions;

import br.cic.unb.overlay.Key;

public class StringKey implements Key, Cloneable
{
    private final String value;

    public StringKey(String value)
    {
        this.value = Preconditions.checkNotNull(value);
    }

    @Override
    public byte[] getBytes()
    {
        return this.value.getBytes();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return Boolean.TRUE;
        }
        
        if (!(obj instanceof StringKey))
        {
            return Boolean.FALSE;
        }
        
        return this.value.equals(((StringKey) obj).value);
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return "StringKey[value = " + this.value + "]";
    }
    
    @Override
    public StringKey clone()
    {
        StringKey clone;
        try
        {
            clone = (StringKey) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            clone = new StringKey(this.value);
        }
        
        return clone;
    }
    
    /**
     * @return
     */
    public final String getValue()
    {
        return value;
    }
}
