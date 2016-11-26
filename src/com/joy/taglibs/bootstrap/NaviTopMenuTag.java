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
package com.joy.taglibs.bootstrap;

import com.joy.Joy;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import static javax.servlet.jsp.tagext.Tag.EVAL_BODY_INCLUDE;
import static javax.servlet.jsp.tagext.Tag.EVAL_PAGE;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class NaviTopMenuTag extends TagSupport {

    private String title;

    public NaviTopMenuTag() {
        this.title = "";
    }

    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Add the responsive menu (fixed) with app title
     * @param title
     * @return 
     */
    private void responsiveMenuButton(JspWriter out) {
        try {
            String outStr = "";
            outStr += "<!-- Top Left Name -->";
            outStr += "<div class=\"navbar-header\">";
            outStr += "<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">";
            outStr += "  <span class=\"sr-only\">Toggle navigation</span>";
            outStr += "  <span class=\"icon-bar\"></span>";
            outStr += "  <span class=\"icon-bar\"></span>";
            outStr += "  <span class=\"icon-bar\"></span>";
            outStr += "</button>";
            
            if (this.title.isEmpty()) {
                outStr += "<DIV class='logobloc'>";
                String myTitle = Joy.PARAMETERS().getApplicationName() + " v" + Joy.PARAMETERS().getVersion();
                outStr += "<A href='" + Joy.URL("about", "display") + "'>";
                outStr += "<IMG src='" + Joy.PARAMETERS().getParameter("logo").getValue() + "' class='logomenu' alt='" + myTitle + "'/>";
                outStr += "</A>";
                outStr += "</DIV>";
            } else
                outStr += "<a class=\"navbar-brand\" href=\"#\">" + title + "</a>";

            outStr += "</div>";
            outStr += "<!-- Top Left Name -->";
            
            out.println(outStr);
        } catch (IOException ex) {
        
        }
    }
    
    @Override
    public int doStartTag() throws JspException {
        JspWriter out =pageContext.getOut();
        try {
            
            out.println("<!-- Start Navi Top Menu -->");
            responsiveMenuButton(out);
            out.println("<DIV class='collapse navbar-collapse' id='bs-example-navbar-collapse-1'>");
            
            
        } catch (IOException ex) {
            Logger.getLogger(NaviTopMenuTag.class.getName()).log(Level.SEVERE, null, ex);
        }
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            JspWriter out =pageContext.getOut();

            out.println("<!-- End Navi Top Menu -->");
            out.println("</DIV>");
        } catch (IOException ex) {}
            return EVAL_PAGE;
    }
}
