/*
 * Copyright (C) 2017 Benoit Cayla (benoit@famillecayla.fr)
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
package com.joy.api;

import com.joy.common.joyClassTemplate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyApiRequest extends joyClassTemplate {
    private List<String> actions;
    private List<JoyApiRequestParameter> parameters;
    private String httpMethod;

    public JoyApiRequest(HttpServletRequest _request, 
                         String ApiStart) {
        try {
            actions = new ArrayList();
            parameters = new ArrayList();
            
            // GET, POST ...
            httpMethod = _request.getMethod();
            
            // Manage URL elements only at first
            String url = _request.getRequestURL().toString();
            if (url.indexOf("?") > 0)
                url = url.substring(0, url.indexOf("?")-1);
            String urlParts[] = url.split("/");
            boolean apiFound = false;
            for (String urlPart : urlParts) {
                if (apiFound) 
                    actions.add(urlPart);
                if (urlPart.equalsIgnoreCase(ApiStart)) 
                    apiFound = true;
            }
            
            // get the URL parameters (and/or attributes) next
            Enumeration<String> params = _request.getParameterNames();
            while (params.hasMoreElements()) {
                String param = params.nextElement();
                if (!param.equalsIgnoreCase("?"))
                    parameters.add(new JoyApiRequestParameter(param, _request.getParameter(param)));
            }
            Enumeration<String> attrs = _request.getAttributeNames();
            while (attrs.hasMoreElements()) {
                String attr = attrs.nextElement();
                parameters.add(new JoyApiRequestParameter(attr, _request.getAttribute(attr).toString()));
            }
            
        } catch (Exception e) {
            this.getLog().log(Level.SEVERE, "Error during URL treatment, exception : {0}", e.toString());
        }
    }
    
    public List<String> getActions() {
        return actions;
    }
    
    public String getAction(int index) {
        try {
            return actions.get(index);
        } catch (Exception e) { return null; }
    }
    
    public String getMainAction() {
        try {
            return actions.get(0);
        } catch (Exception e) { return null; }
    }
    
    public List<JoyApiRequestParameter> getParameters() {
        return parameters;
    }
    
    public JoyApiRequestParameter getParameter(int index) {
        try {
            return parameters.get(index);
        } catch (Exception e) { return null; }
    }
    
    public JoyApiRequestParameter getParameter(String name) {
        try {
            for (JoyApiRequestParameter param : parameters) 
                if (param.getName().equalsIgnoreCase(name))
                    return param;
        } catch (Exception e) { }
        return null;
    }
    
    public String getHttpMethod() {
        return httpMethod;
    }
    
    
}
