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
package org.excalibur.core.execution.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntryImpl implements LogEntry
{
    /**
     * Serial code version <code>serialVersionUID</code> for serialization.
     */
    private static final long serialVersionUID = -5009207787093037662L;

    public static final SimpleDateFormat DEFAULT_EN_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy/MM/DD HH:mm:ss");

    private Date date_;
    private String log_;

    public LogEntryImpl(String log)
    {
        this(new Date(), log);
    }

    public LogEntryImpl(Date time, String log)
    {
        this.date_ = time;
        this.log_ = log;
    }

    @Override
    public Date getDate()
    {
        return date_;
    }

    @Override
    public String getFormattedDate()
    {
        return DEFAULT_EN_TIMESTAMP_FORMAT.format(date_);
    }

    @Override
    public String getLog()
    {
        return log_;
    }
}
