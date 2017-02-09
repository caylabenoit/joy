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
import com.joy.mvc.Action;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class ActionTypeREST extends Action {
    
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
}
