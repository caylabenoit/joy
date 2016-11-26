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

import com.joy.Joy;
import java.util.List;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyConnectionDetail {
    private String dataSource;
    private String user;
    private String password;
    private String url;
    private String driver;
    private List<JoyInitQuery> initqueries;
    
    public JoyConnectionDetail(String DataSource) {
        this.dataSource = DataSource;
    }

    public JoyConnectionDetail(String DataSource, String User, String Password, String url, String Driver, List<JoyInitQuery> queries) {
        this.dataSource = DataSource;
        this.user = User;
        this.password = Password;
        this.url = url;
        this.driver = Driver;
        initqueries = queries;
    }

    public String getDataSource() {
        return dataSource;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String User) {
        this.user = User;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String Password) {
        this.password = Password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String Driver) {
        this.driver = Driver;
    }
    
    public JoyDBProvider getDB() {
        
        // Connection to the DB, get the main Datasource name first
        Joy.LOG().info ("Get new DB connection in the pool");
        JoyDBProvider DbConn = new JoyDBProvider();
        if (this.dataSource != null) {
            DbConn.initFromDataSource(this.dataSource); 
        } 
        if (!DbConn.isInitialized()) {
            Joy.LOG().info ("DataSource unavailable or not reacheable, try a direct connection now.");
            // try with a JDBC direct connection now
            String drivername = this.driver;
            String urlname = this.url;
            String username = this.user;
            String pwd = this.password;
            DbConn.init(drivername, urlname, username, pwd); 
        }
        Joy.LOG().info ("Connection opened successfully ? " + DbConn.isInitialized());
        
        Joy.LOG().info ("Queries Initialization.");
        if (DbConn.isInitialized()) {
            for (JoyInitQuery  query : initqueries) {
                if (query.getDBProvider().equalsIgnoreCase(DbConn.getDBProvider())) 
                    DbConn.executeSQL(query.getSQL());
            }
        }
        Joy.LOG().info ("Connection initialized successfully.");
        
        return DbConn;
    }
}
