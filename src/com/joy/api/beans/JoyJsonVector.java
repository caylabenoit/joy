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

/* Vector Format example
{
  "vector": [
    {
      "name": "TERM_TYPE_LIST",
      "value": {
        "itemcount": 6,
        "items": [
          {
            "name": "0",
            "value": "Unknown"
          },
          {
            "name": "5"
            "value": "Unknown"
          },
          ...
        ]
      }
    }
  ]
}
*/

import com.joy.JOY;
import com.joy.common.joyClassTemplate;
import com.joy.json.JSONArray;
import com.joy.json.JSONException;
import com.joy.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author benoit
 */
public class JoyJsonVector extends joyClassTemplate {
    private JSONArray items;

    public JoyJsonVector() {
        super();
        items = new JSONArray();
    }

    public JSONArray getItems() {
        return items;
    }

    public void setItems(JSONArray items) {
        this.items = items;
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
            finalObj.put("itemcount", items.length());
            finalObj.put("items", items);
            return finalObj;
            
        } catch (JSONException e) {
            return null;
        }
    }
    
    /**
     * Setup the vector object with a resultset content
     * @param rs            resultset
     * @param fieldKey      field value 
     * @param fieldLabel    field name
     */
    public void loadFromResultSet(ResultSet rs, String fieldKey, String fieldLabel) {
        try {
            // Builds the list of items
            while (rs.next()) {
                JoyJsonSingle single = new JoyJsonSingle(rs.getString(fieldLabel), rs.getString(fieldKey));
                this.addItem(single);
            }
            
        } catch (SQLException e) {
            getLog().severe(e.toString());
        }
    }
    
}
