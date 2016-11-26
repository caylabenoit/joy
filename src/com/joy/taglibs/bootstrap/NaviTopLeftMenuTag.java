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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


/**
 * Taglib d'affichage d'un menu
*    Chaque element de menu est placé dans un tag <joy-menu></joy-menu>
    Attributs du tag:
 name (obligatoire) : nom du menu
    [choix]
 URL (obligatoire): JOYURL du menu (http://)
    Ou
 object (obligatoire) nom du tag Object (joy)
 actiontype (facultatif) nom de l'actiontype (joy)
 parametres (entier x de 1 à ...)
        - pnx= Nom du parametre x
        - pnv= Valeur du parametre x
 * @author Benoit CAYLA (benoit@famillecayla.fr) 
 */
public class NaviTopLeftMenuTag extends SimpleTagSupport {
    private String title;
    private String activemenuid;
    private String xmlconfig;
    private static final String EOL = "\n";
    
    /**
     * Get the value of activemenuid
     *
     * @return the value of activemenuid
     */
    public String getActivemenuid() {
        return activemenuid;
    }

    /**
     * Set the value of activemenuid
     *
     * @param activemenuid new value of activemenuid
     */
    public void setActivemenuid(String activemenuid) {
        this.activemenuid = activemenuid;
    }

    /**
     * Get the value of xmlconfig
     *
     * @return the value of xmlconfig
     */
    public String getXmlconfig() {
        return xmlconfig;
    }

    /**
     * Set the value of xmlconfig
     *
     * @param xmlconfig new value of xmlconfig
     */
    public void setXmlconfig(String xmlconfig) {
        this.xmlconfig = xmlconfig;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String buildHrefTag(Element elt, 
                                boolean isDropdown) {
        String liObject = elt.getAttributeValue(C.ACTION_TAG_OBJECT);
        String liActionType = elt.getAttributeValue(C.ACTION_TYPE_TAG);
        String liName = elt.getAttributeValue(C.JOYMENUATTR_NAME);
        String liClass = elt.getAttributeValue(C.JOYMENUATTR_CLASS);
        String liId = elt.getAttributeValue(C.JOYMENUATTR_ID);
        String liURL = elt.getAttributeValue(C.JOYMENUATTR_URL);
        
        String ret = "";
        
        ret += "<A " + ((liId.equalsIgnoreCase(this.activemenuid)) ? "class='active'" : "");
        if (isDropdown) {
            ret += " class='dropdown-toggle' data-toggle='dropdown' role='button' aria-haspopup='true' aria-expanded='false' ";
        }
        ret += " HREF='" + NaviCommonFunctions.buildURL(liURL, liObject, liActionType, elt);
        ret += "'>";
        ret += (liClass != null ? "<I class='" + liClass + "'></I>&nbsp;" : "");
        ret += liName + "</A>";
        
        return ret;
    }
    
    private String printChildsLI(List childs) {
        Iterator iter = childs.iterator();
        String BlocLI = "";
        
        while(iter.hasNext()) { // parcours tous les éléments du bloc 
            Element child = (Element)iter.next();
            String liId = child.getAttributeValue(C.JOYMENUATTR_ID);
                
            BlocLI += ((liId.equalsIgnoreCase(this.activemenuid)) ? "<LI class='active'>" : "<LI>"); 
            BlocLI += buildHrefTag(child, false);
            BlocLI += "</LI>"  + EOL;            
        }
        return BlocLI;
    }
    
    
    /**
     * Fonction recursive de construction de blocs HTML de menu
     * @param bloc Bloc courant (donnée récursive)
     * @param CurrentBloc Chaine contenant le code HTML du menu
     * @return Chaine contenant le code HTML complete avec les éléments enfants
     */
    private String build(List bloc) {
        String CurrentBloc = EOL;
        
        if (!bloc.isEmpty()) {
            Iterator iter = bloc.iterator();
            String BlocULStart = "";
            String BlocULEnd = "";
            
            String BlocLI = "";
            while(iter.hasNext()) { // parcours tous les éléments du bloc 
                Element child = (Element)iter.next();

                // Regarde si l'élément a des enfants
                List childs = child.getChildren(C.JOYMENU_TAG);
                if (!childs.isEmpty()) { // dropdown request
                    BlocULStart = "<LI class='dropdown'>" + EOL; 
                    BlocULStart += buildHrefTag(child, true); 
                    BlocULStart += "<UL class='dropdown-menu dropdown-shortcuts'>" + EOL;
                    BlocULEnd = "</UL>";
                    BlocULEnd += "</LI>";
                    BlocLI = printChildsLI(childs);
                    
                    CurrentBloc += EOL + BlocULStart + BlocLI + BlocULEnd + EOL;
                } else {
                    childs = new ArrayList();
                    childs.add(child);
                    CurrentBloc += EOL + printChildsLI(childs) + EOL;
                }
            }
        }
        return CurrentBloc;
    }
        
    
    private void dynamicMenu(JspWriter out) throws IOException {
        try {
            SAXBuilder sxb = new SAXBuilder();
            org.jdom2.Document document;
            Element racine;
            String xmlConfigFile = (this.xmlconfig.equals("") ? "joy-menu-topleft.xml" : xmlconfig);

            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlConfigFile));
            racine = document.getRootElement();
            List root = racine.getChildren(C.JOYMENU_TAG);
            
            out.println("<UL class='nav navbar-nav'>");
            out.println(build(root));
            out.println("</UL>");
            
        } catch (IOException | JDOMException ex) {
            Joy.LOG().debug ( ex.toString());
            out.println("No menu defined.");
        }
    }
    
    /**
     * Create a top menu
     * @param out write output
     * @throws IOException 
     */
    private void menu(JspWriter out) throws IOException {     
        JspContext jsp = this.getJspContext();
        dynamicMenu(out);
    }
    
    /**
     * Ecriture HTML du Tag
     * @throws JspException
     * @throws IOException 
     */
    @Override
    public void doTag() throws JspException, IOException {
        menu(this.getJspContext().getOut());
    }

}
