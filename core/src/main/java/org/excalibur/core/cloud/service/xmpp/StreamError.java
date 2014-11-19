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
package org.excalibur.core.cloud.service.xmpp;

import java.io.Serializable;

public class StreamError implements Serializable
{
    private static final long serialVersionUID = -1388375478302380001L;
    
    private final String code_;
    
    public StreamError(String code)
    {
        this.code_ = code;
    }

    /**
     * @return the code
     */
    public String getCode()
    {
        return code_;
    }
}
