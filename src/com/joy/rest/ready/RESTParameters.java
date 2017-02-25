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
package com.joy.rest.ready;

import com.joy.Joy;
import com.joy.mvc.actionTypes.ActionTypeREST;

/**
 * returns the applications parameters
 * @author benoit
 */
public class RESTParameters extends ActionTypeREST {

    @Override
    public String restGet() {
        return Joy.PARAMETERS().getJson().toString();
    }
    
}
