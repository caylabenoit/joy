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
package com.joy.tasks.filter;

import com.joy.JOY;
import com.joy.common.filter.FilterCommon;
import com.joy.common.state.JoyState;
import com.joy.tasks.ActionTypeTASK;
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class FilterTask extends FilterCommon {

    protected class ApiConfigEntry {
        private String name;
        private String className;

        public ApiConfigEntry(String name, JSONObject json) {
            JSONArray services = json.getJSONArray("services");
            for (Object tag : services) {
                JSONObject item = (JSONObject)tag;
                if (name.equalsIgnoreCase(item.getString("name"))) {
                    this.className = item.getString("class");
                    this.name = item.getString("name");
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

    }

    @Override
    protected void process(JoyState state) {
        boolean hasActiveSession = false;

        try {
            // Session management check
            HttpSession mySession = state.getCurrentRequest().getSession();
            hasActiveSession = (mySession.getAttribute("JOY_SESSION") != null);
            if (!state.getParameters().isNoLogin()) {
                if (!hasActiveSession) 
                return;
            }

            // Get call informations
            String[] uriParts = state.getCurrentRequest().getRequestURI().split("/");
            ApiConfigEntry myRestCall = new ApiConfigEntry(uriParts[3], state.getTaskConfiguration());
            getLog().fine("TASK action requested");
            ActionTypeTASK actionTaskObject = (ActionTypeTASK) Class.forName(myRestCall.getClassName()).newInstance();
            actionTaskObject.setJoyState(state);
            
            String myTaskName = uriParts[3];
            boolean result;
            PrintWriter out = null;
            // create a new task
            result = JOY.TASKS().newTask(myTaskName,
                                         myTaskName, 
                                         myRestCall.getClassName(),
                                         mySession, 
                                         state.getCurrentRequest(),
                                         state);
            out = state.getCurrentResponse().getWriter();
            out.print( (result ? "{\"result\":\"Success\"}" : "{\"result\":\"Failed\"}") );
            out.close();
            
        } catch (Exception ex) {
            getLog().severe( "Exception=" + ex);
        }
        state.end();
            
    }
    
}
