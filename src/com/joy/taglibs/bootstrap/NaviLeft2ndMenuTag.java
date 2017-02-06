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

import com.joy.C;
import com.joy.Joy;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class NaviLeft2ndMenuTag extends SimpleTagSupport {
    private String xmlconfig;   // fichier de configuration
    private String activemenuid;
    private String selectedcolor;

    public String getSelectedcolor() {
        return selectedcolor;
    }

    public void setSelectedcolor(String selectedcolor) {
        this.selectedcolor = selectedcolor;
    }
    
    public NaviLeft2ndMenuTag() {
        activemenuid="";
        xmlconfig="";
        selectedcolor="#f5f5f5";
    }
    
    public String getXmlconfig() {
        return xmlconfig;
    }

    public void setXmlconfig(String xmlconfig) {
        this.xmlconfig = xmlconfig;
    }

    public String getActivemenuid() {
        return activemenuid;
    }

    public void setActivemenuid(String activemenuname) {
        this.activemenuid = activemenuname;
    }
    
    /**
     * Create a top menu
     * @param out write output
     * @throws IOException 
     */
    private void left2ndMenu(JspWriter out) throws IOException {         
        try {
            SAXBuilder sxb = new SAXBuilder();
            org.jdom2.Document document;
            Element racine;
            String sMenu ="";

            if (xmlconfig == null)
                return;
            
            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlconfig));
            racine = document.getRootElement();
            
            // add the menus items
            List rootpath = racine.getChildren(C.JOYMENU_TAG);
            Iterator items = rootpath.iterator();
            String itemLine = "";
            while(items.hasNext()) {
                Element item = (Element)items.next();
                String id = item.getAttributeValue(C.JOYMENUATTR_ID);
                String glyphe = item.getAttributeValue(C.JOYMENUATTR_GLYPHE);
                String name = item.getAttributeValue(C.JOYMENUATTR_NAME);
                String object = item.getAttributeValue(C.ACTION_TAG_OBJECT);
                String actiontype = item.getAttributeValue(C.ACTION_TYPE_TAG);
                String url = Joy.URL(object, actiontype);
                
                itemLine += "<a href='" + url + "'  class=\"list-group-item\"";
                if (activemenuid.equalsIgnoreCase(id))
                    itemLine += " style=\"background-color: " + selectedcolor + "\"";
                itemLine += ">";
                if (glyphe != null)
                    itemLine += "<i class=\"fa " + glyphe + " fa-fw\"></i>&nbsp;";
                itemLine += name;
                itemLine +="</a>";
            }
            
            // add the buttons
            rootpath = racine.getChildren(C.JOYMENU_TAG_BUTTON);
            items = rootpath.iterator();
            String buttonLine = "";
            while(items.hasNext()) {
                Element item = (Element)items.next();
                String name = item.getAttributeValue(C.JOYMENUATTR_NAME);
                String object = item.getAttributeValue(C.ACTION_TAG_OBJECT);
                String actiontype = item.getAttributeValue(C.ACTION_TYPE_TAG);
                String url = Joy.URL(object, actiontype);
                
                buttonLine += "<a href='" + url + "'  class=\"btn btn-default btn-block\">";
                buttonLine += name;
                buttonLine +="</a>";
            }
            
            sMenu = "<DIV class=\"panel-body\">";
            sMenu += "<DIV class=\"list-group\">";
            sMenu += itemLine;
            sMenu += "</DIV>";
            sMenu += buttonLine;
            sMenu += "</DIV>";
            out.print(sMenu);
            
        } catch (IOException | JDOMException ex) {
            Joy.LOG().debug ( ex.toString());
            out.println("No menu defined.");
        }
    }
    
    
    /**
     * Ecriture HTML du Tag
     * @throws JspException Exception
     * @throws IOException  Exception
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Create a top menu or reuse the existing one
        left2ndMenu(this.getJspContext().getOut());
    }
}
