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
package com.joy.api.beans;

import com.joy.JOY;
import com.joy.common.JoyClassTemplate;
import com.joy.json.JSONObject;

/**
 *
 * @author benoit
 */
public class JoyJsonSingle extends JoyClassTemplate {
    private String name;
    private Object value;

    /**
     *
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
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     *
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     *
     */
    public JoyJsonSingle() {
        this.name = "";
        this.value = "";
    }

    /**
     *
     * @param name
     * @param value
     */
    public JoyJsonSingle(String name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }
    
}
