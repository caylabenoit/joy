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
package com.joy.dashboard;

import com.joy.Joy;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class DashboardTemplate {
    private String Name;
    private String bodyContent;
    private String afterBodyContent;

    public DashboardTemplate() {
        bodyContent="";
        afterBodyContent="";
        Name="";
    }
    
    public String getName() {
        return Name;
    }

    public String getBodyContent() {
        return bodyContent;
    }
    
    public String getAfterBodyContent() {
        return afterBodyContent;
    }
    
    public DashboardTemplate(String Name) {
        this.Name = Name;
    }
    
    public void setBodyContent(String TemplateFile) {
        try {
            if (!TemplateFile.isEmpty())
                this.bodyContent = Joy.readFileAsString(TemplateFile);
        } catch (Exception e) { 
            Joy.log().error(e);
            this.bodyContent = "";
        }
    }
    
    public void setAfterBodyContent(String TemplateFile) {
        try {
            if (!TemplateFile.isEmpty())
                this.afterBodyContent = Joy.readFileAsString(TemplateFile);
        } catch (Exception e) { 
            Joy.log().error(e);
            this.afterBodyContent = "";
        }
    }
}
