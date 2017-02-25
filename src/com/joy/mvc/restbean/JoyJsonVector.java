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
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author benoit
 */
public class JoyJsonVector {
    private JSONArray items;
    private String selected;

    public JoyJsonVector() {
        items = new JSONArray();
        selected = "";
    }

    public JSONArray getItems() {
        return items;
    }

    public void setItems(JSONArray items) {
        this.items = items;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
    
    public void addItem(JoyJsonSingle obj) {
        items.put(obj.getData());
    }
    
    public void addItem(String name, Object value) {
        addItem(new JoyJsonSingle(name, value));
    }
    
    public JSONObject getData() {
        try {
            JSONObject finalObj = new JSONObject();
            finalObj.put("selected", selected);
            finalObj.put("count", items.length());
            finalObj.put("list", items);
            return finalObj;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Setup the vector object with a resultset content
     * @param rs            resultset
     * @param fieldKey      field value 
     * @param fieldLabel    field name
     * @param selected      field value selected
     */
    public void loadFromResultSet(ResultSet rs, String fieldKey, String fieldLabel, String selected) {
        try {
            // Builds the list of items
            while (rs.next()) {
                JoyJsonSingle single = new JoyJsonSingle(rs.getString(fieldLabel), rs.getString(fieldKey));
                this.addItem(single);
            }
            this.selected = selected;
            
        } catch (SQLException e) {
            Joy.LOG().error(e);
        }
    }
    
}
