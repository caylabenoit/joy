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
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;

/**
 *
 * @author Benoit CAYLA <benoit@famillecayla.fr>
 */
public class NaviCommonFunctions {
    /**
     * Construit un morceau d'URL avec les parametres seulement (x étant un chiffre)
     * @param element
     * @return 
     */
    private static String buildURLParameter(Element element) {
        int i=1;
        String urlPart = "&";
        while (element.getAttributeValue("pn" + i) != null) {
            urlPart += element.getAttributeValue("pn" + i) + "=" + element.getAttributeValue("pv" + i) + "&";
            i++;
        }
        return urlPart.substring(0, urlPart.length()-1);
    }

    /**
     * Build a HREF hyperlink
     * @param liURL
     * @param liObject
     * @param liActionType
     * @param child
     * @return 
     */
    public static String buildURL(String liURL, 
                                    String liObject, 
                                    String liActionType, 
                                    Element child) {
        String url = "";

        if (liURL == null) {
            if (liObject != null) {
                url += "." + Joy.PARAMETERS().getJoyDefaultURLPattern() + "?";
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
     * 
     * @param name
     * @param finalpath
     * @return 
     */
    public static boolean isMenuSelected(String name, 
                                         List<String> finalpath) {
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
    public static void getListOfActiveMenu(List bloc, 
                                           List finalpath,
                                           String activemenuid) {
        Iterator iter = bloc.iterator();
        while(iter.hasNext()) {
            Element child = (Element)iter.next();
            String id = child.getAttributeValue(C.JOYMENUATTR_ID);
            if (id.equalsIgnoreCase(activemenuid)) {
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
                getListOfActiveMenu(childs, finalpath, activemenuid);
            }
        }
    }
}
