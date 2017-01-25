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
import java.util.Objects;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionInputTextTag extends SimpleTagSupport {
    private boolean readonly;
    private int size;
    private String cssClass;
    private String cssId;
    private String name;
    private String required;
    private int maxlength;
    private String ariadescribedby;
    private String placeholder;
    private String glypheicon;  // Glyphe to display before the input box
    private String freetagasis;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.freetagasis);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActionInputTextTag other = (ActionInputTextTag) obj;
        return true;
    }

    public String getFreetagasis() {
        return freetagasis;
    }

    public void setFreetagasis(String freetagasis) {
        this.freetagasis = freetagasis;
    }
    
    
    
    public String getGlypheicon() {
        return glypheicon;
    }

    public void setGlypheicon(String glypheicon) {
        this.glypheicon = glypheicon;
    }
    
    public String getAriadescribedby() {
        return ariadescribedby;
    }

    public void setAriadescribedby(String ariadescribedby) {
        this.ariadescribedby = ariadescribedby;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public int getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
    }
    
    public ActionInputTextTag() {
        this.size = 0;
        this.readonly = false;
        this.cssClass="";
        this.cssId="";
        this.maxlength=0;
        this.required="no";
        this.ariadescribedby = "";
        this.placeholder = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
            if (!readonly) {
                String value = "";
                retText  = "<DIV class='form-group'>";
                if (this.glypheicon != null) {
                    retText += "<DIV class='input-group'>";
                    retText += "<SPAN class='input-group-addon'><SPAN class='glyphicon " + this.glypheicon + "' aria-hidden='true'></SPAN></SPAN>";
                }
                retText += "<INPUT type='text' ";
                if (this.freetagasis != null) {
                    retText += " " + this.freetagasis + " "; 
                }
                retText += " name='" + this.name + "' "; 
                if (this.size > 0)
                    retText += " size='" + String.valueOf(this.size) + "' ";
                try {
                    value = actionform.getFormSingleEntry(this.name).getStrValue();
                } catch (Exception e) {}
                retText += " value=\"" + value + "\" "; 
                if (!cssClass.isEmpty())
                    retText += " class='" + cssClass + "' "; 
                if (!cssId.isEmpty())
                    retText += " id='" + cssId + "' "; 
                if (required.equalsIgnoreCase("yes"))
                    retText += " required";
                if (maxlength != 0)
                    retText += " maxlength=" + String.valueOf(maxlength);
                if (!this.ariadescribedby.isEmpty())
                    retText += " aria-describedby='" + this.ariadescribedby + "' "; 
                if (!this.placeholder.isEmpty())
                    retText += " placeholder='" + this.placeholder + "' "; 
                retText += " />";
                if (this.glypheicon != null) 
                    retText += "</DIV>";
                retText += "<DIV class=\"help-block with-errors\"></DIV>";
                retText += "</DIV>";
                
            } else {
                retText += "<INPUT type='hidden' ";
                retText += " name='" + this.name + "' ";
                retText += " value='" + actionform.getFormSingleEntry(this.name).getStrValue() + "' />"; 
                retText += "<SPAN class='input_readonly'>" + actionform.getFormSingleEntry(this.name).getStrValue() + "</SPAN>";
            }
        }
        out.println(retText);
    }

}
