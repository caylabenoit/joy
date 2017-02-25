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

/**
 * RESTFul Specification :
 * POST 	Create 	201 (Created), 'Location' header with link to /customers/{id} containing new ID. 	
 *      404 (Not Found), 
 *      409 (Conflict) if resource already exists..
 * GET 	Read 	200 (OK), list of customers. Use pagination, sorting and filtering to navigate big lists. 	
 *      200 (OK), single customer. 
 *      404 (Not Found), if ID not found or invalid.
 * PUT 	Update/Replace 	404 (Not Found), unless you want to update/replace every resource in the entire collection. 	
 *      200 (OK) or 204 (No Content). 
 *      404 (Not Found), if ID not found or invalid.
 * PATCH 	Update/Modify 	404 (Not Found), unless you want to modify the collection itself. 	
 *      200 (OK) or 204 (No Content). 
 *      404 (Not Found), if ID not found or invalid.
 * DELETE 	Delete 	404 (Not Found), unless you want to delete the whole collection—not often desirable. 	
 *      200 (OK). 
 *      404 (Not Found), if ID not found or invalid.
 */
import static com.joy.C.*;
import com.joy.Joy;
import com.joy.bo.IEntity;
import com.joy.json.JSONArray;
import com.joy.json.JSONObject;
import com.joy.mvc.Action;
import com.joy.mvc.restbean.JoyJsonMatrix;
import com.joy.mvc.restbean.JoyJsonSingle;
import com.joy.mvc.restbean.JoyJsonVector;
import java.sql.ResultSet;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionTypeREST extends Action {
    private JSONArray singles;
    private JSONArray vectors;
    private JSONArray matrixes;
    private JSONArray others;

    public ActionTypeREST() {
        singles = new JSONArray();
        vectors = new JSONArray();
        matrixes = new JSONArray();
        others = new JSONArray();
    }
    
    /**
     * return Not found status
     * @return Not found status
     */
    protected String setStatusNotFound() {
        return RESTFUL_NOT_FOUND;
    }
    
    /**
     * return already exists status
     * @return already exists status
     */
    protected String setStatusAlreadyExist() {
        return RESTFUL_ALREADY_EXIST;
    }
    
    /**
     * return No Content status
     * @return No Content status
     */
    protected String setStatusNoContent() {
        return RESTFUL_NO_CONTENT;
    }
    
    /**
     * Read 	
     * @return 
     *      RESTFUL_NOT_FOUND if not found
     *      any other content (like ID) means OK
     */
    public String restGet() { 
        return this.setStatusNotFound();
    }
    
    /**
     * Update replace
     * @return 
     *      RESTFUL_NOT_FOUND if not found
     *      RESTFUL_NO_CONTENT if no content to return
     *      any other content (like ID) means OK
     */
    public String restPut() { 
        return this.setStatusNotFound();
    }
    
    /**
     * Create 	
     * @return 
     *      RESTFUL_NOT_FOUND if not found
     *      RESTFUL_ALREADY_EXIST id already exist
     *      any other content (like ID) means OK
     */
    public String restPost() { 
        return this.setStatusNotFound();
    }
    
    /**
     * Delete
     * @return 
     *      RESTFUL_NOT_FOUND ID not found
     *      any other content (like ID) means OK
     */
    public String restDelete() { 
        return this.setStatusNotFound();
    }
    
    /**
     * Add a single value set (= name + value)
     * @param name  name/label
     * @param value value object
     * @return false if error
     */
    public boolean addSingle(String name, Object value) {
        try {
            singles.put(Joy.GET_JSON_VALUESET(name, value));
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
     * Add another kind of object into the other section
     * @param name  name/label
     * @param value value object
     * @return false if error
     */
    public boolean addOther(String name, Object value) {
        try {
            others.put(Joy.GET_JSON_VALUESET(name, value));
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
        return finalObject;
    }
    
    /**
     * Return a JSON Object which contains all the information to display a combobox
     * @param jsonVectorName Json tag/label
     * @param entity        Entity name
     * @param fieldKey      Key field into the entity
     * @param fieldLabel    Label field into the entity
     * @param selected      Key value for the selected item
     * @return false if error
     */
    public boolean addVector(String jsonVectorName, String entity, String fieldKey, String fieldLabel, String selected) {
        try {
            IEntity ent = getBOFactory().getEntity(entity);
            ResultSet rs = ent.select();
            
            JoyJsonVector vector = new JoyJsonVector();
            vector.loadFromResultSet(rs, fieldKey, fieldLabel, selected);
            this.getBOFactory().closeResultSet(rs);
            
            return this.addVector(jsonVectorName, vector);
            
        } catch (Exception e) {
            Joy.LOG().error(e);
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
            Joy.LOG().error(e); 
            return false;
        }
        return true;
    }
    
    public boolean addMatrix(String jsonMatrixName, JoyJsonMatrix matrix) {
        try {
            matrixes.put(new JoyJsonSingle(jsonMatrixName, matrix.getData()).getData());
        } catch (Exception e) {
            Joy.LOG().error(e); 
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
            Joy.LOG().error(e);
            return false;
        }
    }
    
}
