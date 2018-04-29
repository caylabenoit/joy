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
package com.joy.api.ready;

import com.joy.api.utils.RESTEntityCommon;
import com.joy.C;

/**
 * Returns an antity result in JSON format with the filters in parameters (must be specified by pair).
 * Usage : [URL]/entity/[Entity]/[criteria 1]/[Value 1]/[criteria 2]/[Value 2]/...
 * Example : http://localhost:18080/dgm/rest/entity/[ENTITY NAME]/[PARAM NAME 1]/[PARAM VALUE 1]
 * Note : You can use the specific PARAM NAME : ROWCOUNT which limit the number of records.
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */

public class RESTEntity extends RESTEntityCommon {

    @Override
    public String restGet() {
        try {
            return this.getFilteredEntity(this.getCurrentRequest().getAction(1)).exp().toString();
        } catch (Exception e) {
            getLog().severe (e.toString());
            return C.JSON_EMPTY;
        }
    }
    
}
