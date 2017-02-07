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
 * @author Benoit CAYLA (benoit@famillecayla.fr) CAYLA
 */
public class NaviTopRightUserMgtMenuTag extends SimpleTagSupport {

    /**
     * Ecriture HTML du Tag
     * @throws JspException
     * @throws IOException 
     */
    @Override
    public void doTag() throws JspException, IOException {
        // Create a top menu or reuse the existing one
        JspContext jsp = this.getJspContext();
        String User = "";
        String outStr = "";
        
        try {
            Action actionform = (Action)jsp.findAttribute(C.ACTION_FORM_BEAN);
            User = actionform.getSessionAttr("user");
            outStr = "<!-- User Login & profile -->";
            outStr += "<LI class=\"dropdown\">";
            outStr += "  <a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">";
            outStr += "  <i class=\"fa fa-user fa-fw\"></i><i class=\"fa fa-caret-down\"></i>";
            outStr += "  </a>";
            outStr += "<UL class=\"dropdown-menu dropdown-user\">";
            if (!User.isEmpty())
                outStr += "<li><a href=\"#\"><i class=\"fa fa-user fa-fw\"></i> " +  User + " Profile</a></li>";
            outStr += "  <li><a href=\"#\"><i class=\"fa fa-gear fa-fw\"></i> Settings</a></li>";
            outStr += "  <li class=\"divider\"></li>";
            if (!User.isEmpty()) 
                outStr += "<li><a href=\""+ Joy.URL("login", "logout") + "\"><i class=\"fa fa-sign-out fa-fw\"></i> Logout</a>";
            else
                outStr += "<li><a href=\""+ Joy.URL("login", "request") + "\"><i class=\"fa fa-sign-out fa-fw\"></i> Login</a>";
            outStr += "  </li>";
            outStr += "</UL>";
            outStr += "</LI>";
            outStr += "<!-- End User Login & profile -->";
            
            this.getJspContext().getOut().println(outStr);
        } catch (Exception e) { 
            
        }
    }

}
