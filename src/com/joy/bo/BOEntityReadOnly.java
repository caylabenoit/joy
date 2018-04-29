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
import static com.joy.bo.BOEntityRWType.boReadWrite;
import com.joy.bo.init.BOInitRecord;
import com.joy.common.ActionLogReport;
import com.joy.api.beans.JoyJsonMatrix;
import com.joy.api.beans.JoyJsonVector;
import com.joy.common.JoyClassTemplate;
/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOEntityReadOnly extends JoyClassTemplate implements IEntity {
    
    /**
     *
     */
    protected static String JOY_QUERY_ALIAS = "JOYCUSTOM";
    
    /**
     *
     */
    protected List<BOField> fields;         // fields of the entity

    /**
     *
     */
    protected String name;                  // entity name

    /**
     *
     */
    protected String label;                 // entity label / name if no label specified

    /**
     *
     */
    protected String query;                 // entity query (not for tables)

    /**
     *
     */
    protected boolean distinct;             // clause distinct requested

    /**
     *
     */
    protected String dateFormat;            // default date format

    /**
     *
     */
    protected List<String> sortedFields;    // sorted field list

    /**
     *
     */
    protected List<String> filters;         // Filters (where clause) list

    /**
     *
     */
    protected JoyDBProvider dbConnection;   // DB connection

    /**
     *
     */
    protected BOEntityRWType boType;          // type of entity : table or query

    /**
     *
     */
    protected int limitRecords;             // limit the number of record collected
    
    /**
     *
     * @return
     */
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
    public void setBoType(BOEntityRWType boType) {
        this.boType = boType;
    }

    /**
     *
     * @param Query
     */
    @Override
    public void setQuery(String Query) {
        this.query = Query;
    }
    
    /**
     *
     * @return
     */
    @Override
    public JoyDBProvider getDB() {
        return this.dbConnection;
    }
    
    /**
     * Check i a record already exists in the entity
     * Take in account the Keys and key values
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
    
    /**
     * Set the dbConnection to the Entity and its fields
     * @param con 
     */
    @Override
    public void setDB(JoyDBProvider con) {
        dbConnection = con;
    }
    
    @Override
    public BOEntityRWType getBOType() {
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
    
    /**
     *
     */
    @Override
    public void resetFilters() {
        filters.clear();
    }
    
    /**
     *
     * @return
     */
    public List<String> sorts() {
        return sortedFields;
    }

    /**
     *
     * @return
     */
    public List<String> filters() {
        return filters;
    }
    
    /**
     *
     */
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
    
    /**
     *
     * @param args
     */
    @Override
    public void useOnlyTheseFields(String... args) {
        this.useNoFields();
        useOrNotTheseFields(true, args);
    }
    
    /**
     *
     * @param args
     */
    @Override
    public void doNotUseTheseFields(String... args) {
        useOrNotTheseFields(false, args);
    }
    
    /**
     *
     * @param args
     */
    @Override
    public void useTheseFields(String... args) {
        useOrNotTheseFields(true, args);
    }
    
    /**
     *
     */
    @Override
    public void useNoFields() {
        for (BOField field : fields) 
            field.doNotUseThisField();
    }
    
    /**
     *
     */
    @Override
    public void reset() {
        resetValues();
        resetKeys();
        useAllFields();
        resetSorts();
        resetFilters();
    }
    
    /**
     *
     * @param format
     */
    @Override
    public void setDateFormat(String format) {
        dateFormat = format;
    }
    
    /**
     *
     */
    @Override
    public void resetValues() {
        for (BOField field : fields) 
            field.setValue(null);
    }
    
    /**
     *
     */
    @Override
    public void resetKeys() {
        for (BOField field : fields) 
            field.unsetKey();
    }

    private void useAllFields() {
        for (BOField field : fields) 
            field.useThisField();
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean isDistinct() {
        return distinct;
    }

    /**
     *
     * @param Distinct
     */
    @Override
    public void setDistinct(boolean Distinct) {
        this.distinct = Distinct;
    }
    
    /**
     *
     */
    public BOEntityReadOnly() {
        this.sortedFields =  new ArrayList();
        this.fields = new ArrayList();
        this.filters = new ArrayList();
        this.distinct = false;
        this.name = "";
        this.dateFormat = "yyyy-mm-dd HH:mm:ss";
        this.limitRecords = -1;
        this.query = "";
        this.name = "";
    }
    
    /**
     *
     * @return
     */
    @Override
    public List<BOField> fields() {
        return fields;
    }
    
    /**
     *
     * @param Name
     * @return
     */
    @Override
    public BOField field(String Name) {
        BOField retField = null;
        for (BOField field : fields) 
            if (field.getLabel().equalsIgnoreCase(Name) || field.getColumnName().equalsIgnoreCase(Name)) 
                retField = field;
        if (retField == null)
            getLog().severe("Field " + Name + " not found.");
        return retField;
    }

    /**
     *
     * @param index
     * @return
     */
    @Override
    public BOField field(int index) {
        BOField retField = fields.get(index);
        if (retField == null) {
            getLog().severe("Field with index " + index + " not found.");
        }
        return retField;
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return (!fields.isEmpty() && !name.equals(""));
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    /**
     *
     * @param rs
     * @return
     */
    protected JSONObject exportResultSet(ResultSet rs)  {
        JoyJsonMatrix matrix = new JoyJsonMatrix();
        
        try {
            while (rs.next()) {
                JoyJsonVector line = new JoyJsonVector();
                for (int i=1; i <= rs.getMetaData().getColumnCount() ; i++) {
                    String colName = rs.getMetaData().getColumnName(i).toUpperCase();
                    Object val = rs.getObject(colName);
                    if (val == null) val = "";
                    line.addItem(colName, val.toString());
                }
                matrix.addRow(line);
            }
            return matrix.getData();
            
        } catch (SQLException ex) {
            getLog().severe(ex.toString());
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
            getLog().severe(e.toString());
        }
    }
    
    /**
     * Set all the myQuery values into the PreparedStatement (? replacement)
     * @param ps JDBC PreparedStatement
     * @param query Query and values container
     */
    protected void setQueryValues(PreparedStatement ps, 
                                BOQueryExecution query) {
        int i=1;
        for (BOField obj : query.getValues()) {
            getLog().fine("Connection: " + dbConnection + " | SQL '?' Value order (" + i + ") replaced by [" + obj.getValue() + "]" );
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
        getLog().fine("SQL generated: " + sql);
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
            //Joy.LOG().debug("Order By generated: " + sql);
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
            getLog().severe(ex.toString());
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
            getLog().severe(ex.toString());
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
            getLog().severe(ex.toString());
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
    
    /**
     *
     */
    @Override
    public void insertDefaultRecords() {}

    /**
     *
     * @param data
     * @param removeAllBefore
     * @return
     */
    @Override
    public Collection<ActionLogReport> imp(JSONObject data, boolean removeAllBefore) {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public int delete() {
        return 0;
    }

    /**
     *
     */
    @Override
    public void truncate() {}

    /**
     *
     * @return
     */
    @Override
    public int insert() {
        return 0;
    }

    /**
     *
     * @return
     */
    @Override
    public int update() {
        return 0;
    }

    /**
     *
     * @return
     */
    @Override
    public int upsert() {
        return 0;
    }

    /**
     *
     * @param record
     */
    @Override
    public void addDefaultRecord(BOInitRecord record) {}
    
    /**
     *
     * @param fieldname
     * @return
     */
    @Override
    public int getNewIDForField(String fieldname) { return -1; }
}
