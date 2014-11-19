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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.io.CharStreams;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.excalibur.core.io.utils.IOUtils2.closeQuietly;

public final class Strings2
{
    /**
     * New line character.
     */
    public static final String NEW_LINE = "\n";

    /**
     * Tabular character.
     */
    public static final String TAB = "\t";

    /**
     * Return carry character.
     */
    public static final String RETURN = "\r";

    /**
     * Forward slash.
     */
    public static final String FORWARD_SLASH = "/";

    /**
     * 
     */
    public static final String RFC1035_REGEX_PATTERN = "(?=^.{1,63}$)[a-z]([-a-z0-9-]*)";

    /**
     * 
     */
    public static final String COMMA = ",";
    
    /**
     * 
     */
    public static final String SPACE = " ";

    private Strings2()
    {
        throw new UnsupportedOperationException();
    }

    public static String toStringAndClose(InputStream input) throws IOException
    {
        return toStringAndClose(input, UTF_8);
    }

    public static String toStringAndClose(InputStream input, Charset cs) throws IOException
    {
        checkNotNull(input, "input");
        checkNotNull(cs, "charset");

        InputStreamReader isr = new InputStreamReader(input, cs);

        try
        {
            return CharStreams.toString(isr);
        }
        finally
        {
            closeQuietly(isr, input);
        }
    }

    public static String nullAsEmpty(String value)
    {
        return value == null ? "" : value;
    }
    
    public static String ensureMaxSize(int maxSize, String value)
    {
        if (value == null)
        {
            return value;
        }
        
        return value.length() < maxSize ? value : value.substring(0, maxSize); 
    }
    
    public static String validRfc1025RandomUUID()
    {
        String uuid =  UUID.randomUUID().toString();
        uuid = ensureMaxSize(63, uuid.replaceAll("\\-", "").trim());
        return String.format("e%s", uuid); 
    }
}
