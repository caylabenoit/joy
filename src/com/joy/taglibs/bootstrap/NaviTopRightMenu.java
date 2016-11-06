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
public class NaviTopRightMenu extends TagSupport {
    
    @Override
    public int doStartTag() throws JspException {
        JspWriter out =pageContext.getOut();
        try {
            out.println("<!-- Navi Top Right Menu -->");
            out.println("<UL class=\"nav navbar-top-links navbar-right\">");
        } catch (IOException ex) {
            Logger.getLogger(NaviTopRightMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            JspWriter out =pageContext.getOut();
            out.println("</UL>");
            out.println("<!-- End Navi Top Right Menu -->");
        } catch (IOException ex) {}
            return EVAL_PAGE;
    }
}
