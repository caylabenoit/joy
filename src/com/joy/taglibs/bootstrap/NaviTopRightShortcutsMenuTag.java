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
 * Taglib d'affichage d'un menu
*    Chaque element de menu est placé dans un tag <joy-menu></joy-menu>
*    Attributs du tag:
*    * name (obligatoire) : nom du menu
*    [choix]
*    * url (obligatoire): URL du menu (http://)
*    Ou
*    * object (obligatoire) nom du tag Object (joy)
*    * actiontype (facultatif) nom de l'actiontype (joy)
*    * parametres (entier x de 1 à ...)
*        - pnx= Nom du parametre x
*        - pnv= Valeur du parametre x
 * @author Benoit CAYLA (benoit@famillecayla.fr) 
 */
public class NaviTopRightShortcutsMenuTag extends SimpleTagSupport {
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
     * @param bloc Bloc courant (donnée récurssive)
     * @param CurrentBloc Chaine contenant le code HTML du menu
     * @return Chaine contenant le code HTML complete avec les éléments enfants
     */
    private String buildMenuBloc(List bloc, String CurrentBloc) {
        
        if (!bloc.isEmpty()) {
            CurrentBloc += eol;
            Iterator iter = bloc.iterator();
            
            String BlocLI = "";
            while(iter.hasNext()) { // parcours tous les éléments du bloc 
                Element child = (Element)iter.next();
                String liObject = child.getAttributeValue(C.ACTION_TAG_OBJECT);
                String liActionType = child.getAttributeValue(C.ACTION_TYPE_TAG);
                String liName = child.getAttributeValue(C.JOYMENUATTR_NAME);
                String liShortcut = (child.getAttributeValue(C.JOYMENUATTR_SHORTCUT) == null ? "false" : child.getAttributeValue(C.JOYMENUATTR_SHORTCUT));
                String liURL = child.getAttributeValue(C.JOYMENUATTR_URL);
                String liClass = child.getAttributeValue(C.JOYMENUATTR_CLASS);
                String childBloc = "";
                
                // Regarde si l'élément a des enfants
                List childs = child.getChildren(C.JOYMENU_TAG);
                if (!childs.isEmpty()) {
                    childBloc = buildMenuBloc(childs, childBloc);
                }
                BlocLI += childBloc;
                if (liShortcut.equalsIgnoreCase("true")) {
                    BlocLI += "<li>";
                    BlocLI += "<A href=\"" + NaviCommonFunctions.buildURL(liURL, liObject, liActionType, child) + "\"><div><p>" + (liClass != null ? "<I class='" + liClass + "'></I>&nbsp;" : "") + liName + "</p></div></A>";
                    BlocLI += "</li>";
                }
            }
            
            CurrentBloc = BlocLI +  eol;
        }
        return CurrentBloc;
    }
    
    /**
     * Create a top menu
     * @param out write output
     * @throws IOException 
     */
    private void shortcutList(JspWriter out) throws IOException {         
        try {
            SAXBuilder sxb = new SAXBuilder();
            org.jdom2.Document document;
            Element racine;
            String sMenu ="";
            String xmlConfigFile = (this.xmlconfig.equals("") ? "joy-menu.xml" : xmlconfig);

            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlConfigFile));
            racine = document.getRootElement();
            
            List root = racine.getChildren(C.JOYMENU_TAG);
            sMenu = buildMenuBloc(root, sMenu);
            
            out.println("<!-- Shortcuts -->");
            out.println("<LI class=\"dropdown\">");
            out.println("<a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">");
            out.println("<i class=\"fa fa-tasks fa-fw\"></i>&nbsp;<i class=\"fa fa-caret-down\"></i>");
            out.println("</a>");
            out.println("<ul class=\"dropdown-menu dropdown-shortcuts\">");
            out.println(sMenu);
            out.println("<li class=\"divider\"></li>");
            out.println("<li><a class=\"text-center lishortcutitem\" href=\""+ Joy.basicURL("Home", "display") + "\"><strong>Go Home</strong>&nbsp;<i class=\"fa fa-angle-right\"></i></a></li>");
            out.println("</ul></li>");
            out.println("<!-- End Shortcuts -->");
                
        } catch (IOException | JDOMException ex) {
            Joy.log().debug ( ex.toString());
            out.println("No menu defined.");
        }
    }
    
    /**
     * Ecriture HTML du Tag
     * @throws JspException
     * @throws IOException 
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Create a top menu or reuse the existing one
        shortcutList(this.getJspContext().getOut());
    }

}
