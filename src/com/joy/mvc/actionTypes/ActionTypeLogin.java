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
package com.joy.mvc.actionTypes;

import com.joy.C;
import com.joy.mvc.Action;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionTypeLogin extends Action{
    
    public String request() {
        return C.ACTION_TAG_LOGIN_REQUEST;
    }
    
    public String login() {
        //String User = this.getStrArgumentValue("user");
        //String Password = this.getStrArgumentValue("password");
        return C.ACTION_SUCCESS;
    }
    
    public String logout() {
        return C.ACTION_SUCCESS;
    }
}
