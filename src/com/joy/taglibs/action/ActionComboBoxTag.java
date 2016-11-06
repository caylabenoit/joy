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
import com.joy.Joy;
import com.joy.mvc.Action;
import com.joy.mvc.formbean.JoyFormSingleEntry;
import com.joy.mvc.formbean.JoyFormVectorEntry;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * 
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionComboBoxTag extends SimpleTagSupport {
    private String name;
    private int height;
    private String id;
    private String onchange; 
    private String cssClass;
    private String placeholder;

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getCSSClass() {
        return cssClass;
    }

    public void setCSSClass(String CSSClass) {
        this.cssClass = CSSClass;
    }
    
    public String getOnchange() {
        return onchange;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    public ActionComboBoxTag() {
        height = 0;
        id = "";
        onchange="";
        cssClass="";
        placeholder="";
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        try {
            JspContext jsp = this.getJspContext();
            Action actionform = (Action)jsp.findAttribute(C.ACTION_FORM_BEAN);
            JoyFormVectorEntry Vector = actionform.getFormVectorEntry(name);
            String sCombo = "";
            JspWriter out =jsp.getOut();

            if (!Vector.getVector().isEmpty()) {
                String myId = (id.isEmpty() ? name : id);
                sCombo += "<SELECT ";
                sCombo += " name=\"" + name + "\" ";
                if (!cssClass.isEmpty())
                    sCombo += " class='" + cssClass + "' "; 
                if (!this.onchange.isEmpty())
                    sCombo += " onchange=\"" + this.onchange + "\" ";
                if (!this.placeholder.isEmpty())
                    sCombo += " placeholder=\"" + this.placeholder + "\" ";                
                sCombo += " id='" + myId + "'";
                sCombo += ">";
                for (JoyFormSingleEntry entry : Vector.getVector()) {
                    sCombo += "<OPTION";
                    sCombo += " Value='" + entry.getID() + "'";
                    if ( (Vector.getSelected().equalsIgnoreCase(entry.getID())) || (Vector.getSelected().equalsIgnoreCase(entry.getStrValue())) ) {
                        sCombo += " SELECTED ";
                    }
                    sCombo += ">";
                    sCombo += entry.getValue();
                    sCombo += "</OPTION>";
                }
                sCombo += "</SELECT>";
                
                out.print(sCombo);
            }
        } catch (Exception e) {
            Joy.log().error ( e.toString());
        }
    }
}