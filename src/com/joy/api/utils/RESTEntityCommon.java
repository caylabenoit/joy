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
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the arguments pairs into a rest call
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class RESTEntityCommon extends ActionTypeREST {
    protected List<PairParameter> Pairs;
    
    public RESTEntityCommon() {
        super();
        Pairs = new ArrayList();
    }
    
    protected class PairParameter {
        private String Name;
        private String Value;

        public PairParameter(String Name, String Value) {
            this.Name = Name;
            this.Value = Value;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }
    }
    
    public int getPairsCount(int Index) {
        return Pairs.size();
    }
    
    public PairParameter getPair(int Index) {
        try {
            return Pairs.get(Index);
        } catch (Exception e) { return null; }
    }

    protected boolean CollectPairs(int firstParameterIndex) {
        int i = firstParameterIndex; // begins from the 3rd parameter
        boolean hasPair = true;
        
        try {
            while (hasPair) {
                String argName = getRestParameter(i++); //this.getStrArgumentValue("P" + i++);
                String argValue = getRestParameter(i++); //this.getStrArgumentValue("P" + i++);
                hasPair = (!argName.isEmpty() &&  !argValue.isEmpty());
                if (hasPair) {
                    Pairs.add(new PairParameter(argName, argValue));
                } 
            }
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * return a filtered entity object
     * @param entityName    entity name
     * @param firstParameterIndex index of the first parameter (not the entity name)
     * @return JSON Term's list
     */
    protected IEntity getFilteredEntity(String entityName, 
                                        int firstParameterIndex) {
        try {
            IEntity entity = this.getBOFactory().getEntity(entityName);

            // Manage Criterias for entity
            this.CollectPairs(firstParameterIndex);
            for (int i=0; i< this.getPairsCount(i); i++) {
                String ColName = this.getPair(i).getName().toUpperCase();
                if (ColName.equalsIgnoreCase("ROWCOUNT")) {
                    // Limit recors if specific field ROWCOUNT filled and > 0
                    try { 
                        int limit = Integer.valueOf(this.getPair(i).getValue());
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
                                ii= Integer.valueOf(this.getPair(i).getValue());
                            } catch (NumberFormatException e) { }
                            entity.field(ColName).setKeyValue(ii);
                            break;
                        case fieldFloat:
                            float j = 0;
                            try {
                                j= Float.valueOf(this.getPair(i).getValue());
                            } catch (NumberFormatException e) { }
                            entity.field(ColName).setKeyValue(j);
                            break;

                        case fieldDate:
                        case fieldString: 
                        default: 
                            entity.field(ColName).setKeyValue(this.getPair(i).getValue());
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
