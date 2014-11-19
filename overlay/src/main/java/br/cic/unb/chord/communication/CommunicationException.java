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

/**
 * Checked exception to inform about error of communication between the nodes in network.
 * 
 */
public class CommunicationException extends Exception
{
    private static final long serialVersionUID = -8566177877528393598L;

    public CommunicationException()
    {
        super();
    }

    /**
     * @param message
     *            A message describing this exception.
     */
    public CommunicationException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     *            The Throwable that caused this Exception.
     */
    public CommunicationException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     *            A message describing this exception.
     * @param cause
     *            The Throwable that caused this Exception.
     */
    public CommunicationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
