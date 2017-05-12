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
package com.joy.auth;

import com.joy.api.JoyApiRequest;
import com.joy.common.filter.FilterCommon;
import com.joy.common.state.JoyState;
import com.joy.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import com.joy.C;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class FilterAuthenticate extends FilterCommon {

    /**
     * Build the content of the future encrypted token
     * TO OVERRIDE
     * @param request input http request
     * @return string to encrypt
     */
    protected String buildTokenContent(JoyApiRequest request) {
        return C.DEFAULT_USER;
    }
    
    /**
     * Returns the user name
     * TO OVERRIDE
     * @param request input http request
     * @return user name
     */
    protected String getUser(JoyApiRequest request) {
        return C.DEFAULT_USER;
    }
    
    @Override
    protected void process(JoyState state) {
        String connectedUser = C.DEFAULT_USER;
        String token = C.TOKEN_EMPTY;
        JSONObject global = new JSONObject();
        
        try {
            if (checkLogin(state.getAPIRequest())) {
                connectedUser = getUser(state.getAPIRequest());
                token = this.encrypt(buildTokenContent(state.getAPIRequest()));
                global.put(C.TOKEN_STATUS_TAG, C.TOKEN_STATUS_OK);
            } else {
                token = C.TOKEN_EMPTY;
                global.put(C.TOKEN_STATUS_TAG, C.TOKEN_STATUS_KO); // si no login
            }
            global.put(C.TOKEN_USER_TAG, connectedUser);
            global.put(C.TOKEN_TOKEN_TAG, token);
            
        } catch (Exception e) {
            global.put(C.TOKEN_USER_TAG, C.DEFAULT_USER);
            global.put(C.TOKEN_TOKEN_TAG, C.TOKEN_EMPTY);
            global.put(C.TOKEN_STATUS_TAG, C.TOKEN_STATUS_KO); // si no login
            getLog().log(Level.SEVERE, "FilterAuthenticate.process> Exception={0}", e);
        }
        
        PrintWriter out;
        try {
            out = state.getCurrentResponse().getWriter();
            out.print( global.toString() );
            out.close();
        } catch (IOException ex) {
            getLog().log(Level.SEVERE, "FilterAuthenticate.process> IOException={0}", ex);
        }
    }

    /**
     * Check the login with the request entries
     * TO OVERRIDE
     * @param request JoyApiRequest request informations
     * @return true if login OK
     */
    protected boolean checkLogin(JoyApiRequest request) {
        return true; 
    }
    
}

