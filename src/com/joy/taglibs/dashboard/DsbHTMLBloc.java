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

import com.joy.Joy;
import com.joy.dashboard.DashboardBloc;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class DsbHTMLBloc extends SimpleTagSupport {
    private boolean body;

    public boolean isBody() {
        return body;
    }

    public void setBody(boolean body) {
        this.body = body;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        try {
            JspContext jsp = this.getJspContext();
            JspWriter out =jsp.getOut();
            String retText = "";

            // Get the Action object

            DashboardBloc bloc = (DashboardBloc)jsp.findAttribute("DASHBOARD_COLUMN");

            if (bloc != null) {
                if (body)
                    retText = bloc.getHTMLContent();
                else
                    retText = bloc.getJSContent();
            }
            out.print(retText);
        } catch (Exception e) {
            Joy.log().fatal(e);
        }
    }

}
