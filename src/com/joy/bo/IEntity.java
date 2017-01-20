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
package com.joy.bo;

import com.joy.bo.init.BOInitRecord;
import com.joy.common.ActionLogReport;
import com.joy.json.JSONObject;
import com.joy.providers.JoyDBProvider;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public interface IEntity {
    // Minimum interfaces
    /**
     * Set Entity Name
     * @param name Entity Name
     */
    public void setName(String name);           
    /**
     * Get Entity name 
     * @return Entity name
     */
    public String getName();
    /**
     * Set or change Entity type
     * @param boType ReadOnly or ReadWrite
     */
    public void setBoType(BOEntityType boType);
    /**
     * return the entity type
     * @return ReadOnly or ReadWrite
     */
    public BOEntityType getBOType();
    /**
     * Clone the entity
     * @return cloned entity
     */
    public Object clone();
    /**
     * Get the maximum number or record which can be returned after querying the DB
     * @return 
     */
    public int getLimitRecords();
    /**
     * Get the maximum number or record which can be returned after querying the DB
     * @param limitRecords Max records
     */
    public void setLimitRecords(int limitRecords);
    /**
     * Return the entity label
     * @return entity label
     */
    public String getLabel();
    /**
     * Set the entity label
     * @param Label new entity label
     */
    public void setLabel(String Label);
    /**
     * Add a new sort by usilg a field list 
     * @param fieldlist list of table/query fields to use for sorting
     */
    public void addSort(String... fieldlist);
    /**
     * Add a new filter by usilg a field list (become Where clause)
     * @param clauseList list of table/query fields to use for filtering
     */
    public void addFilter(String... clauseList);
    /**
     * Return true if there are some records inside the entity
     * @return true if records exists
     */
    public boolean hasRecord();
    public void useOnlyTheseFields(String... args);
    public void doNotUseTheseFields(String... args);
    public void useTheseFields(String... args);
    public void useNoFields();
    public void reset();    // reset all the contextuals parametrisation
    public void resetFilters();
    public void resetKeys();
    public void resetSorts();
    public void resetValues();
    public void insertDefaultRecords(); // insert into the entity the default records
    public void addDefaultRecord(BOInitRecord record);  // add a new default record
    public void setDB(JoyDBProvider con);
    public JoyDBProvider getDB();
    public void setDateFormat(String format);
    public boolean isDistinct();
    public void setDistinct(boolean Distinct);
    public List<BOField> fields();
    public BOField field(int index);
    public BOField field(String Name);
    public boolean isInitialized();
    public ResultSet select();
    public int count(boolean... filtering);
    public JSONObject exp();
    // Write interfaces
    public Collection<ActionLogReport> imp(JSONObject data, boolean removeAllBefore);
    public boolean delete();
    public void truncate();
    public int insert();
    public int update();
    public int upsert();
    public int getNewIDForField(String fieldname);
    // Read specific (query) interface
    public void setQuery(String Query);
    public String getQuery();
    // Group management
    public String getGroup();
    public void setGroup(String group);
}
