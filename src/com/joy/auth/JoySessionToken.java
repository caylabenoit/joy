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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoit Cayla (benoit@famillecayla.fr)
 */
public class JoySessionToken {
    
    private class JoyTokenData {
        private String name;
        private String value;

        public JoyTokenData() {
            this.name = "";
            this.value = "";
        }

        public JoyTokenData(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }       
    }
    
    private List<JoyTokenData> tokenData;
    
    
    public JoySessionToken() {
        tokenData = new ArrayList();
    }
    
    public void add(String name, String value) {
        tokenData.add(new JoyTokenData(name, value));
    }
    
    public boolean checkCookie(String cookie) {
        return false;
    }
    
    
    
    
}
