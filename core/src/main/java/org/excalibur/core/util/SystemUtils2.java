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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.io.Files.createParentDirs;
import static com.google.common.io.Files.write;
import static org.excalibur.core.cipher.TripleDESUtils.decrypt;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.commons.lang.SystemUtils;
import org.excalibur.core.domain.UserKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SystemUtils2
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtils2.class.getName());

    public static final String JAVA_HOME = System.getenv("JAVA_HOME");

    public static final String APPLICATION_DATA_DIR_PROPERTY_KEY = "org.excalibur.application.data.dir";

    private static final String DEFAULT_EXCALIBUR_DATA_DIR_KEY = "/.excalibur";

    private SystemUtils2()
    {
        throw new UnsupportedOperationException();
    }

    public static Integer getIntegerProperty(String name, Integer defaultValue)
    {
        String value = getProperty(name, defaultValue != null ? defaultValue.toString(): null);
        Integer result = defaultValue;

        if (!isNullOrEmpty(value))
        {
            try
            {
                result = Integer.parseInt(value);
            }
            catch (NumberFormatException nfe)
            {
                LOGGER.error("Value [{}] is not an Integer value {} for property [{}]!", value, name, nfe.getMessage(), nfe);
            }
        }
        return result;
    }
    
    public static Integer getIntegerProperty(String name)
    {
        return getIntegerProperty(name, null);
    }
    
    public static Long getLongProperty(String name, Long defaultValue)
    {
    	String value = getProperty(name, defaultValue == null ? null : defaultValue.toString());
    	Long result = defaultValue;
    	
    	
    	if (!isNullOrEmpty(value))
    	{
    		try
    		{
    			result = Long.parseLong(value);
    		}
    		catch(NumberFormatException nfe)
    		{
    			LOGGER.error("Value [{}] is not a Long value {} for property [{}]!", value, name, nfe.getMessage(), nfe);
    		}    		
    	}
    	return result;
    }

    public static String getProperty(String name, String defaultValue)
    {
        return System.getProperty(name, defaultValue);
    }
    
    public static String getProperty(String name)
    {
        return getProperty(name, null);
    }

    public static File getPropertyFile(String name, String defaultPath)
    {
        String file = getProperty(name, defaultPath);
        checkState(!isNullOrEmpty(file));

        return new File(file);
    }

    public static boolean getBooleanProperty(String propertyName, boolean defaultValue)
    {
        String value = getProperty(propertyName, String.valueOf(defaultValue));
        boolean result = defaultValue;

        if (!isNullOrEmpty(value))
        {
            result = Boolean.parseBoolean(value);
        }

        return result;
    }

    /**
     * Returns a {@link File} representing the user's home directory.
     * 
     * @return a {@link File} representing the user's home directory.
     */
    public static File getUserDirectory()
    {
        return new File(getUserDirectoryPath());
    }

    /**
     * Returns a {@link String} representing the user's home path.
     * 
     * @return a {@link String} representing the user's home path.
     */
    public static String getUserDirectoryPath()
    {
        return System.getProperty("user.home");
    }

    /**
     * <p>
     * Returns a {@link File} representing the default application's data directory.
     * 
     * <p>
     * <strong>Notice:</strong>The default directory is ~/.excalibur. However, it can be redefined through the system property:
     * <em>org.excalibur.application.data.dir</em>.
     * 
     * @return a {@link File} representing the default application's data directory.
     */
    public static File getApplicationDataDir()
    {
        return new File(getApplicationDataPath());
    }

    /**
     * <p>
     * Returns a {@link String} representing the default application's data path.
     * 
     * <p>
     * <strong>Notice:</strong>The default directory is ~/.excalibur. However, it can be redefined through the system property:
     * <em>org.excalibur.application.data.dir</em>.
     * 
     * @return a {@link String} representing the default application's data directory.
     */
    public static String getApplicationDataPath()
    {
        return System.getProperty(APPLICATION_DATA_DIR_PROPERTY_KEY,
                String.format("%s%s", SystemUtils.getUserHome().getAbsolutePath(), DEFAULT_EXCALIBUR_DATA_DIR_KEY));
    }
    
    public static String getUserSshKeyDirFor(UserKey key)
    {
        checkNotNull(key);
        checkNotNull(key.getUser());
        checkState(!isNullOrEmpty(key.getUser().getUsername()));
        
        return String.format("%s/.%s/.ssh", getApplicationDataPath(), key.getUser().getUsername());
    }
    
    public static File writeUserkey(UserKey key) throws IOException
    {
        checkNotNull(key);
        checkNotNull(key.getUser());
        checkState(!isNullOrEmpty(key.getName()));
        checkState(!isNullOrEmpty(key.getUser().getUsername()));
        checkState(!isNullOrEmpty(key.getPrivateKeyMaterial()));
                   
        File sshKeyFile = new File(String.format("%s%s.pem", addLastForwardSlash(getUserSshKeyDirFor(key)), key.getName()));
        
//        Files.setPosixFilePermissions(sshKeyFile.toPath(), new HashSet<PosixFilePermission>());
        
        createParentDirs(sshKeyFile);
        write(decrypt(key.getPrivateKeyMaterial()).getBytes(), sshKeyFile);
        
//        chmod(sshKeyFile, 600);
//        chmod(sshKeyFile.getParent(), 600);
        
        return sshKeyFile;
    }
    
    public static File writeUserSshKey(UserKey key) throws IOException
    {
        checkNotNull(key);
        checkNotNull(key.getUser());
        checkState(!isNullOrEmpty(key.getName()));
        checkState(!isNullOrEmpty(key.getUser().getUsername()));
        checkState(!isNullOrEmpty(key.getPublicKeyMaterial()));
        
        File userPublicKey = new File(String.format("%s%s_rsa.pub", addLastForwardSlash(getUserSshKeyDirFor(key)), key.getName()));
        createParentDirs(userPublicKey);
        
        write(decrypt(key.getPublicKeyMaterial()).getBytes(), userPublicKey);
        
        chmod(userPublicKey, 600);
        chmod(userPublicKey.getParent(), 755);
        
        return userPublicKey;
    }

    public static String addLastForwardSlash(String name)
    {
        return name == null || name.trim().isEmpty() ? name : name.charAt(name.length() - 1) == '/' ? name : name.concat("/");
    }

    /**
     * Creates the application's data directory if it does not exist.
     * 
     * @return <code>true</code> if the directory was created or <code>false</code> otherwise.
     * @see #gettApplicationDataDirectory()
     */
    public static boolean createApplicationDataDir()
    {
        File file = getApplicationDataDir();

        if (!file.exists())
        {
            return file.mkdirs();
        }

        return true;
    }
    
    public static int chmod(File file, int mode)
    {
        return chmod(file.getName(), mode);
    }
    
    public static int chmod(String filename, int mode)
    {
        try
        {
            Class<?> fspClass = Class.forName("java.util.prefs.FileSystemPreferences");
            Method chmodMethod = fspClass.getDeclaredMethod("chmod", String.class, Integer.TYPE);
            chmodMethod.setAccessible(true);
            
            return (Integer) chmodMethod.invoke(null, filename, mode);
        }
        catch (Throwable ex)
        {
            return -1;
        }
    }
}
