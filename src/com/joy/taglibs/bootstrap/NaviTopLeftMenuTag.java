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
import com.joy.mvc.Action;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;


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
public class NaviTopLeftMenuTag extends SimpleTagSupport {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Ecriture HTML du Tag
     * @throws JspException
     * @throws IOException 
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Create a top menu or reuse the existing one
        JspContext jsp = this.getJspContext();
        String outStr = "";
        
        try {
            Action actionform = (Action)jsp.findAttribute(C.ACTION_FORM_BEAN);
            String myTitle = "";
            if (title==null) {
                myTitle = Joy.parameters().getApplicationName() + " v" + Joy.parameters().getVersion();
            } else
                myTitle = title;
            outStr += "<!-- Top Left Name -->";
            outStr += "<div class=\"navbar-header\">";
            outStr += "<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">";
            outStr += "  <span class=\"sr-only\">Toggle navigation</span>";
            outStr += "  <span class=\"icon-bar\"></span>";
            outStr += "  <span class=\"icon-bar\"></span>";
            outStr += "  <span class=\"icon-bar\"></span>";
            outStr += "</button>";
            outStr += "<a class=\"navbar-brand\" href=\"#\">" + myTitle + "</a>";
            outStr += "</div>";
            outStr += "<!-- Top Left Name -->";
            
            this.getJspContext().getOut().println(outStr);
        } catch (Exception e) { 
            
        }
    }

}
