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
package org.excalibur.core.domain;

public class UserNotFoundException extends RuntimeException
{
    /**
     * Serial code version <code></code> for serialization.
     */
    private static final long serialVersionUID = 5195813203683409633L;
    
    private final Integer userId_;
    private final String username_;

    public UserNotFoundException(Integer userId, String username, String message)
    {
        super(message);
        this.userId_ = userId;
        this.username_ = username;
    }
    
    public UserNotFoundException(String username, String message)
    {
        this(0, username, message);
    }

    /**
     * @return the userId
     */
    public Integer getUserId()
    {
        return userId_;
    }
    
    public String getUsername()
    {
        return username_;
    }
}
