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
package com.joy.taglibs.action;

import com.joy.C;
import com.joy.mvc.Action;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionInputTextAreaTag extends SimpleTagSupport {
    private int cols;
    private int rows;
    private String cssClass;
    private String cssId;
    
    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
    private String name;

    public ActionInputTextAreaTag() {
        cssClass="";
        cssId="";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCSSClass() {
        return cssClass;
    }

    public void setCSSClass(String CSSClass) {
        this.cssClass = CSSClass;
    }

    public String getCSSId() {
        return cssId;
    }

    public void setCSSId(String CSSId) {
        this.cssId = CSSId;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        JspContext jsp = this.getJspContext();
        JspWriter out =jsp.getOut();
        String retText = "";
        
        // Get the Action object
        Action actionform = (Action)jsp.findAttribute(C.ACTION_FORM_BEAN);
        
        if (actionform != null) {
            String value = "";
            retText = "<TEXTAREA ";
            retText += " name='" + this.name + "' "; 
            if (this.cols > 0)
                retText += " cols='" + String.valueOf(this.cols) + "' ";
            if (this.rows > 0)
                retText += " rows='" + String.valueOf(this.rows) + "' ";
            try {
                value = actionform.getFormSingleEntry(this.name).getStrValue();
            } catch (Exception e) {}
            if (!cssClass.isEmpty())
                retText += " class='" + cssClass + "' "; 
            if (!cssId.isEmpty())
                retText += " id='" + cssId + "' "; 
            retText += ">" + value + "</TEXTAREA>"; 
        }
        out.println(retText);
    }

}
