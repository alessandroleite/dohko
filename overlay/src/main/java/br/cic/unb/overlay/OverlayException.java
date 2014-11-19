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
package br.cic.unb.overlay;

/**
 * Whenever this exception is thrown, an error has occurred which cannot be resolved by the service layer.
 */
public final class OverlayException extends Exception
{

    private static final long serialVersionUID = 1039630030458301201L;

    /**
     * Creates a new service exception with the given description.
     * 
     * @param message
     *            Description for the user.
     */
    public OverlayException(String message)
    {
        super(message);
    }

    /**
     * Creates a new service exception with the given description.
     * 
     * @param message
     *            Description for the user.
     * @param cause
     *            Throwable which led to throwing this exception.
     */
    public OverlayException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
