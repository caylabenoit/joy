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
import com.joy.Joy;
import static com.joy.bo.BOFieldType.*;
import com.joy.providers.JoyDBProvider;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr) CAYLA
 */
public class BOField implements Cloneable {
    /**
     * belongs to this table
     */
    protected String table;
    /**
     * Field name
     */
    protected String name;
    /**
     * field label
     */
    protected String label;
    /**
     * This flag indicates the key field to use when building
     *  - the Where/and clause
     *  - the update clause
     *  - the delete clause (selecting the deleted records)
     */
    protected boolean key;
    /**
     * Indicates if this field can be used to calculate a new ID (Max +1)
     */
    protected boolean enableNewID;
    /**
     * Data type
     */
    protected BOFieldType dataType;
    /**
     * Value to store for update/insert/delete/select queries
    */
    protected Object value;
    /**
     * This flag indicates if the field is used when building 
     *  - the Select [field] clause
     *  - the Insert into ... (fields) clause (selecting the inserted fields)
     *  - the update clause (selecting the updated fields)
     */
    protected boolean doNotUse;
    /**
     * Just indicate if a value has already been set
     */
    protected boolean valueSet;
    /**
     * DB connection
     */
    protected JoyDBProvider dbConnection;

    @Override
    protected Object clone()  {
        Object myClone = null;
        try {
            myClone = super.clone();
            
        } catch(CloneNotSupportedException cnse) {
            Joy.LOG().error(cnse);
        }
        return myClone;
    }
    
    public void setDB(JoyDBProvider con) {
        dbConnection = con;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String Label) {
        this.label = Label;
    }
    
    public String getTableName() {
        return table;
    }

    public void setTableName(String TableName) {
        this.table = TableName;
    }
    
    public boolean isNotUsed() {
        return doNotUse;
    }

    public void doNotUseThisField() {
        this.doNotUse = true;
    }
    
    public void useThisField() {
        this.doNotUse = false;
    }
    
    public String getColumnName() {
        return name;
    }

    public void setKeyValue(Object Value) {
        this.value = Value;
        this.key = true;
        this.valueSet = true;
    }
    
    public Object getValue() {
        return value;
    }

    public int setNextIDValue() {
        int newID = this.getNextID();
        setValue(newID);
        return newID;
    }
    
    public void setValue(Object Value) {
        this.value = Value;
        this.valueSet = true;
    }
    
    public void setColumnName(String ColumnName) {
        this.name = ColumnName;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey() {
        this.key = true;
    }
    
    public void unsetKey() {
        this.key = false;
    }
    
    public BOFieldType getFieldType() {
        return dataType;
    }

    public void setFieldType(BOFieldType FieldType) {
        this.dataType = FieldType;
    }

    public boolean isEnableNewID() {
        return enableNewID;
    }

    public void setEnableNewID(boolean enableNewID) {
        this.enableNewID = enableNewID;
    }

    public BOField(String TableName, String ColumnName, boolean Key, BOFieldType FieldType, String Label) {
        this.name = ColumnName;
        this.table = TableName;
        this.key = Key;
        this.dataType = FieldType;
        this.value = null;
        this.enableNewID = false;
        this.doNotUse = false;
        this.valueSet = false;
        this.dbConnection = null;
        this.label = Label;
    }

    public BOField() {
        this.name = "";
        this.table = "";
        this.key = false;
        this.dataType = fieldString;
        this.value = null;
        this.enableNewID = false;
        this.doNotUse = false;
        this.valueSet = false;
    }

    /** 
     * Returns the next ID (by increasing the maximum existing ID
     * @return new and not yet used ID
     */
    public int getNextID() {
        try {
            if (this.dataType == fieldInteger) {
                String sql = "SELECT ";
                int nId = 1;
                sql += "MAX(" + this.name + ") AS " + C.ENTITYFIELD_COUNT_FIELD;
                sql += " FROM " + table;
            
                ResultSet rs = dbConnection.getResultSet(sql);
                if (rs.next()) {
                    nId = rs.getInt(C.ENTITYFIELD_COUNT_FIELD) + 1;
                }
                dbConnection.closeResultSet(rs);
                return nId;
            } else {
                return -1;
            }
        } catch (SQLException ex) {
            Joy.LOG().error(ex);
            return 0;
        }
    }
    
}
