/*
 * Copyright (C) 2016 Benoit CAYLA <benoit@famillecayla.fr>
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
import java.util.ArrayList;
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
 * @author Benoit CAYLA <benoit@famillecayla.fr>
 */
public class NaviBreadCrumbsTag extends SimpleTagSupport {
    private String xmlconfig;   // fichier de configuration
    private String activemenuid;
    private static final String eol = "\n";
    
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
     * Fonction recursive de construction de blocs HTML de menu
     * @return Chaine contenant le code HTML complete avec les éléments enfants
     */
    private String buildBreadCrumbs(List<String> finalpath) {
        String BlocLI = "";
        int i = finalpath.size()-1;
        
        while (i >= 0) {
            BlocLI += "<LI class='active'>"; 
            BlocLI += finalpath.get(i--);
            BlocLI += "</LI>"  + eol;
        }

        return BlocLI;
    }
    
    /**
     * Get the active item path (all the tree) and put them into a list
     * @param bloc
     * @param finalpath 
     */
    private void getListOfActiveMenu(List bloc, List finalpath) {
        Iterator iter = bloc.iterator();
        while(iter.hasNext()) {
            Element child = (Element)iter.next();
            String id = child.getAttributeValue(C.JOYMENUATTR_ID);
            if (id.equalsIgnoreCase(this.activemenuid)) {
                Element elt = child;
                finalpath.add(elt.getAttributeValue(C.JOYMENUATTR_NAME));
                while (elt.getParentElement() != null) {
                    if (elt.getParentElement().getAttributeValue(C.JOYMENUATTR_ID) != null) {
                        finalpath.add(elt.getParentElement().getAttributeValue(C.JOYMENUATTR_NAME));
                    }
                    elt = elt.getParentElement();
                }
                return;
            } else {
                List childs = child.getChildren(C.JOYMENU_TAG);
                getListOfActiveMenu(childs, finalpath);
            }
        }
    }
    
    /**
     * Create a top menu
     * @param out write output
     * @throws IOException 
     */
    private void breadCrumbs(JspWriter out) throws IOException {         
        try {
            SAXBuilder sxb = new SAXBuilder();
            org.jdom2.Document document;
            Element racine;
            String xmlConfigFile = (this.xmlconfig.equals("") ? "joy-menu.xml" : xmlconfig);

            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlConfigFile));
            racine = document.getRootElement();
            
            List rootpath = racine.getChildren(C.JOYMENU_TAG);
            List<String> finalpath = new ArrayList();
            getListOfActiveMenu(rootpath, finalpath);

            out.println("<OL class=\"breadcrumb\">");
            out.println(buildBreadCrumbs(finalpath));
            out.println("</OL>");
                
        } catch (IOException | JDOMException ex) {
            Joy.LOG().debug ( ex.toString());
            out.println("No menu defined.");
        }
    }
    
    @Override
    public void doTag() throws JspException, IOException {
        breadCrumbs(this.getJspContext().getOut());
    }
}
