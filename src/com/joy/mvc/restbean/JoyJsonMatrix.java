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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author benoit
 */
public class JoyJsonMatrix {
    private JSONArray rows;
    private JSONArray columns;
    
    public JoyJsonMatrix() {
        rows = new JSONArray();
        columns = new JSONArray();
    }
    
    public void addRow(JoyJsonVector obj) {
        rows.put(obj.getData());
    }

    public JSONArray getRows() {
        return rows;
    }

    public void setRows(JSONArray rows) {
        this.rows = rows;
    }

    public JSONObject getData() {
        try {
            JSONObject finalObj = new JSONObject();
            finalObj.put("count", rows.length());
            finalObj.put("rows", rows);
            finalObj.put("columns", columns);
            return finalObj;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Direct load a matrix into the result form from a resultset
     * @param rs    resultset
     * @return false if error
     */
    public boolean loadFromResultSet(ResultSet rs) {
        List<String> FieldNames = new ArrayList();
        int i;
        boolean hasNoError = true;
        
        try {
            // Get the ResultSet names list first
            ResultSetMetaData rsmd = rs.getMetaData(); 
            for(i=1; i <= rsmd.getColumnCount(); i++){ 
                FieldNames.add(rsmd.getColumnName(i));
                columns.put(rsmd.getColumnName(i));
            }

            // Load the matrix
            while (rs.next()) {
                JoyJsonVector columns = new JoyJsonVector();
                for(i=0; i < FieldNames.size(); i++){ 
                    columns.addItem(FieldNames.get(i), rs.getObject(FieldNames.get(i)));
                }
                this.addRow(columns);
            }
            
        } catch (SQLException e) {
            Joy.LOG().error(e);
            hasNoError = false;
        } 
        return hasNoError;
    }
}
