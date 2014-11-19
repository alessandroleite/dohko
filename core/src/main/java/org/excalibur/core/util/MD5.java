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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class MD5
{
    /**
     * Message digest for calculating hash values.
     */
    private final MessageDigest messageDigest;
    
    private static final MD5 INSTANCE = new MD5();
    
    private MD5()
    {
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public String hash(String message)
    {
        Preconditions.checkState(!Strings.isNullOrEmpty(message));
        
        synchronized (this.messageDigest)
        {
            this.messageDigest.reset();
            this.messageDigest.update(message.getBytes());
            return new String(this.messageDigest.digest());
        }
    }
    
    public static final MD5 getInstance()
    {
        return INSTANCE;
    }
    
    public static final MD5 instance()
    {
        return getInstance();
    }
    
    public static String md5Hash(String message)
    {
        return getInstance().hash(message);
    }
}
