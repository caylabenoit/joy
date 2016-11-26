/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joy.taglibs.form;

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
public class JoyFormTag extends TagSupport  {
    private String actiontype;
    private String object;
    private String name;
    private String id;
    private boolean inline;

    public JoyFormTag() {
        inline = true;
    }

    public boolean isInline() {
        return inline;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getActiontype() {
        return actiontype;
    }

    public void setActiontype(String actiontype) {
        this.actiontype = actiontype;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out =pageContext.getOut();
            out.println("<FORM " + (inline?"class='form-inline'" : "") + " role='form' method='POST' action='" + Joy.PARAMETERS().getJoyDefaultActionName() + "' name='" + name + "' id='" + (id==null ? name : id) + "'>");
            if (actiontype != null)
                out.println("<INPUT type='hidden' name='" + C.ACTION_TYPE_TAG + "' id='" + C.ACTION_TYPE_TAG + "' value='" + actiontype + "' />");
            out.println("<INPUT type='hidden' name='" + C.ACTION_TAG_OBJECT + "' id='" + C.ACTION_TAG_OBJECT + "' value='" + object + "' />" );
        } catch (IOException ex) {}
        return EVAL_BODY_INCLUDE;
    }
    
    @Override
    public int doEndTag() throws JspException {
        try {
            JspWriter out =pageContext.getOut();
            out.println("</FORM>");
        } catch (IOException ex) {}
            return EVAL_PAGE;
    }
}
