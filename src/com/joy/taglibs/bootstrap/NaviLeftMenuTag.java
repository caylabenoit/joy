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
public class NaviLeftMenuTag extends SimpleTagSupport {
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
     * Construit un morceau d'url avec les parametres seulement (x étant un chiffre)
 PNx : Nom du parametre
 PVx : Valeur du parametre
     * @param element
     * @return 
     */
    private String buildURLParameter(Element element) {
        int i=1;
        String urlPart = "&";
        while (element.getAttributeValue("pn" + i) != null) {
            urlPart += element.getAttributeValue("pn" + i) + "=" + element.getAttributeValue("pv" + i) + "&";
            i++;
        }
        return urlPart.substring(0, urlPart.length()-1);
    }

    /**
     * Buil a href hyperlink
     * @param liURL
     * @param liObject
     * @param liActionType
     * @param child
     * @return 
     */
    private String buildURL(String liURL, 
                            String liObject, 
                            String liActionType, 
                            Element child) {
        String url = "";

        if (liURL == null) {
            if (liObject != null) {
                url += "." + Joy.parameters().getJoyDefaultURLPattern() + "?";
                if (liObject != null)
                    url += C.ACTION_TAG_OBJECT + "=" + liObject + "&";
                if (liActionType!= null)
                    url += C.ACTION_TYPE_TAG + "="  + liActionType;
                url += buildURLParameter(child);
            } else { 
                url += "#";
            }
        } else { 
            url = liURL;
        }
        return url;
    }
    
    /**
     * Fonction recursive de construction de blocs HTML de menu
     * @param bloc Bloc courant (donnée récurssive)
     * @param CurrentBloc Chaine contenant le code HTML du menu
     * @param CurrentLevel niveau actuel
     * @return Chaine contenant le code HTML complete avec les éléments enfants
     */
    private String buildMenuBloc(List bloc, 
                                 String CurrentBloc,
                                 int CurrentLevel,
                                 List finalpath) {
        
        if (!bloc.isEmpty()) {
            CurrentBloc += eol;
            Iterator iter = bloc.iterator();
            String BlocUL = "";
            boolean blocSelected = false;
            
            String BlocLI = "";
            while(iter.hasNext()) { // parcours tous les éléments du bloc 
                Element child = (Element)iter.next();
                String liObject = child.getAttributeValue(C.ACTION_TAG_OBJECT);
                String liActionType = child.getAttributeValue(C.ACTION_TYPE_TAG);
                String liName = child.getAttributeValue(C.JOYMENUATTR_NAME);
                String liClass = child.getAttributeValue(C.JOYMENUATTR_CLASS);
                String liId = child.getAttributeValue(C.JOYMENUATTR_ID);
                String liURL = child.getAttributeValue(C.JOYMENUATTR_URL);
                boolean hasChild = false;
                String childBloc = "";
                
                if (isMenuSelected(liId, finalpath))
                    blocSelected = true;
                
                // Regarde si l'élément a des enfants
                List childs = child.getChildren(C.JOYMENU_TAG);
                if (!childs.isEmpty()) {
                    childBloc = buildMenuBloc(childs, childBloc, CurrentLevel+1, finalpath);
                    hasChild = true;
                }

                BlocLI += ((liId.equalsIgnoreCase(this.activemenuid)) ? "<LI class='active'>" : "<LI>"); 
                BlocLI += "<A aria-expanded='false' " + ((liId.equalsIgnoreCase(this.activemenuid)) ? "class='active'" : "") + " href='" + buildURL(liURL, liObject, liActionType, child) + "'>";
                BlocLI += (liClass != null ? "<I class='" + liClass + "'></I>&nbsp;" : "");
                if (hasChild)
                    BlocLI += "<SPAN class='fa arrow'></SPAN>";
                BlocLI += liName + "</A>";
                BlocLI += (hasChild ? childBloc : "");
                BlocLI += "</LI>"  + eol; 
            }
            
            switch (CurrentLevel) {
                case 1: 
                    BlocUL = "<UL class='nav metismenu' id='side-menu'>" + eol; 
                    break;
                case 2: 
                    BlocUL = (!blocSelected ? "<UL class='nav nav-second-level' aria-expanded='false'>" : "<UL class='nav nav-second-level collapse in' aria-expended='false'>") + eol;
                    break;
                case 3: 
                    BlocUL = (!blocSelected ? "<UL class='nav nav-third-level' aria-expanded='false'>" : "<UL class='nav nav-third-level collapse in' aria-expended='false'>") + eol;
                    break;
                default: 
                    BlocUL = (!blocSelected ? "<UL class='nav nav-fourth-level' aria-expanded='false'>" : "<UL class='nav nav-fourth-level collapse in' aria-expended='false'>") + eol;
            }

            CurrentBloc = eol + BlocUL + BlocLI + "</UL>" + eol;
        }
        return CurrentBloc;
    }

    /**
     * 
     * @param name
     * @param finalpath
     * @return 
     */
    private boolean isMenuSelected(String name, List<String> finalpath) {
        for (String menu : finalpath) 
            if (name.equalsIgnoreCase(menu))
                return true;
        return false;
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
                finalpath.add(id);
                while (elt.getParentElement() != null) {
                    if (elt.getParentElement().getAttributeValue(C.JOYMENUATTR_ID) != null)
                        finalpath.add(elt.getParentElement().getAttributeValue(C.JOYMENUATTR_ID));
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
    private void leftMatisMenu(JspWriter out) throws IOException {         
        try {
            SAXBuilder sxb = new SAXBuilder();
            org.jdom2.Document document;
            Element racine;
            String sMenu ="";
            String xmlConfigFile = (this.xmlconfig.equals("") ? "joy-menu.xml" : xmlconfig);

            document = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlConfigFile));
            racine = document.getRootElement();
            
            List rootpath = racine.getChildren(C.JOYMENU_TAG);
            List<String> finalpath = new ArrayList();
            getListOfActiveMenu(rootpath, finalpath);
            
            List root = racine.getChildren(C.JOYMENU_TAG);
            sMenu = buildMenuBloc(root, sMenu, 1, finalpath);
            
            out.println("<DIV class='navbar-default sidebar' role='navigation'>");
            out.println("<DIV class='sidebar-nav navbar-collapse'>");
            out.println(sMenu);
            out.println("</DIV>");
            out.println("</DIV>");
                
        } catch (IOException | JDOMException ex) {
            Joy.log().debug ( ex.toString());
            out.println("No menu defined.");
        }
    }
    
    /**
     * Ecriture HTML du Tag
     * @throws JspException Exception
     * @throws IOException Exception
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Create a top menu or reuse the existing one
        leftMatisMenu(this.getJspContext().getOut());
    }

}
