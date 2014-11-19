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
package org.excalibur.core;

import java.util.Iterator;

public final class ArrayIterator<T> implements Iterator<T>
{
    /**
     * The array to be iterate.
     */
    private final T[] array_;

    /**
     * The cursor position.
     */
    private volatile int currentIndex_;

    /**
     * 
     * @param array
     *            The array to be iterate.
     */
    public ArrayIterator(T[] array)
    {
        this.array_ = array;
    }

    @Override
    public boolean hasNext()
    {
        return array_ != null && currentIndex_ < array_.length;
    }

    @Override
    public T next()
    {
        return array_[currentIndex_++];
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
