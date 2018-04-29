/*
 * Copyright (C) 2017 benoit
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
package com.joy.api.ready;

import com.joy.C;
import com.joy.JOY;
import com.joy.auth.JoyAuthCookie;
import com.joy.json.JSONObject;
import com.joy.json.JSONArray;
import com.joy.common.parameters.JoyParameterFileMenu;

/**
 *
 * @author benoit
 */
public class RESTAppContext extends RESTParameters {

    @Override
    public String restGet() {
        JSONObject all = new JSONObject();
        
        // Application parameters
        all.put("parameters", this.getState().getAppParameters().getJson()); 
        
        // Menus
        JSONArray menus = new JSONArray();
        for (JoyParameterFileMenu menuFile : this.getState().getAppParameters().getMenuFiles()) {
            menus.put(JOY.GET_JSON_VALUESET(menuFile.getName(), 
                                            new JSONObject(JOY.FILE_TO_STRING(menuFile.getFilename()))));
        }
        all.put("menus", menus);

        // Navigation
        JSONObject navi = new JSONObject(JOY.FILE_TO_STRING(this.getState().getAppParameters().getNaviFile()));
        all.put("navi", navi);
        
        // Session
        JSONObject session = new JSONObject();
        JoyAuthCookie myToken = new JoyAuthCookie(this.getState().getHttpAuthToken());
        session.put(C.TOKEN_PKEY_TAG, myToken.getPublicKey());
        session.put(C.TOKEN_TOKEN_TAG, myToken.getCryptedToken());
        session.put(C.TOKEN_STATUS_TAG, !myToken.getCryptedToken().isEmpty());
        all.put("session", session);

        return all.toString();
    }

}
