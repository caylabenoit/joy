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
package com.joy.mvc.controller;

import com.joy.C;
import com.joy.Joy;
import com.joy.mvc.ActionRedirect;
import com.joy.mvc.ActionRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * This class persists the Actions configuration in the joy-actions.xml
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionConfiguration {
    private org.jdom2.Document xmlDocument;
    private Element xmlRoot;
    
    public ActionConfiguration() {
        xmlDocument = null;
        xmlRoot = null;
    }
    
    public boolean isInitialized() {
        return (xmlRoot != null);
    }
    
    public boolean init(String XMLConfigActionFile) {
        try {
            SAXBuilder sxb = new SAXBuilder();
            
            xmlDocument = sxb.build(Thread.currentThread().getContextClassLoader().getResourceAsStream(XMLConfigActionFile));
            xmlRoot = xmlDocument.getRootElement();
            return (xmlRoot != null) ;
            
        } catch (JDOMException ex) {
            Joy.LOG().error( "JDOMException=" + ex);
        } catch (IOException ex) {
            Joy.LOG().error( "IOException=" + ex);
        }
        return false;
    }
    
    private String getElementText(Element elt, String child) {
        try {
            return elt.getChild(child).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public ActionRequest getActionRequest(String _object) {
        ActionRequest result = null;
        if (xmlRoot == null) return null;
        
        result = new ActionRequest();
        
        if (_object.equalsIgnoreCase(C.ACTION_TAG_OBJECTTYPE_LOGIN)) {
            
            // Login management into tag <loginRequest>
            Element levelLogin = xmlRoot.getChild("loginRequest");
            if (levelLogin != null) {
                result.setObject(C.ACTION_TAG_OBJECTTYPE_LOGIN);
                result.setObjectType(C.ACTION_TAG_OBJECTTYPE_LOGIN);
                result.setJavaclass(getElementText(levelLogin, C.ACTION_TAG_ACTION));
                result.setFlowType(C.ACTION_TAG_OBJECTTYPE_LOGIN);
                // add the Request redirection
                Element requestTag = levelLogin.getChild("request");
                ActionRedirect redirReq = new ActionRedirect();
                redirReq.setTag(C.ACTION_TAG_LOGIN_REQUEST);
                redirReq.setUrl(requestTag.getText());
                result.addRedirection(redirReq);
                // add the Success redirection
                Element successTag = levelLogin.getChild("afterlogin");
                ActionRedirect successobj = new ActionRedirect();
                successobj.setTag(C.ACTION_TAG_LOGIN_AFTERLOGIN);
                successobj.setUrl(successTag.getText());
                result.addRedirection(successobj);
                // add the Request redirection
                Element failTag = levelLogin.getChild("afterlogout");
                ActionRedirect failobj = new ActionRedirect();
                failobj.setTag(C.ACTION_TAG_LOGIN_AFTERLOGOUT);
                failobj.setUrl(failTag.getText());
                result.addRedirection(failobj);
 
                result.setisInitialized(true);
            } else
                result.setisInitialized(false);
            
        } else {
            // Manage the <ActionRequest> Tags only
            List level1list = xmlRoot.getChildren(C.ACTION_TAG_REQUEST);
            Iterator i1 = level1list.iterator();
            while(i1.hasNext()) { 
                Element elt = (Element)i1.next();
                if (_object.equalsIgnoreCase(elt.getChild(C.ACTION_TAG_OBJECT).getText())) {
                    result.setObject(elt.getChild(C.ACTION_TAG_OBJECT).getText());
                    result.setObjectType(getElementText(elt, C.ACTION_TAG_OBJECTTYPE));
                    result.setJavaclass(getElementText(elt, C.ACTION_TAG_ACTION));
                    result.setFlowType(getElementText(elt, C.ACTION_TAG_FLOWTYPE));
                    result.setisInitialized(true);

                    // récupère la liste des redirects
                    Element eltRedirects = elt.getChild(C.ACTION_TAG_REDIRECTS);
                    if (eltRedirects != null) {
                        List redirects = eltRedirects.getChildren(C.ACTION_TAG_REDIRECT);
                        Iterator i2 = redirects.iterator();

                        while(i2.hasNext()) { // parcours les noeuds redirects
                            Element redir = (Element)i2.next();
                            ActionRedirect redirobj = new ActionRedirect();
                            redirobj.setTag(redir.getAttributeValue(C.ACTION_TAG_ATTRIB_TAG));
                            redirobj.setUrl(redir.getText());
                            result.addRedirection(redirobj);
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public String getRedirectNoAction(String _Tag) {
        if (xmlRoot == null) return null;
        
        Element root = xmlRoot.getChild(C.ACTION_TAG_REDIRECT_NO_ACTION);
        List redirects = root.getChildren(C.ACTION_TAG_REDIRECT);
        Iterator i2 = redirects.iterator();

        while(i2.hasNext()) { // parcours les noeuds redirects
            Element redir = (Element)i2.next();
            if (_Tag.equalsIgnoreCase(redir.getAttributeValue(C.ACTION_TAG_ATTRIB_TAG))) {
                return redir.getText();
            }
        }
        return "";
    }
}
