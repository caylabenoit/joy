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

import com.joy.C;
import com.joy.json.JSONObject;
import com.joy.common.ActionLogReport;
import com.joy.common.ActionLogReport.enum_CRITICITY;
import com.joy.json.JSONArray;
import com.joy.json.JSONException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import static com.joy.bo.BOEntityRWType.boReadWrite;
import static com.joy.bo.BOFieldType.fieldInteger;
import com.joy.bo.init.BOInitField;
import com.joy.bo.init.BOInitRecord;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr) 
 */
public class BOEntityReadWrite extends BOEntityReadOnly {
    /**
     * Default records (initialization in the XML file)
     */
    private List<BOInitRecord> defaultRecords;

    public BOEntityReadWrite() {
        super();
        defaultRecords = new ArrayList<>();
    }
    
    /**
     * Build SQL delete statement
     * @param withfilters Specify to use filtering
     * @return SQL Query ready to execute
     */
    protected BOQueryExecution getSQLDelete(boolean withfilters) {
        BOQueryExecution lQuery = new BOQueryExecution();
        String sql = "DELETE FROM " + name;
        String where = " WHERE 1=1";
        
        if (withfilters) {
            sql += where;
            for (BOField field : fields) {
                if (field.isKey()) {
                    sql += " AND " + field.getColumnName() + " = ?";
                    lQuery.addValue(field);
                }
            }
        }
        lQuery.setSQL(sql);
        return lQuery;
    }
    
    /**
     * return SQL update  with ? instead of values
     * @return SQL update
     */
    protected  BOQueryExecution getSQLUpdate() {
        BOQueryExecution lQuery = new BOQueryExecution();
        String sql = "UPDATE " + name + " SET ";
        String set = "";
        String where = " WHERE 1=1";
        
        for (BOField field : fields) {
            if (!field.isKey() && !field.isNotUsed()) {
                set += field.getColumnName() + " = ?,";
                lQuery.addValue(field);
            } 
        }
        
        for (BOField field : fields) {
            if (!field.isNotUsed() && field.isKey()) {
                where += " AND " + field.getColumnName() + " = ? ";
                lQuery.addValue(field);
            } 
        }
        
        sql += set.substring(0, set.length()-1) + " " + where;
        
        lQuery.setSQL(sql);
        return lQuery;
    }
    
    /**
     * Return SQL insert statement with ? instead of values
     * @return SQL insert
     */
    protected BOQueryExecution getSQLInsert() {
        BOQueryExecution lQuery = new BOQueryExecution();
        String sql = "INSERT INTO " + name ;
        sql += " (";
        for (BOField field : fields) {
            if (!field.isNotUsed())
                sql += field.getColumnName() + ", ";
        }
        sql = sql.substring(0, sql.length() - 2) + ") Values (";
        for (BOField field : fields) {
            if (!field.isNotUsed()) {
                sql += "?,";
                lQuery.addValue(field);
            }
        }
        sql = sql.substring(0, sql.length() - 1) + ")";
        
        lQuery.setSQL(sql);
        return lQuery;
    }

    /*
    * importJson all the data coming from a JSON Object into a table
    *   @data : JSON Data
    *   @removeAllBefore: delete all records before import
    *   return : Joy Logs & errors
    */
    @Override
    public Collection<ActionLogReport> imp(JSONObject data, 
                                                  boolean removeAllBefore) {
        if (this.boType != boReadWrite) return null;
        Collection<ActionLogReport> logs = new ArrayList();
        JSONArray records;
        int i;
        // récupère le nom de la table pour vérification
        try {
            records = data.getJSONArray(this.name);
        } catch (JSONException e) { records = null; }
        
        if (records != null) {
            // vide la table si besoin
            if (removeAllBefore) {
                getLog().info("Remove all records in table " + this.name);
                this.resetKeys();
                this.delete();
                logs.add(new ActionLogReport("IMP1", 0, "All records successfully removed from table", enum_CRITICITY.INFO));
            }
           
            // parcours toutes les lignes
            for (i=0; i<records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                // Parcours les colonnes
                for (int j=0; j<record.getJSONArray("Record").length() ; j++) {
                    JSONObject Column = record.getJSONArray("Record").getJSONObject(j);
                    String ColumnName = Column.keys().next();
                    Object ColumnValue = Column.get(ColumnName);
                    this.field(ColumnName).setValue(ColumnValue);
                }
                // Insertion de l'enregistrement
                this.insert();
            }
            logs.add(new ActionLogReport("IMP3", 0, i + " Record(s) inserted successfully.", enum_CRITICITY.INFO));
            
        } else {
            getLog().severe("Tables or Metadata do not match with file !");
            logs.add(new ActionLogReport("IMP2", 0, "Tables not match with file !", enum_CRITICITY.FATAL));
        }
        return null;
    }
    
    /**
     * Supprime toutes les données de la table avec condition
     * la condition where est déterminée avec les éléments/field clés/key
     * @return nb of rows removed
     */
    @Override
    public int delete()  {
        if (this.boType != boReadWrite) return 0;
        try {
            BOQueryExecution lQuery = this.getSQLDelete(true);
            
            PreparedStatement ps = dbConnection.prepareSQL(lQuery.getSQL());
            setQueryValues(ps, lQuery);
            int retNb =  ps.executeUpdate();
            getLog().fine("Number of rows deleted : " + retNb);
            return retNb;
            
        } catch (SQLException ex) {
            getLog().severe(ex.toString());
            return 0;
        }
    }    

    /**
     * Tronque la table (truncate)
     */
    @Override
    public void truncate()  {
        if (this.boType != boReadWrite) return;
        try {
            PreparedStatement ps = dbConnection.prepareSQL("TRUNCATE TABLE " + this.name);
            ps.execute();
            
        } catch (SQLException ex) {
            getLog().severe(ex.toString());
        }
    }    
    
    /**
     * insert the data considering the values set
     * @return nb of rows affected / should be 1 if no errors ! 
     */
    @Override
    public int insert()  {
        if (this.boType != boReadWrite) return 0;
        try {
            BOQueryExecution lQuery = this.getSQLInsert();
            
            PreparedStatement ps = dbConnection.prepareSQL(lQuery.getSQL());
            setQueryValues(ps, lQuery);
            int retNb =  ps.executeUpdate();
            getLog().fine("Number of rows altered : " + retNb);
            return retNb;
            
        } catch (SQLException ex) {
            getLog().severe(ex.toString());
            return -1;
        }
    }
    
    /**
     * Launch an update considering the values set and all the key fields and data are used for Where conditions
     * @return nb of rows affected / should be sup to 0 if no errors ! inf to 0 if errors
     */
    @Override
    public int update()  {
        if (this.boType != boReadWrite) return 0;
        try {
            BOQueryExecution lQuery = this.getSQLUpdate();
            
            PreparedStatement ps = dbConnection.prepareSQL(lQuery.getSQL());
            setQueryValues(ps, lQuery);
            int retNb =  ps.executeUpdate();
            getLog().fine("Number of rows altered : " + retNb);
            return retNb;
            
        } catch (SQLException ex) {
            getLog().severe(ex.toString());
            return -1;
        }
    }

    /**
     * insert the data if the data does not exists yet, update else the existing one
     * @return Nb of rows affected
     */
    @Override
    public int upsert() {
        if (this.boType != boReadWrite) return 0;
        if (this.checkIfExists())
            return this.update();
        else 
            return this.insert();
    }

    /**
     * insert the default records into the DB
     */
    @Override
    public void insertDefaultRecords() {
        this.reset();
        this.useNoFields();
        try {
            for (BOInitRecord record : this.defaultRecords) {
                for (BOInitField field : record.getFields()) {
                    this.field(field.getName()).useThisField();
                    this.field(field.getName()).setValue(field.getValue());
                }
                this.insert();
            }
            
        } catch (Exception e) {
            getLog().severe(e.toString());
        }
    }
    
    /** 
     * Add a new default record to this entity
     * @param record new record to add
     */
    @Override
    public void addDefaultRecord(BOInitRecord record) {
        defaultRecords.add(record);
    }
    
    /** 
     * Returns the next ID (by increasing the maximum existing ID
     * @param fieldname field name
     * @return new and not yet used ID (-1 in case of error)
     */
    @Override
    public int getNewIDForField(String fieldname) {
        try {
            // find the field
            BOField myField = null;
            for (BOField field : this.fields) {
                if (field.getColumnName().equalsIgnoreCase(fieldname))
                    myField = field;
            }
            
            // get the new id
            if (myField != null) 
                if (myField.dataType == fieldInteger) {
                    String sql = "SELECT ";
                    int nId;
                    sql += "MAX(" + myField.name + ") AS " + C.ENTITYFIELD_COUNT_FIELD;
                    sql += " FROM " + this.name;

                    ResultSet rs = dbConnection.getResultSet(sql);
                    if (rs.next()) {
                        nId = rs.getInt(C.ENTITYFIELD_COUNT_FIELD) + 1;
                    } else
                        nId = -1;
                    dbConnection.closeResultSet(rs);
                    return nId;
                }
            
        } catch (SQLException ex) {
            getLog().severe(ex.toString());
        }
        return -1;
    }
    
}
