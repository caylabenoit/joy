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
package com.joy.mvc;

import com.joy.C;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionRequest {
    private String object;
    private String objectType;
    private String javaclass;
    private List redirects;
    private boolean isInitialized;
    private String flowType;
    
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public boolean getIsInitialized() {
        return isInitialized;
    }

    public void setisInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }
    
    public ActionRequest() {
        redirects = new ArrayList();
        isInitialized = false;
        flowType = C.FLOWTYPE_STREAM;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String FlowType) {
        if (FlowType == null)
            this.flowType = C.FLOWTYPE_STREAM;
        else 
            this.flowType = (FlowType.isEmpty() ? C.FLOWTYPE_STREAM : FlowType);
    }

    public String getJavaclass() {
        return javaclass;
    }

    public void setJavaclass(String javaclass) {
        this.javaclass = javaclass;
    }

    public List getRedirects() {
        return redirects;
    }

    public void setRedirects(List redirects) {
        this.redirects = redirects;
    }
    
    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
    
    public ActionRedirect getRedirectFromTag(String _Tag) {
        Iterator redirsIterator = redirects.iterator();
        while(redirsIterator.hasNext()) {
            ActionRedirect redirection = (ActionRedirect)redirsIterator.next();
            if (redirection.getTag().equalsIgnoreCase(_Tag)) {
                return redirection;
            }
        }
        return null;
    }
    
    public void addRedirection(ActionRedirect redir) {
        redirects.add(redir);
    }
}
