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
package com.joy.api.utils;

import com.joy.bo.BOFieldType;
import com.joy.bo.IEntity;
import com.joy.api.ActionTypeREST;

/**
 * This class manages the arguments pairs into a rest call
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class RESTEntityCommon extends ActionTypeREST {

    public RESTEntityCommon() {
        super();
    }
    
    /**
     * Return a filtered entity object
     * The URL must have the entity name just after api (ex. http://.../api/[entity name]/...
     * Parameters are used to filter the entity
     * @return JSON Term's list
     */
    protected IEntity getFilteredEntity(String entityName) {
        
        try {
            IEntity entity = this.getBOFactory().getEntity(entityName);

            // Manage Criterias for entity
            for (int i=0; i< this.getCurrentRequest().getParameters().size(); i++) {
                String ColName = this.getCurrentRequest().getParameter(i).getName();
                
                if (ColName.equalsIgnoreCase("ROWCOUNT")) {
                    // Limit recors if specific field ROWCOUNT filled and > 0
                    try { 
                        int limit = Integer.valueOf(this.getCurrentRequest().getParameter(i).getValue());
                        if (limit > 0)
                            entity.setLimitRecords(limit);
                    } catch (NumberFormatException e) {}
                        
                } else {
                    // Other field filter else ...
                    BOFieldType colType = entity.field(ColName).getFieldType();
                    switch (colType) {
                        case fieldBoolean: 
                        case fieldInteger: 
                            int ii = 0;
                            try {
                                ii= Integer.valueOf(this.getCurrentRequest().getParameter(i).getValue());
                            } catch (NumberFormatException e) { }
                            entity.field(ColName).setKeyValue(ii);
                            break;
                        case fieldFloat:
                            float j = 0;
                            try {
                                j= Float.valueOf(this.getCurrentRequest().getParameter(i).getValue());
                            } catch (NumberFormatException e) { }
                            entity.field(ColName).setKeyValue(j);
                            break;

                        case fieldDate:
                        case fieldString: 
                        default: 
                            entity.field(ColName).setKeyValue(this.getCurrentRequest().getParameter(i).getValue());
                    }
                }
            }
            return entity;
            
        } catch (Exception e) {
            getLog().severe(e.toString());
            return null;
        }
    }
    
}
