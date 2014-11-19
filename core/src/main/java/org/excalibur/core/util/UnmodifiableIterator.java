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
package org.excalibur.core.util;

import java.util.Iterator;

public class UnmodifiableIterator<E> implements Iterator<E>
{
    private final Iterator<E> adapteeIterator_; 
    
    public UnmodifiableIterator(Iterator<E> iterator)
    {
        this.adapteeIterator_ = iterator;
    }

    @Override
    public boolean hasNext()
    {
        return adapteeIterator_.hasNext();
    }

    @Override
    public E next()
    {
        return adapteeIterator_.next();
    }

    /**
     * Guaranteed to throw an exception and leave the underlying data unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Override
    @Deprecated
    public final void remove()
    {
        throw new UnsupportedOperationException();
    }

}
