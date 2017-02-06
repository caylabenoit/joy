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

import com.joy.dashboard.DashboardBloc;
import com.joy.dashboard.DashboardRow;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class DsbHTMLColLoop extends TagSupport  {
    private DashboardRow myDashboardRow; 
    private int i;
    private boolean noRow;
    private boolean bootstrapSupport;

    public boolean isBootstrapSupport() {
        return bootstrapSupport;
    }

    public void setBootstrapSupport(boolean bootstrapSupport) {
        this.bootstrapSupport = bootstrapSupport;
    }
    
    public DsbHTMLColLoop() {
        noRow = true;
        i=0;
        bootstrapSupport = true;
    }
    
    private boolean doNext () {
        if (i < myDashboardRow.columnCount()) {
            JspWriter out = pageContext.getOut();
            DashboardBloc myCol = myDashboardRow.getColumn(i);
            try {
                if (bootstrapSupport) {
                    out.println("<!-- Dashboard Column iteration here -->");
                    out.println("<div class='col-lg-" + myCol.getSize() + "'>");
                    out.println("<div class='panel panel-default'>");
                    out.println("<div class='panel-heading'>" + myCol.getTitle() + "</div>");
                    out.println("<div class='panel-body'>");
                }
            } catch (IOException ex) {}
            
            pageContext.setAttribute("DASHBOARD_COLUMN", myCol);
            pageContext.setAttribute("DASHBOARD_COLUMN_INDEX", i);
            i++;
            return true;
        } else
            return false;
    }
    
    @Override
    public int doAfterBody() throws JspException {
            JspWriter out = pageContext.getOut();
            try {
                if (bootstrapSupport)
                    out.println("</div></div></div>"); // column close
            } catch (IOException ex) {}
            if (doNext()) {
                return EVAL_BODY_AGAIN;
            }
            return SKIP_BODY;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            myDashboardRow = (DashboardRow)pageContext.getAttribute("DASHBOARD_ROW");
            if (myDashboardRow != null) {
                JspWriter out = pageContext.getOut();
                out.println("<!-- Dashboard Column iteration Starts -->");
                if (myDashboardRow.columnCount() > 0) {
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
            pageContext.removeAttribute("DASHBOARD_COLUMN");
            pageContext.removeAttribute("DASHBOARD_COLUMN_INDEX");
            if (!noRow) {
                JspWriter out =pageContext.getOut();
                out.println("<!-- Dashboard Column iteration Ends -->");
            }
        } catch (IOException ex) {
        }
            return EVAL_PAGE;
    }
}
