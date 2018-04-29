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
import java.io.PrintWriter;
import java.util.logging.Level;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class FilterTask extends FilterCommon {

    /**
     *
     * @param state
     */
    @Override
    protected void process(JoyState state) {
        boolean hasActiveSession = false;

        try {
            if (!state.getAppParameters().isNoLogin()) {
                if (!hasActiveSession) 
                return;
            }

            // Get call informations
            taskConfigEntry myTaskCall = new taskConfigEntry(state.getAPIRequest().getMainAction(), state.getTaskConfiguration());
            if (myTaskCall.isSecure()) {
                if (!checkToken(state)) {
                    getLog().log(Level.SEVERE, "Authentication failed");
                    state.getCurrentResponse().setStatus(SC_UNAUTHORIZED);
                    return;
                }
            }
            
            getLog().fine("TASK action requested");
            ActionTypeTASK actionTaskObject = (ActionTypeTASK) Class.forName(myTaskCall.getClassName()).newInstance();
            actionTaskObject.setJoyState(state);
            
            String myTaskName = state.getAPIRequest().getMainAction();
            boolean result;
            PrintWriter out = null;
            // create a new task
            result = JOY.TASKS().newTask(myTaskName, 
                                         myTaskCall.getClassName(),
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
