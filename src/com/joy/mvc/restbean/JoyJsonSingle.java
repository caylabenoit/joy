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
package com.joy.mvc.restbean;

import com.joy.Joy;
import com.joy.json.JSONObject;

/**
 *
 * @author benoit
 */
public class JoyJsonSingle {
    private String name;
    private Object value;

    public JSONObject getData() {
        try {
            return Joy.GET_JSON_VALUESET(name, value);
        } catch (Exception e) {
            return null;
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public JoyJsonSingle() {
        this.name = "";
        this.value = "";
    }

    public JoyJsonSingle(String name, Object value) {
        this.name = name;
        this.value = value;
    }
    
}
