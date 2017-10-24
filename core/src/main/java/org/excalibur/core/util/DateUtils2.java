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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.*;

public final class DateUtils2
{
    
    private DateUtils2()
    {
        throw new UnsupportedOperationException();
    }
    
    public static boolean equal(Date date, Date other)
    {
        Calendar c1 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        c1.setTime(date);
        
        Calendar c2 = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        c2.setTime(other);
        
        return c1.getTimeInMillis() == c2.getTimeInMillis();
    }
    
    public static Date toUTC(Date date)
    {
        checkNotNull(date, "Date to convert to UTC might not be null");
        return toUTC(date.getTime());
    }
    
    public static Date toUTC(long millis)
    {
    	Calendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    	c.setTimeInMillis(millis);
    	return c.getTime();
    }

    public static long seconds(long timeInMillis)
    {
        return TimeUnit.MILLISECONDS.toSeconds(timeInMillis);
    }
}
