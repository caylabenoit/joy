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
package com.joy.api;

import com.joy.JOY;
import com.joy.bo.IEntity;
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import com.joy.api.beans.JoyJsonMatrix;
import com.joy.api.beans.JoyJsonSingle;
import com.joy.api.beans.JoyJsonVector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 *
 * @author benoit
 */
public class ActionRest extends Action {
    private JSONArray singles;
    private JSONArray vectors;
    private JSONArray matrixes;
    private JSONArray others;

    public ActionRest() {
        singles = new JSONArray();
        vectors = new JSONArray();
        matrixes = new JSONArray();
        others = new JSONArray();
    }
    
    
    /**
     * Add a single value set (= name + value)
     * @param name  name/label
     * @param value value object
     * @return false if error
     */
    public boolean addSingle(String name, Object value) {
        try {
            singles.put(JOY.GET_JSON_VALUESET(name, value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Add a single data object
     * @param obj
     * @return 
     */
    public boolean addSingle(JoyJsonSingle obj) {
        try {
            singles.put(obj.getData());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fill the single data with one record (add single by pair name/value)
     * @param entity Entity (contains one record)
     * @return 
     */
    public boolean addSingle(IEntity entity) {
        try {
            
            ResultSet rs = entity.select();
            if (rs.next()) {
                ResultSetMetaData rsmd = rs.getMetaData(); 
                for(int i=1; i <= rsmd.getColumnCount(); i++){ 
                    String fieldname = rsmd.getColumnName(i);
                    String fieldValue = rs.getString(fieldname);
                    this.addSingle(fieldname, fieldValue);
                }
            }
            this.getBOFactory().closeResultSet(rs);
            return true;
            
        } catch (Exception e) {
            getLog().severe(e.toString());
            return false;
        }
    }    
    
    /**
     * Add another kind of object into the other section
     * @param name  name/label
     * @param value value object
     * @return false if error
     */
    public boolean addOther(String name, Object value) {
        try {
            others.put(JOY.GET_JSON_VALUESET(name, value));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Return the JOY standard dataset which contains all the data in formatted json
     * @return 
     */
    public JSONObject getData() {
        JSONObject finalObject = new JSONObject();
        finalObject.put("single", singles);
        finalObject.put("vector", vectors);
        finalObject.put("matrix", matrixes);
        finalObject.put("other", others);
        finalObject.put("parameters", getState().getParameters().getJson());
        return finalObject;
    }
    
    /**
     * Return a JSON Object which contains all the information to display a combobox
     * @param jsonVectorName Json tag/label
     * @param entity        Entity name
     * @param fieldKey      Key field into the entity
     * @param fieldLabel    Label field into the entity
     * @return false if error
     */
    public boolean addVector(String jsonVectorName, String entity, String fieldKey, String fieldLabel) {
        try {
            IEntity ent = getBOFactory().getEntity(entity);
            ResultSet rs = ent.select();
            
            JoyJsonVector vector = new JoyJsonVector();
            vector.loadFromResultSet(rs, fieldKey, fieldLabel);
            this.getBOFactory().closeResultSet(rs);
            
            return this.addVector(jsonVectorName, vector);
            
        } catch (Exception e) {
            getLog().severe(e.toString());
            return false;
        }
    }
    
    /**
     * Add a vector
     * @param jsonVectorName Json tag/label
     * @param vector vector to add
     * @return 
     */
    public boolean addVector(String jsonVectorName, JoyJsonVector vector) {
        try {
            vectors.put(new JoyJsonSingle(jsonVectorName, vector.getData()).getData());
        } catch (Exception e) {
            getLog().severe(e.toString());
            return false;
        }
        return true;
    }
    
    public boolean addMatrix(String jsonMatrixName, JoyJsonMatrix matrix) {
        try {
            matrixes.put(new JoyJsonSingle(jsonMatrixName, matrix.getData()).getData());
        } catch (Exception e) {
            getLog().severe(e.toString());
            return false;
        }
        return true;
    }
    
    public boolean addMatrix(String jsonVectorName, IEntity entity) {
        try {
            ResultSet rs = entity.select();
            
            JoyJsonMatrix matrix = new JoyJsonMatrix();
            matrix.loadFromResultSet(rs);
            this.getBOFactory().closeResultSet(rs);
            
            return this.addMatrix(jsonVectorName, matrix);
            
        } catch (Exception e) {
            getLog().severe(e.toString());
            return false;
        }
    }
    
    /**
     * Return the REST parameter by getting data from the URL direclty
     * example : http://localhost:8080/mykiradata/api/bterm/1
     *  Param 1 : bterm
     *  Param 2 : 1 etc.
     * @param i parameter rank
     * @return parameter value
     */
    public String getRestParameter(int i) {
        return this.getURL().toString().split("/")[i+5];
    }
    
}
