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


import static com.joy.C.RESTFUL_ALREADY_EXIST;
import static com.joy.C.RESTFUL_NOT_FOUND;
import static com.joy.C.RESTFUL_NO_CONTENT;
import static com.joy.C.RESTFUL_OK;

import com.joy.common.filter.FilterCommon;
import com.joy.api.ActionTypeREST;
import com.joy.api.beans.JoyJsonPOSTReturn;
import static com.joy.api.beans.JoyJsonPOSTReturn.JoyEnumPOSTUpSertCodes.delete;
import static com.joy.api.beans.JoyJsonPOSTReturn.JoyEnumPOSTUpSertCodes.upsert;
import com.joy.common.state.JoyState;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import javax.servlet.ServletOutputStream;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * REST HTTP filter
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class FilterAPI extends FilterCommon
{

   /**
     * Manage a REST call request
     * http://localhost:18180/GovManagementTool/action?object=[tag] / objecttype=rest
     * http://localhost:18180/GovManagementTool/rest/[tag]/P1/P2/P3/...
     * @param state
     */
    @Override
    protected void process(JoyState state) {

        try {
            // Get call informations
            ApiConfigEntry myRestCall = new ApiConfigEntry(state.getAPIRequest().getMainAction(), state.getRestConfiguration());
            String resultREST = "";
            
            if (myRestCall.isSecure()) {
                if (!checkToken(state.getHttpAuthToken())) {
                    getLog().log(Level.SEVERE, "Authentication failed");
                    state.getCurrentResponse().setStatus(SC_UNAUTHORIZED);
                    return;
                }
            }
            
            state.getLog().log(Level.INFO, "REST action requested");
            ActionTypeREST actionRestObject = (ActionTypeREST) Class.forName(myRestCall.getClassName()).newInstance();
            actionRestObject.init(state);
            
            switch (state.getAPIRequest().getHttpMethod()) {
                case "DELETE": // Delete
                    JoyJsonPOSTReturn retDELETE = new JoyJsonPOSTReturn();
                    retDELETE.setUpdateType(delete);
                    resultREST = actionRestObject.restDelete(retDELETE); 
                    state.getCurrentResponse().setStatus(SC_OK);
                    if (resultREST.equalsIgnoreCase(RESTFUL_NOT_FOUND))
                        state.getCurrentResponse().setStatus(SC_NOT_FOUND);
                    resultREST = retDELETE.getJsonReturn().toString();
                    break;
                    
                case "POST": // Create or update
                    JoyJsonPOSTReturn retPOST = new JoyJsonPOSTReturn();
                    resultREST = actionRestObject.restPost(retPOST); 
                    retPOST.setUpdateType(upsert);
                    switch (resultREST) {
                        case RESTFUL_NOT_FOUND: state.getCurrentResponse().setStatus(SC_NOT_FOUND); break;
                        case RESTFUL_ALREADY_EXIST: state.getCurrentResponse().setStatus(SC_CONFLICT); break;
                        case RESTFUL_NO_CONTENT: state.getCurrentResponse().setStatus(SC_NO_CONTENT);break;
                        case RESTFUL_OK:
                        default: state.getCurrentResponse().setStatus(SC_OK);
                    }
                    resultREST = retPOST.getJsonReturn().toString();
                    break;
                    
                case "GET": // Get-read default
                default:
                    resultREST = actionRestObject.restGet();
                    state.getCurrentResponse().setStatus(SC_OK);
                    if (resultREST.equalsIgnoreCase(RESTFUL_NOT_FOUND))
                        state.getCurrentResponse().setStatus(SC_NOT_FOUND);
            }

            state.getCurrentResponse().setContentType (myRestCall.getMime());
            if (myRestCall.getMime().equalsIgnoreCase("unknown/unknown")) {
                // Force a file download
                state.getCurrentResponse().setHeader ("Content-Disposition", "attachment; filename=\"\"");
                ServletOutputStream outs = state.getCurrentResponse().getOutputStream();
                outs.print(resultREST);
                outs.flush();
                outs.close();
                
            } else {
                // Return classic flow (in json)
                PrintWriter out = state.getCurrentResponse().getWriter();
                out.print( resultREST );
                out.close();
            }
            
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            getLog().log(Level.SEVERE, "FilterAPI.process> IOException={0}", ex);
        }
        
        try {
            state.end();
        } catch (Exception ex) {
            getLog().log(Level.WARNING, "FilterAPI.process (Closing Joy State)> Exception={0}", ex);
        }
    }
    
}