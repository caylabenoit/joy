/*
 * Copyright (C) 2016 Benoit CAYLA (benoit@famillecayla.fr)
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
package com.joy.log;

import com.joy.C;
import com.joy.Joy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyLogDetails {
    private String filename;
    private String folder;
    private boolean prefixDate;
    
    
    public JoyLogDetails() {
        filename="";
        folder="";
        prefixDate=false;
    }

    public String getBasicFile() {
        return filename;
    }
    
    public String getFile() {
        return getFilename();
    }
    
    public void setPrefixDate(boolean PrefixDate) {
        this.prefixDate = PrefixDate;
    }
    
    public void setDirectory(String Directory) {
        this.folder = Directory;
    }
    
    public String getCompleteFilename() {
        return getDirectory() + "/" + getFilename();
    }

    public void setFilename(String Filename) {
        this.filename = Filename;
    }
    
    private String getDirectory() {
        return (folder.isEmpty() ? Joy.parameters().getApplicationFolder() + "/" + C.LOG_DIR_FROM_APPDIR : folder);
    }
    
    private String getFilename() {
        if (prefixDate) { 
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_");
            return dateFormat.format(cal.getTime()) + filename;
        } else
            return filename;
    }
}
