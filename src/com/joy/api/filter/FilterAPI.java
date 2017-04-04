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
package com.joy.api.filter;

import com.joy.common.filter.FilterCommon;
import static com.joy.C.RESTFUL_ALREADY_EXIST;
import static com.joy.C.RESTFUL_NOT_FOUND;
import static com.joy.C.RESTFUL_NO_CONTENT;
import com.joy.api.ActionTypeREST;
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * REST HTTP filter
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class FilterAPI extends FilterCommon
{
    
    protected class ApiConfigEntry {
        private String name;
        private String className;
        private String mime;

        public ApiConfigEntry(String name, JSONObject json) {
            JSONArray services = json.getJSONArray("services");
            for (Object tag : services) {
                JSONObject item = (JSONObject)tag;
                if (name.equalsIgnoreCase(item.getString("name"))) {
                    this.className = item.getString("class");
                    this.name = item.getString("name");
                    this.mime = item.getString("mime");
                    return;
                }
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMime() {
            return mime;
        }

        public void setMime(String mime) {
            this.mime = mime;
        }
    }

   /**
     * Manage a REST call request
     * http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=rest
     * http://localhost:18180/GovManagementTool/rest/[tag]/P1/P2/P3/...
     * @param request
     * @param response
     */
    @Override
    protected void process(HttpServletRequest request,
                           HttpServletResponse response) {

        try {
            // Get call informations
            String[] uriParts = request.getRequestURI().split("/");
            ApiConfigEntry myRestCall = new ApiConfigEntry(uriParts[3], getState().getRestConfiguration());
            String resultREST = "";
            
            getState().getLog().log(Level.INFO, "REST action requested");
            ActionTypeREST actionRestObject = (ActionTypeREST) Class.forName(myRestCall.getClassName()).newInstance();
            actionRestObject.init(getState());
            
            switch (request.getMethod()) {
                case "PUT": // Update-Replace
                    resultREST = actionRestObject.restPut(); 
                    response.setStatus(SC_OK);
                    if (resultREST.equalsIgnoreCase(RESTFUL_NOT_FOUND))
                        response.setStatus(SC_NOT_FOUND);
                    else if (resultREST.equalsIgnoreCase(RESTFUL_NO_CONTENT))
                        response.setStatus(SC_NO_CONTENT);
                    break;
                    
                case "DELETE": // Delete
                    resultREST = actionRestObject.restDelete(); 
                    response.setStatus(SC_OK);
                    if (resultREST.equalsIgnoreCase(RESTFUL_NOT_FOUND))
                        response.setStatus(SC_NOT_FOUND);
                    break;
                    
                case "POST": // Create
                    resultREST = actionRestObject.restPost(); 
                    response.setStatus(SC_OK);
                    if (resultREST.equalsIgnoreCase(RESTFUL_NOT_FOUND))
                        response.setStatus(SC_NOT_FOUND);
                    else if (resultREST.equalsIgnoreCase(RESTFUL_ALREADY_EXIST))
                        response.setStatus(SC_CONFLICT);
                    break;
                    
                case "GET": // Get-read default
                default:
                    resultREST = actionRestObject.restGet();
                    response.setStatus(SC_OK);
                    if (resultREST.equalsIgnoreCase(RESTFUL_NOT_FOUND))
                        response.setStatus(SC_NOT_FOUND);
            }

            response.setContentType (myRestCall.getMime());
            if (myRestCall.getMime().equalsIgnoreCase("unknown/unknown")) {
                // Force a file download
                response.setHeader ("Content-Disposition", "attachment; filename=\"\"");
                ServletOutputStream outs = response.getOutputStream();
                outs.print(resultREST);
                outs.flush();
                outs.close();
                
            } else {
                // Return classic flow (in json)
                PrintWriter out = response.getWriter();
                out.print( resultREST );
                out.close();
            }
            actionRestObject.endOfWork();
            
        } catch (Exception ex) {
            getState().getLog().log(Level.SEVERE, "IOException=" + ex);
        }
        getState().getEntities().End();
    }
    
}