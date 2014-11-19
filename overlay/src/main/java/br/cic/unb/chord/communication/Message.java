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

/**
 * This class represents a message sent over socket protocol supported by {@link Endpoint} and {@link Proxy}.
 **/
public abstract class Message implements Serializable
{
    private static final long serialVersionUID = 4275429900527922826L;
    /**
     * Time stamp of this message.
     */
    private final long timeStamp;

    /**
     * Constructs a message with time stamp of current system time.
     */
    protected Message()
    {
        this.timeStamp = System.currentTimeMillis();
    }

    /**
     * @return Returns the timeStamp.
     */
    public final long getTimeStamp()
    {
        return this.timeStamp;
    }

    /**
     * Overwritten from {@link java.lang.Object}.
     * 
     * @return String representation of this.
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[Message@");
        buffer.append(this.hashCode());
        buffer.append(" from time ");
        buffer.append(this.timeStamp);
        buffer.append("]");
        return buffer.toString();
    }
}
