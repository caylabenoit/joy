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
public class ActionRadioTag extends SimpleTagSupport {
    private String name;
    private String onclick;
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    
    public ActionRadioTag() {
        onclick="";
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        boolean myCheckedValue = false;
        String content = "";
        JspContext jsp = this.getJspContext();
        //Get the writer object for output.
        JspWriter out = jsp.getOut();
        Object val = "";
        
        try {
            // get the Action object
            Action actionform = (Action)jsp.findAttribute(C.ACTION_FORM_BEAN);
            val = actionform.getFormSingleEntry(this.name).getValue();
        } catch (Exception e) { val = ""; }
        
        if (!String.valueOf(val).isEmpty())
            myCheckedValue = (boolean)val;
        content = "<INPUT TYPE=RADIO ";
        content += " NAME='" + this.name + "' ";
        content += " ID='" + this.name + "' ";
        if (!this.onclick.isEmpty())
            content += " ONCLICK=\"" + this.onclick + "\" ";
        content += (myCheckedValue ? "checked='CHECKED'" : "");
        content += ">";
        if (!this.label.isEmpty())
            content += "<label for='" + this.name + "'>" + this.label + "</label>";

        out.print(content);
    }

}
