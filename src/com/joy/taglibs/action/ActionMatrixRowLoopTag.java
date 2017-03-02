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
import com.joy.mvc.ActionForm;
import com.joy.mvc.formbean.JoyFormMatrix;
import com.joy.mvc.formbean.JoyFormVector;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionMatrixRowLoopTag extends TagSupport  {
    private String name;
    private JoyFormMatrix matrix; 
    private int i;
    private boolean noRow;

    public ActionMatrixRowLoopTag() {
        noRow = true;
        i=0;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean doNext () {
        if (i < matrix.getMatrix().size()) {
            JoyFormVector vector = matrix.getMatrix().get(i);
            pageContext.setAttribute(C.JOTYAGLIB_ROWATTRIBUTE, vector);
            pageContext.setAttribute(C.JOTYAGLIB_ROWINDEX, i);
            i++;
            return true;
        } else
            return false;
    }
    
    @Override
    public int doAfterBody() throws JspException {
            if (doNext())
                return EVAL_BODY_AGAIN;
            return SKIP_BODY;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            ActionForm ActionObject = (ActionForm)pageContext.getRequest().getAttribute(C.ACTION_FORM_BEAN);
            matrix = ActionObject.getMatrix(name);
            if (matrix != null) {
                if (matrix.getRowNumber() > 0) {
                    i = 0;
                    doNext();
                    noRow=false;
                } else {
                    noRow = true;
                    return SKIP_BODY;
                }
                JspWriter out = pageContext.getOut();
                out.println("<!-- Joy Start Matrix -->");
            }
        } catch (IOException ex) {}
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.removeAttribute(C.JOTYAGLIB_ROWATTRIBUTE);
            pageContext.removeAttribute(C.JOTYAGLIB_ROWINDEX);
            if (!noRow) {
                JspWriter out =pageContext.getOut();
                out.println("<!-- Joy End Matrix -->");
            }
        } catch (IOException ex) {
        }
            return EVAL_PAGE;
    }
}
