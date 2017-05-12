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

import com.joy.common.JoyClassTemplate;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoyAuthToken extends JoyClassTemplate {
    private String user;
    private int status;
    private String token;

    public String getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public JoyAuthToken(String globalToken) {
        String[] tokenparsed = globalToken.split("\\|");
        
        if (tokenparsed.length == 3) {
            try {
                this.status = Integer.valueOf(tokenparsed[0]);
            } catch (Exception e) { this.status = 0; }
            this.user = tokenparsed[1];
            this.token = tokenparsed[2];
        }
        else {
            this.status = 0;
            this.user = "Anonymous";
            this.token = "";
        }
    }
    
    
}
