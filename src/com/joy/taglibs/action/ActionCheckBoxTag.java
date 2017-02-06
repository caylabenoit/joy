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
public class ActionCheckBoxTag extends SimpleTagSupport {
    private String name;
    private String onclick;

    public ActionCheckBoxTag() {
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
        
        try {
            // get the Action object
            Action actionform = (Action)jsp.findAttribute(C.ACTION_FORM_BEAN);
            Object val = actionform.getFormSingleEntry(this.name).getValue();

            if (val != null)
                myCheckedValue = (boolean)val;
            content = "<INPUT TYPE=CHECKBOX ";
            content += " NAME='" + this.name + "' ";
            if (!this.onclick.isEmpty())
                content += " ONCLICK=\"" + this.onclick + "\" ";
            content += (myCheckedValue ? "CHECKED" : "");
            content += ">";

            out.print(content);
        } catch (Exception e) {}
    }

}
