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
package org.excalibur.core.cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.sshd.common.Cipher.Mode;
import org.apache.sshd.common.cipher.TripleDESCBC;
import org.excalibur.core.util.AnyThrow;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;


public class TripleDESUtils
{
    public static final String APP_DEFAULT_KEY = System.getProperty("org.excalibur.cipher.key", "94ad6603a83307e2993aeac8fd97014b");
    
    public static String cipher(String message)
    {
        try
        {
            return cipher(APP_DEFAULT_KEY, message);
        }
        catch (Exception e)
        {
            AnyThrow.throwUncheked(e);
        }
        
        throw new RuntimeException("This should never happen!");
    }
    
    
    public static String cipher(String key, String message) throws Exception
    {
        checkState(!isNullOrEmpty(message));
        checkState(!isNullOrEmpty(key));
        
        byte[] data = message.getBytes(); 
        update(Mode.Encrypt, key, data);
        return new String(Base64.encodeBase64(data));
    }
    
    public static String decrypt(String key, String message) throws Exception
    {
        checkState(!isNullOrEmpty(message));
        checkState(!isNullOrEmpty(key));
        
        byte [] data = Base64.decodeBase64(message.getBytes());
        update(Mode.Decrypt, key, data);
        return new String(data);
    }
    
    public static String decrypt(String message)
    {
        if (!isNullOrEmpty(message))
        {
            try
            {
                return decrypt(APP_DEFAULT_KEY, message);
            }
            catch (Exception e)
            {
                AnyThrow.throwUncheked(e);
            }
            throw new RuntimeException("This should never happen!");
        }
        return message;
    }
    
    public static byte[] update(Mode mode, String key, byte[] data) throws Exception
    {
        checkState(!isNullOrEmpty(key));
        checkNotNull(data);
        
        TripleDESCBC des = new TripleDESCBC();
        des.init(mode, key.getBytes(), key.getBytes());
        des.update(data, 0, data.length);
        
        return data;
    }
}
