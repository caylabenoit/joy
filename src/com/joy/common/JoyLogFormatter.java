/*
 * Copyright (C) 2017 Benoit Cayla (benoit@famillecayla.fr)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joy.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyLogFormatter extends Formatter {
    private static final DateFormat LOG_DF = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
    
    /** 
     * Joy Log specific format
     * @param record log record
     * @return formatted log record
     */
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(2000);
        builder.append("{").append(record.getThreadID()).append("} ");
        builder.append(LOG_DF.format(new Date(record.getMillis()))).append(" - ");
        builder.append(record.getLevel()).append(" - ");
        builder.append(record.getSourceClassName()).append(">");
        builder.append(record.getSourceMethodName()).append(" - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }
    
    
}
