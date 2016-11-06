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

import com.joy.C;
import com.joy.Joy;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyAHrefTag extends TagSupport{
    private String name;
    private String id;
    private String object;
    private String actiontype;
    private String otherparams;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getActiontype() {
        return actiontype;
    }

    public void setActiontype(String actiontype) {
        this.actiontype = actiontype;
    }

    public String getOtherparams() {
        return otherparams;
    }

    public void setOtherparams(String otherparams) {
        this.otherparams = otherparams;
    }
    
    @Override
    public int doStartTag() throws JspException {
        JspWriter out =pageContext.getOut();
        String aHref = "";
        String hyperlink = "";
        
        try {
            aHref += "." + Joy.parameters().getJoyDefaultURLPattern();
            aHref += "?" + C.ACTION_TAG_OBJECT + "=" + object;
            if (actiontype != null)
                aHref += "&" + C.ACTION_TYPE_TAG + "=" + actiontype;
            if (otherparams != null)
                aHref += "&" + otherparams;

            hyperlink = "<A HREF='" + aHref + "'";
            if (this.name != null)
                hyperlink += " name='" + this.name + "'";
            if (this.id != null)
                hyperlink += " id='" + this.id + "'";
            hyperlink += ">";

            out.println(hyperlink);
        } catch (IOException ex) {
        }
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            JspWriter out =pageContext.getOut();
            out.println("</A>");
        } catch (IOException ex) {
        }
        return EVAL_PAGE;
    }
}
