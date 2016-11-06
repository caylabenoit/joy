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

import com.joy.Joy;
import com.joy.json.JSONException;
import com.joy.json.JSONObject;
import com.joy.providers.JoyDBProvider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static com.joy.bo.BOEntityType.boReadWrite;
import com.joy.bo.init.BOInitRecord;
import com.joy.common.ActionLogReport;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOEntityReadOnly implements Cloneable, IEntity {
    
    protected static String JOY_QUERY_ALIAS = "JOYCUSTOM";
    
    protected List<BOField> fields;         // fields of the entity
    protected String name;                  // entity name
    protected String label;                 // entity label / name if no label specified
    protected String query;                 // entity query (not for tables)
    protected boolean distinct;             // clause distinct requested
    protected String dateFormat;            // default date format
    protected List<String> sortedFields;    // sorted field list
    protected List<String> filters;         // Filters (where clause) list
    protected JoyDBProvider dbConnection;   // DB connection
    protected BOEntityType boType;          // type of entity : table or query
    protected int limitRecords;             // limit the number of record collected

    @Override
    public String getQuery() {
        if (this.boType != boReadWrite) 
            return query;
        else
            return "SELECT * FROM " + this.name;   
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setBoType(BOEntityType boType) {
        this.boType = boType;
    }

    @Override
    public void setQuery(String Query) {
        this.query = Query;
    }
    
    @Override
    public JoyDBProvider getDB() {
        return this.dbConnection;
    }
    
    /**
     * Check i a record already exists in the entity
     * Take in account the Keys & key values
     * @return true if record exists
     */
    public boolean checkIfExists() {
        boolean retval;
        ResultSet rsSource = this.select();
        try { 
            retval = (rsSource.next());
        } catch (SQLException ex) {
            retval = false;
        }
        this.dbConnection.closeResultSet(rsSource);
        return retval;
    }
    
    @Override
    public Object clone() {
        BOEntityReadOnly myClone = null;
        try {
            myClone = (BOEntityReadOnly)super.clone();
            List<BOField> listField = new ArrayList();
            for (BOField field : fields) {
                listField.add((BOField)field.clone());
            }
            myClone.fields = listField;
            
        } catch(CloneNotSupportedException cnse) {
            Joy.log().error(cnse);
        }
        return myClone;
    }
    
    /**
     * Set the dbConnection to the Entity and its fields
     * @param con 
     */
    @Override
    public void setDB(JoyDBProvider con) {
        dbConnection = con;
        for (BOField field : fields) {
            field.setDB(con);
        }
    }
    
    @Override
    public BOEntityType getBOType() {
        return boType;
    }

    @Override
    public int getLimitRecords() {
        return limitRecords;
    }

    @Override
    public void setLimitRecords(int limitRecords) {
        this.limitRecords = limitRecords;
    }
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String Label) {
        this.label = Label;
    }
    
    @Override
    public void addSort(String... fieldlist) {
        for (int i=0; i < fieldlist.length; i++) 
            sortedFields.add(fieldlist[i]);
    }
    
    @Override
    public void addFilter(String... clauseList) {
        for (int i=0; i < clauseList.length; i++) 
            filters.add(clauseList[i]);
    }
    
    @Override
    public void resetFilters() {
        filters.clear();
    }
    
    public List<String> sorts() {
        return sortedFields;
    }

    public List<String> filters() {
        return filters;
    }
    
    @Override
    public void resetSorts() {
        sortedFields.clear();
    }

    /**
     * Set the liste field for use
     * @param touse Use or not use the liste fields
     * @param args field list
     */
    private void useOrNotTheseFields(boolean touse, String... args) {
        for (BOField field : fields) { // go through all the fields
            int i = 0;
            while(i < args.length) { // go through the dynamic list
                if (args[i++].equalsIgnoreCase(field.getColumnName())) {
                    // field match with one argument list element
                    if (touse)
                        field.useThisField();
                    else
                        field.doNotUseThisField();
                }
            }
        }
    }
    
    @Override
    public void useOnlyTheseFields(String... args) {
        this.useNoFields();
        useOrNotTheseFields(true, args);
    }
    
    @Override
    public void doNotUseTheseFields(String... args) {
        useOrNotTheseFields(false, args);
    }
    
    @Override
    public void useTheseFields(String... args) {
        useOrNotTheseFields(true, args);
    }
    
    @Override
    public void useNoFields() {
        for (BOField field : fields) 
            field.doNotUseThisField();
    }
    
    @Override
    public void reset() {
        resetValues();
        resetKeys();
        useAllFields();
        resetSorts();
        resetFilters();
    }
    
    @Override
    public void setDateFormat(String format) {
        dateFormat = format;
    }
    
    @Override
    public void resetValues() {
        for (BOField field : fields) 
            field.setValue(null);
    }
    
    @Override
    public void resetKeys() {
        for (BOField field : fields) 
            field.unsetKey();
    }

    private void useAllFields() {
        for (BOField field : fields) 
            field.useThisField();
    }
    
    @Override
    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public void setDistinct(boolean Distinct) {
        this.distinct = Distinct;
    }
    
    public BOEntityReadOnly() {
        this.sortedFields =  new ArrayList();
        this.fields = new ArrayList();
        this.filters = new ArrayList();
        this.distinct = false;
        this.name = "";
        this.dateFormat = Joy.parameters().getJoyDefaultDateFormat();
        this.limitRecords = -1;
        this.query = "";
        this.name = "";
    }
    
    @Override
    public List<BOField> fields() {
        return fields;
    }
    
    @Override
    public BOField field(String Name) {
        BOField retField = null;
        for (BOField field : fields) 
            if (field.getLabel().equalsIgnoreCase(Name) || field.getColumnName().equalsIgnoreCase(Name)) 
                retField = field;
        if (retField == null)
            Joy.log().error("Field " + Name + " not found.");
        return retField;
    }

    @Override
    public BOField field(int index) {
        BOField retField = fields.get(index);
        if (retField == null) {
            Joy.log().error("Field with index " + index + " not found.");
        }
        return retField;
    }
    
    @Override
    public boolean isInitialized() {
        return (!fields.isEmpty() && !name.equals(""));
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * Exporte la table dans un objet JSON
     * @param rs Resultset 
     * @return JSON content
     */
    protected JSONObject exportResultSet(ResultSet rs)  {
        try {
            Collection<JSONObject> lines = new ArrayList<>();
            while (rs.next()) {
                JSONObject oneLine = new JSONObject();
                for (int i=1; i <= rs.getMetaData().getColumnCount() ; i++) {
                    String colName = rs.getMetaData().getColumnName(i).toUpperCase();
                    Object val = rs.getObject(colName);
                    //if (val != null) {
                    if (val == null) 
                        val = "";
                    JSONObject valueset = new JSONObject();
                    valueset.put("name", colName);
                    valueset.put("value", val.toString());
                    oneLine.put(colName, valueset);
                    //}
                }
                JSONObject jsonOneLine = new JSONObject();
                jsonOneLine.put("row", oneLine);
                lines.add(jsonOneLine);
            }
            
            JSONObject all = new JSONObject();
            all.put(this.name, lines);
            Joy.log().info("JSON Export Successul.");
            return all;
            
        } catch (SQLException | JSONException e) {
            Joy.log().error(e);
        }
        return null;
    }
    
    /**
     * Set the Values into the myQuery
     * @param ps JDBC PreparedStatement object
     * @param field field/column to set
     * @param i Order of the ? in the myQuery (PreparedStatement)
     */
    protected void setValue(PreparedStatement ps, 
                            BOField field, 
                            int i) {
        try {
            if (field.getValue() == null)
                ps.setObject(i, null); 
            else
                switch (field.dataType) {
                    case fieldBoolean: 
                    case fieldInteger: 
                        try {
                            ps.setInt(i, (int)field.getValue()); 
                        } catch (Exception e) {
                            ps.setInt(i, Integer.valueOf(field.getValue().toString())); 
                        }
                        break;
                    case fieldFloat: 
                        try {
                            ps.setFloat(i, (float)field.getValue()); 
                        } catch (Exception e) {
                            ps.setFloat(i, Float.valueOf(field.getValue().toString())); 
                        }
                        break;
                    case fieldDate: 
                        try {
                            ps.setDate(i, new java.sql.Date(((java.util.Date)field.getValue()).getTime())); 
                        } catch (Exception e) {
                            SimpleDateFormat formatter = new SimpleDateFormat(this.dateFormat);
                            try {
                                java.util.Date dt = formatter.parse(field.getValue().toString());
                                java.sql.Date sqlDate = new java.sql.Date(dt.getTime());
                                ps.setDate (i,  sqlDate);
                            } catch (ParseException ex) { ps.setDate(i, null);  }
                        }
                        
                        break;
                    case fieldString: 
                    default: 
                        ps.setString(i, field.getValue().toString()); 
                        break;
                }
        } catch (SQLException e) {
            Joy.log().error(e);
        }
    }
    
    /**
     * Set all the myQuery values into the PreparedStatement (? replacement)
     * @param ps JDBC PreparedStatement
     * @param query Query & values container
     */
    protected void setQueryValues(PreparedStatement ps, 
                                BOQueryExecution query) {
        int i=1;
        for (BOField obj : query.getValues()) {
            Joy.log().debug("Connection: " + dbConnection + " | SQL '?' Value order (" + i + ") replaced by [" + obj.getValue() + "]" );
            setValue(ps, obj, i++);
        }
    }
    
    /**
     * Build the SQL SELECT part of the Query
     * @param countonly
     * @return 
     */
    protected String getSQLSelect(boolean countonly) {
        String sql = "SELECT ";
        
        // distinct
        if (distinct) sql+= " DISTINCT ";
        
        // fields or count(*)
        if (!countonly) {
            for (BOField field : fields) 
                if (!field.isNotUsed())
                    sql += field.getColumnName() + ",";
            sql = sql.substring(0, sql.length() - 1);
        } else
            sql += " COUNT(*) ";
        
        return sql;
    }
    
    /**
     * Build the SQL FROM part of the Query
     * @param pFrom
     * @return 
     */
    protected String getSQLFrom(String pFrom) {
        String myFrom = pFrom;
        if (this.boType != boReadWrite) 
            myFrom = "(" + query + ") " + JOY_QUERY_ALIAS;
        return " FROM " + myFrom;
    }
    
    /**
     * Build the SQL WHERE part of the Query
     * @param myQuery
     * @param filtering
     * @return 
     */
    protected String getSQLWhere(BOQueryExecution myQuery, boolean filtering) {
        // Add the filtering clause
        String sql = " WHERE 1=1";
        if (filtering) { // Filter on Keys uniquely
            for (BOField field : fields) 
                if (field.isKey()) {
                    sql += " AND " + field.getColumnName() + " = ?";
                    myQuery.addValue(field);
                }
            // Explicit filters specified ...
            if (!filters.isEmpty()) 
                for (String filter : filters)
                    sql += " AND " + filter;
        }
        return sql;
    }
    
    /**
     * Build the SQL ORDER BY part of the Query
     * @param sorted
     * @return 
     */
    protected String getSQLOrderBy(boolean sorted) {
        // Add the  Order By clause
        if (sorted)
            return addSQLSort();
        return "";
    }
    
    /**
    * Build a Select SQL myQuery. This basic query does not take in account the record limitations (specific to each DB)
    * @param filtering define a filtered myQuery based on the specified keys
    * @param From Table or Query 
    * @param sorted Define a myQuery with sort (order by)
    * @param countonly if true, define a Count(*) myQuery (do not use the select fields in that case)
    * @return SQL myQuery
    */
    protected BOQueryExecution getSelectQuery(boolean filtering, 
                                              boolean sorted,
                                              boolean countonly,
                                              String From) {

        String sql = "";
        BOQueryExecution myQuery = new BOQueryExecution();
        sql += getSQLSelect(countonly);
        sql += getSQLFrom(From);
        sql += getSQLWhere(myQuery, filtering);
        sql += getSQLOrderBy(sorted);
        Joy.log().debug("SQL generated: " + sql);
        myQuery.setSQL(sql);
        return myQuery;
    }
    
    /**
     * Add order by clause to the SQL statement
     * @return part of SQL statement order by ...
     */
    protected String addSQLSort() {
        String sql = "";
        // Ajout de la clause Order By
        if (!sortedFields.isEmpty()) {
            sql += " ORDER BY ";
            for (String sort : sortedFields) 
                sql += sort + ",";
            sql = sql.substring(0, sql.length() - 1);
            Joy.log().debug("Order By generated: " + sql);
        }
        return sql;
    }

    
    /**
     * Get a ResultSet from a Select with filtering (based on keys and explicits filters
     * @return ResultSet
     */
    @Override
    public ResultSet select()  {
        try {
            BOQueryExecution lQuery = getSelectQuery(true, true, false, name);
            PreparedStatement ps = dbConnection.prepareSQL(lQuery.getSQL());
            setQueryValues(ps, lQuery);
            return ps.executeQuery();
        } catch (SQLException ex) {
            Joy.log().error(ex);
        }
        return null;
    }
    
    /**
     * Return the number of rows of the table (no filter)
     * @param filtering
     * @return number of rows
     */
    @Override
    public int count(boolean... filtering) {
        try {
            boolean withfilter = false;
            if (filtering.length == 1)
                withfilter = filtering[0];
            int result = 0;
            BOQueryExecution lQuery = getSelectQuery(withfilter, true, true, name);
            ResultSet rs = dbConnection.getResultSet(lQuery.getSQL());
            if (rs.next()) {
                result = rs.getInt(1);
            }
            dbConnection.closeResultSet(rs);
            
            return result;
        } catch (SQLException ex) {
            Joy.log().error(ex);
            return -1;
        }
    }

    /**
     * export all filtered data into a JSON Object
     * @return JSONObject formatted object
     */
    @Override
    public JSONObject exp()  {
        try {
            BOQueryExecution lQuery = getSelectQuery(true, true, false, name);
            PreparedStatement ps = dbConnection.prepareSQL(lQuery.getSQL());
            setQueryValues(ps, lQuery);
            
            ResultSet rs = ps.executeQuery();
            
            return exportResultSet(rs);
        } catch (SQLException ex) {
            Joy.log().error(ex);
        }
        return null;
    }

    /**
     * Return true if there are some records inside the entity
     * @return true if records exists
     */
    public boolean hasRecord() {
        ResultSet rs = this.select();
        boolean retVal = false;
        
        try {
            retVal = rs.next();
        } catch (SQLException ex) {}
        this.dbConnection.closeResultSet(rs);
        
        return retVal;
    }
    
    @Override
    public void insertDefaultRecords() {}

    @Override
    public Collection<ActionLogReport> imp(JSONObject data, boolean removeAllBefore) {
        return null;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public void truncate() {}

    @Override
    public int insert() {
        return 0;
    }

    @Override
    public int update() {
        return 0;
    }

    @Override
    public int upsert() {
        return 0;
    }

    @Override
    public void addDefaultRecord(BOInitRecord record) {}
}
