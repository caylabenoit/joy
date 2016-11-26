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
package com.joy.providers;

import com.joy.common.SQLScriptRunner;
import com.joy.Joy;
import com.joy.bo.BOField;
import com.joy.bo.BOFieldType;
import static com.joy.bo.BOFieldType.*;
import com.joy.bo.BOMapFieldLabel;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.joy.bo.IEntity;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyDBProvider {
    private Connection dbConnection;

    public boolean isInitialized() {
        return (dbConnection != null);
    }
    
    public void end() {
        try {
            dbConnection.close();
        } catch (SQLException ex) {
            Joy.LOG().error (ex);
        }
    }

    /**
     * Connection with splitted credential used
     * @param driver 
     * @param url 
     * @param user 
     * @param password 
     */
    public void init(String driver, String url, String user, String password) {

        if (dbConnection == null) {
            try {
                Joy.LOG().debug ("New Connection initalization (direct)");
                Joy.LOG().debug ("Connection Driver: " + driver);
                Joy.LOG().debug ("Connection URL: " + url);
                Joy.LOG().debug ("Connection User: " + user);
                
                Class.forName(driver);
                
                dbConnection = DriverManager.getConnection(url, user, password);
                Joy.LOG().info ("New Connection Initialized");
                
            } catch (ClassNotFoundException cnfe) {
                Joy.LOG().error ("ClassNotFoundException> " + cnfe);
                
            } catch (SQLException sqe) {
                Joy.LOG().error ("SQLException> " + sqe);
            }
        }
    }    
    
    /**
     * Connection using configfile (text file)
     */
    public void initFromConfigFile() {

        if (dbConnection == null) {
            try {
                Joy.LOG().debug ("New Connection initalization from config file");
                JoyConfigfileProvider config = new JoyConfigfileProvider();
                String driver = config.get("driver");
                String url = config.get("url");
                String user = config.get("user");
                String password = config.get("password");
                
                Joy.LOG().debug ("Connection Driver: " + driver);
                Joy.LOG().debug ("Connection URL: " + url);
                Joy.LOG().debug ("Connection User: " + user);
                
                Class.forName(driver);
                
                dbConnection = DriverManager.getConnection(url, user, password);
                Joy.LOG().info ("New Connection Initialized");
                
            } catch (ClassNotFoundException cnfe) {
                Joy.LOG().error ("ClassNotFoundException> " + cnfe);
                
            } catch (SQLException sqe) {
                Joy.LOG().error ("SQLException> " + sqe);
            }
        }
    }
    
    /**
     * Connection using DataSource
     * @param dsString 
     */
    public void initFromDataSource(String dsString) {
        Context initContext;
        try {
            if (dbConnection != null) {
                //LogProvider.debug ("No need to connect again, reuse existing connection");

            } else {
                Joy.LOG().info ("Get new connection using datasource " + dsString);
                initContext = new InitialContext();
                DataSource ds = (DataSource)initContext.lookup(dsString);
                dbConnection = ds.getConnection();
            }
        } catch (NamingException | SQLException ex) {
            Joy.LOG().warn("NamingException or SQLException error: " + ex);
        } catch (Exception e) {
            Joy.LOG().warn ("Unexpected error: " + e);
        }
    }

    /**
     * returns the DB Name (provider)
     * @return PostgreSQL ou Oracle
     */
    public String getDBProvider() {
        try {
            return dbConnection.getMetaData().getDatabaseProductName(); // PostgreSQL ou Oracle
        } catch (SQLException ex) {
            return "";
        }
    }
    
    private Connection getConnection() {
        return dbConnection;
    }
    
    public void executeSQL(String SQL) {
        getResultSet(SQL);
    }
    
    public ResultSet getResultSet(String SQL) {
        try {
            Joy.LOG().debug("SQL > " + SQL);
            PreparedStatement ps = getConnection().prepareStatement(SQL);
            return ps.executeQuery();
        } catch (SQLException e) {
            Joy.LOG().error(e);
        }
        return null;
    }
    
    private ResultSet getResultSet(PreparedStatement ps) {
        try {
            Joy.LOG().debug("Connection: " + this.dbConnection + " | Get resultset With PreparedStatement");
            return ps.executeQuery();
        } catch (SQLException e) {
            Joy.LOG().error(e);
        }
        return null;
    }
    
    public PreparedStatement prepareSQL(String SQL) {
        try {
            Joy.LOG().debug("Connection: " + this.dbConnection + " | SQL > " + SQL);
            return getConnection().prepareStatement(SQL);
        } catch (SQLException e) {
            Joy.LOG().error(e);
        }
        return null;
    }
    
    public void closeResultSet(ResultSet rs) {
        try {
            Joy.LOG().debug("Close recordset, Cursor: " + rs.toString());
            rs.close();
        } catch (SQLException e) {
            Joy.LOG().error(e);
        }
    }

    public void runScript(String myFile) {
        try {
            Joy.LOG().info("Run script from " + myFile);
            Reader reader = new FileReader(myFile);
            SQLScriptRunner runer = new SQLScriptRunner(dbConnection, true, true);
            runer.runScript(reader);
            
        } catch (IOException | SQLException ex) {
            Joy.LOG().error(ex);
        }
    }

    /**
     * Modify the Entity by adding all its fields with Metadata
     * @param Entity    Entity object
     * @param Query     SQL Query (if not a table)
     * @param remapsField remaps fields (optional)
     */
    public void getMetadataFromDB(IEntity Entity,
                                  String Query,
                                  List<BOMapFieldLabel> remapsField) {
        String myQueryMD = (Query == null ? "" : Query);
        if (myQueryMD.isEmpty())
            myQueryMD = "SELECT * FROM " + Entity.getName();
        getFieldMetadataFromDB(Entity, myQueryMD, remapsField);
    }
    
    private String getLabelFromFieldName(String Name, List<BOMapFieldLabel> remapsField) {
        for (BOMapFieldLabel fieldmap : remapsField) 
            if (fieldmap.getName().equalsIgnoreCase(Name)) {
                return fieldmap.getLabel();
            }
        return Name;
    }
    
    /**
     * Get the Technical metadata from the DB directly
     * @param ent BO Entity to build
     * @param Query Query for getting the metadata
     * @param Name Entity Name (could be Table name or custom label)
     * @param remapsField remap field list (name to label)
     */
    private void getFieldMetadataFromDB(IEntity ent, 
                                        String Query, 
                                        List<BOMapFieldLabel> remapsField) {
        int i;
        
        try {
            ResultSet rs = getResultSet(Query);
            ResultSetMetaData rsmd = rs.getMetaData(); 
            
            for(i=1; i <= rsmd.getColumnCount(); i++){ 
                String fieldName = rsmd.getColumnName(i); 
                String fieldType = rsmd.getColumnTypeName(i); 
                int prec = rsmd.getPrecision(i);
                int scale = rsmd.getScale(i);
                String fieldLabel = getLabelFromFieldName(fieldName, remapsField); 

                BOField myField = new BOField(ent.getName(), 
                                              fieldName, 
                                              false, 
                                              getFieldDatatype(fieldType, prec, scale),
                                              (fieldLabel == null ? fieldName : fieldLabel));
                ent.fields().add(myField);
            } 
            Joy.LOG().info(i-1 + " Field(s) successfully added to Entity " + ent.getName());

        } catch (Exception ex) {
            Joy.LOG().error(ex);
        }
    }
    
    /**
     * Returns the data type
     * @param myType DB data type label
     * @param prec DB data precision
     * @param scale DB scale
     * @return internal data type
     */
    private static BOFieldType getFieldDatatype(String myType, 
                                                int prec, 
                                                int scale) {
        
        if (myType.toUpperCase().substring(0, 3).equalsIgnoreCase("INT"))
            return fieldInteger; 
        
        if (myType.toUpperCase().contains("CHAR"))
            return fieldString;
        
        if (myType.toUpperCase().contains("FLOAT"))
            return fieldFloat;
        
        if (myType.toUpperCase().contains("DATE") || myType.toUpperCase().contains("TIME"))
            return fieldDate;
        
        // Other cases
        switch (myType.toUpperCase()) {
            case "NUMBER": 
                if (scale > 0)
                    return fieldFloat; 
                else
                    return fieldInteger; 
            case "NUMERIC": 
                return fieldInteger; 
            case "DECIMAL":
                return fieldFloat;
            case "BOOLEAN": 
                return fieldBoolean; 
            case "STRING": 
            default : 
                return fieldString;
        }
    }
}
