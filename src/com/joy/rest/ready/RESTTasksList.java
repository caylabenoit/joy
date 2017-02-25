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
package com.joy.rest.ready;

import com.joy.Joy;
import com.joy.mvc.actionTypes.ActionTypeREST;

/**
 * Returns a JSON with all the tasks
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class RESTTasksList extends ActionTypeREST {

    @Override
    public String restGet() {
        int limit = 0;
        try {
            limit = this.getIntArgumentValue("P1");
        } catch (Exception e) {}
        
        try {
            return Joy.TASKS().getJSONTasksDesc(limit); 
        } catch (Exception e) {
            Joy.LOG().fatal(e);
            return "";
        }
    }
    
}
