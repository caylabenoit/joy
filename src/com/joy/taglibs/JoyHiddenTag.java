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
package com.joy.taglibs;

import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyHiddenTag extends SimpleTagSupport {
    private String name;
    private String value;
    
    public JoyHiddenTag() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        JspContext jsp = this.getJspContext();
        JspWriter out =jsp.getOut();
        String retText = "";
        retText = "<INPUT type='hidden' ";
        retText += " name='" + this.name + "' "; 
        retText += " id='" + this.name + "' "; 
        retText += " value='" + this.value + "' "; 
        retText += " >";
        out.println(retText);
    }

}
