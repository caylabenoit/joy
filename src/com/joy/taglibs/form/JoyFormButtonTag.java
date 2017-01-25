/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joy.taglibs.form;

import com.joy.Joy;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyFormButtonTag extends SimpleTagSupport {
    private String id;
    private String label;
    private String onclick;
    private boolean submit;     // submit button
    private boolean back;       // back button
    private boolean cancel;     // reset button
    private boolean link;       // go to link button
    private String cssClass;
    private String object;
    private String actiontype;
    private String p1;
    
    public boolean isLink() {
        return link;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public void setLink(boolean link) {
        this.link = link;
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
    
    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public String getCSSClass() {
        return cssClass;
    }

    public void setCSSClass(String CSSClass) {
        this.cssClass = CSSClass;
    }
    
    
    public JoyFormButtonTag() {
        onclick = "";
        submit = false;
        back = false;
        cancel = false;
        link = false;
        object="";
        actiontype="";
        p1="";
        cssClass="";
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public boolean getSubmit() {
        return submit;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        JspContext jsp = this.getJspContext();
        //Get the writer object for output.
        JspWriter out =jsp.getOut();
        
        String strButtonTag = "<BUTTON ";
        if (!id.isEmpty())
            strButtonTag += " id='"+ id + "' ";
        if (!cssClass.isEmpty())
            strButtonTag += " class='" + cssClass + "' "; 
        
        if (submit) {
            // do nothing, default behavior :-)
            strButtonTag += " type='submit' ";
        } else if (cancel) {
            strButtonTag += " type='button' ";
            strButtonTag += " type='Reset' ";
        } else if (back) {
            strButtonTag += " type='button' ";
            strButtonTag += " onclick=\"javascript:history.back();\" "; 
        } else if (link) {
            String myurl = Joy.URL(object, actiontype);
            myurl += (!p1.isEmpty() ? "&" + p1 : "");
            strButtonTag += " type='button' ";
            strButtonTag += " onclick=\"javascript:window.open('" + myurl + "', '_self');\" "; 
        } else {
            strButtonTag += " type='button' ";
            if (!onclick.isEmpty())
                strButtonTag += "onclick=\""+ onclick + "\" ";
        }

        strButtonTag += ">" + this.label + "</BUTTON>";            
        
        out.print(strButtonTag);
    }
}
