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
package com.joy.api.beans;

import com.joy.JOY;
import com.joy.common.JoyClassTemplate;
import com.joy.json.JSONObject;

/**
 * This class encapsulate a value (json export). 
 * A value is put in a json with its name.
 * @author benoit
 */
public class JoyJsonSingle extends JoyClassTemplate {
    private String name;
    private Object value;

    /**
     * Return the value encapsulated
     * @return
     */
    public JSONObject getData() {
        try {
            return JOY.GET_JSON_VALUESET(name, value);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * return the Name of the value
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Name of the value
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * return the value
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set the value
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Initialisation
     */
    public JoyJsonSingle() {
        this.name = "";
        this.value = "";
    }

    /**
     * Return the Json object with name and value.
     * @param name
     * @param value
     */
    public JoyJsonSingle(String name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }
    
}
