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
package com.joy.etl;

import com.joy.Joy;
import com.joy.bo.BOFactory;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.joy.bo.IEntity;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class MappingSpecification {
    private List<FieldMap> FieldMaps;
    private String From;
    private String To;
    private String Id;
    private List<LookupMap> LookupMaps;
    private String Filter;

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String Filter) {
        this.Filter = Filter;
    }
    
    public String getFrom() {
        return From;
    }

    public void setFrom(String From) {
        this.From = From;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String To) {
        this.To = To;
    }
    
    public MappingSpecification(String id) {
        FieldMaps = new ArrayList();
        LookupMaps = new ArrayList();
        this.Id = id;
        Filter = "";
    }
    
    public void add(FieldMap field) {
        FieldMaps.add(field);
    }
    
    public void add(LookupMap lookup) {
        LookupMaps.add(lookup);
    }
    
    public StatMap process(BOFactory entities, Object... forceParams) {
        StatMap stats = new StatMap();
        ResultSet rsFrom;
        
        try {
            
            IEntity boTo = entities.getEntity(To);
            IEntity boFrom = entities.getEntity(From);
            if (boFrom == null)
                boFrom = entities.getEntity(From);
            List<FieldMap> CheckIfExists = new ArrayList();
            boolean InsertReq = true;

            Joy.log().debug("Map Entity " + boFrom + " to " + boTo);
            
            // Build the Check list field list before
            for (FieldMap field : this.FieldMaps) 
                if (field.isCheckIfExist())
                    CheckIfExists.add(field);
            
            // Select all data into the from entity
            if (!Filter.isEmpty()) 
                boFrom.addFilter(Filter);
            rsFrom = boFrom.select();
            
            while (rsFrom.next()) {
                // Manage single fields map first
                for (FieldMap map : FieldMaps) {    // go through all the fields to map
                    //Joy.log().debug("Map Field | " + map.getFieldNameFrom() + " --> " +map.getFieldNameTo());
                    if (!map.isKey()) {// normal map (not a key)
                        Object val = null;
                        if (!map.getFieldNameFrom().isEmpty()) {
                            // Target value (from the to resultset)
                            val = rsFrom.getObject(map.getFieldNameFrom()); 
                            
                        } else {
                            // get the given value
                            switch (map.getValue().toUpperCase()) {
                                case "SYSDATE": // system date
                                    val = new Date();
                                    break;
                                case "%1": // Given fixed parameter
                                    if (forceParams.length == 1)
                                        val = forceParams[0];
                                    else
                                        val = null;
                                    break;
                                default: // fixed value
                                    val = map.getValue();
                            }
                        }
                        if (!map.getDefaultIfNull().isEmpty() && val == null)
                            boTo.field(map.getFieldNameTo()).setValue(map.getDefaultIfNull());
                        else
                            boTo.field(map.getFieldNameTo()).setValue(val); 
                            
                    } else { // it's a key !
                        if (CheckIfExists.isEmpty()) { // No checks for this mapping, Calculate a new ID and direct insert
                            boTo.field(map.getFieldNameTo()).setNextIDValue();
                            InsertReq = true;
                        } else { // some checks requested, try to get the possible existing match in the "to entity"
                            IEntity boToCheck = entities.getEntity(To);
                            for (FieldMap field : CheckIfExists) { // build the checks conditions
                                boToCheck.field(field.getFieldNameTo()).setKeyValue(rsFrom.getObject(field.getFieldNameFrom()));
                            }
                            ResultSet rsCheck = boToCheck.select();
                            if (rsCheck.next()) { // Match found, set the key
                                boTo.field(map.getFieldNameTo()).setKeyValue(rsCheck.getObject(map.getFieldNameTo()));
                                InsertReq = false;
                            } else { // no match, create a new key
                                boTo.field(map.getFieldNameTo()).setNextIDValue();
                                InsertReq = true;
                            }
                            entities.closeResultSet(rsCheck);
                        }
                    }
                }
                
                // Manage lookups now
                for (LookupMap lookup : LookupMaps) {
                    IEntity boLookup = entities.getEntity(lookup.getEntityLookup());
                    // Add the lookup conditions (could have several keys)
                    for (FieldMap lookupCondition : lookup.getLookupConditions()) {
                        boLookup.field(lookupCondition.getFieldNameTo()).setKeyValue(rsFrom.getObject(lookupCondition.getFieldNameFrom()));
                    }
                    ResultSet rsLookup = boLookup.select(); // lookup execution
                    if (rsLookup.next()) { // retrieve lookup results
                        boTo.field(lookup.getTo()).setValue(rsLookup.getObject(lookup.getEntityLookupKey()));
                    }
                    entities.closeResultSet(rsLookup);
                }
                
                // insert or update
                if (InsertReq)
                    stats.incRowsInserted(boTo.insert());
                else
                    stats.incRowsUpdated(boTo.update());
                
            }
            entities.closeResultSet(rsFrom);
            return stats;
            
        } catch (Exception e) {
            Joy.log().error(e);
            stats.setFatalError(true);
            return stats;
        }
        
    }

}
