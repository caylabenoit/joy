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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class DashboardBloc {
    private String title;
    private String id;
    private String blocType;
    private String size;
    private List<DashboardBlocParameter> parameters;
    private DashboardTemplate template;

    public void setTemplate(DashboardTemplate Template) {
        this.template = Template;
    }
    
    public void addParameter(DashboardBlocParameter param) {
        parameters.add(param);
    }
    
    public String getSize() {
        return size;
    }

    public void setSize(String Size) {
        this.size = Size;
    }

    public String getBlocType() {
        return blocType;
    }

    public void setBlocType(String BlocType) {
        this.blocType = BlocType;
    }
    
    public DashboardBloc() {
        title = "";
        id = "";
        size = "";
        this.blocType = "";
        parameters = new ArrayList<>();
    }

    public String getID() {
        return id;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    public String getJSContent() {
        return getContent(template.getAfterBodyContent());
    }
    
    public String getHTMLContent() {
        return getContent(template.getBodyContent());
    }
    
    /**
     * Return the bloc content by using templates & parameters
 Replace the parameters between [] into the template by the values 
 replace also the predefined tags [id], [size], [title] and [type]
 (be careful the \n are removed)
     * @return Content
     */
    private String getContent(String myContent) {
        for (DashboardBlocParameter param : parameters) {
            myContent = myContent.replace("[" + param.getName() + "]", param.getValue());
        }
        myContent = myContent.replace("[ID]", this.getID());
        myContent = myContent.replace("[type]", this.getBlocType());
        myContent = myContent.replace("[size]", this.getSize());
        myContent = myContent.replace("[title]", this.getTitle());
        
        return myContent;
    }
}
