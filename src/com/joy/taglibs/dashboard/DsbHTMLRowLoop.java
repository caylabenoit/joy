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
package com.joy.taglibs.dashboard;

import com.joy.C;
import com.joy.dashboard.Dashboard;
import com.joy.dashboard.DashboardRow;
import com.joy.mvc.Action;
import com.joy.mvc.ActionForm;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class DsbHTMLRowLoop extends TagSupport  {
    private String name;
    private Dashboard myDashboard; 
    private int i;
    private boolean noRow;
    private boolean bootstrapSupport;

    public boolean isBootstrapSupport() {
        return bootstrapSupport;
    }

    public void setBootstrapSupport(boolean bootstrapSupport) {
        this.bootstrapSupport = bootstrapSupport;
    }
    
    public DsbHTMLRowLoop() {
        noRow = true;
        i=0;
        bootstrapSupport = true;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean doNext () {
        if (i < myDashboard.rowCount()) {
            JspWriter out = pageContext.getOut();
            try {
                out.println("<!-- Dashboard Row iteration here -->");
                if (bootstrapSupport)
                    out.println("<DIV class='row'>");
            } catch (IOException ex) {}
            DashboardRow myRow = myDashboard.getRow(i);
            pageContext.setAttribute("DASHBOARD_ROW", myRow);
            pageContext.setAttribute("DASHBOARD_INDEX", i);
            i++;
            return true;
        } else
            return false;
    }
    
    @Override
    public int doAfterBody() throws JspException {
            JspWriter out = pageContext.getOut();
            try {
                out.println("</DIV>"); // column close
            } catch (IOException ex) {}
            if (doNext()) {
                return EVAL_BODY_AGAIN;
            }
            return SKIP_BODY;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            ActionForm ActionObject = (ActionForm)pageContext.getRequest().getAttribute(C.ACTION_FORM_BEAN);
            myDashboard = (Dashboard)ActionObject.getSingle(name).getValue();
            if (myDashboard != null) {
                JspWriter out = pageContext.getOut();
                out.println("<!-- Dashboard Row iteration Starts -->");
                if (myDashboard.rowCount() > 0) {
                    i = 0;
                    doNext();
                    noRow=false;
                } else {
                    noRow = true;
                    return SKIP_BODY;
                }
            }
        } catch (IOException ex) {}
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.removeAttribute("DASHBOARD_ROW");
            pageContext.removeAttribute("DASHBOARD_INDEX");
            if (!noRow) {
                JspWriter out =pageContext.getOut();
                out.println("<!-- Dashboard Row iteration Ends -->");
            }
        } catch (IOException ex) {
        }
            return EVAL_PAGE;
    }
}
