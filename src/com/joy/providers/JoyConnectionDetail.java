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

import com.joy.common.JoyClassTemplate;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyConnectionDetail extends JoyClassTemplate {
    private String dataSource;
    private String user;
    private String password;
    private String url;
    private String driver;
    private List<JoyInitQuery> initqueries;
    
    /**
     *
     * @param DataSource
     */
    public JoyConnectionDetail(String DataSource) {
        this.dataSource = DataSource;
    }
    
    /**
     *
     */
    public JoyConnectionDetail() {
        super();
    }
    
    /**
     *
     * @param DataSource
     * @param User
     * @param Password
     * @param url
     * @param Driver
     * @param queries
     */
    public void init(String DataSource, String User, String Password, String url, String Driver, List<JoyInitQuery> queries) {
        this.dataSource = DataSource;
        this.user = User;
        this.password = Password;
        this.url = url;
        this.driver = Driver;
        initqueries = queries;
    }

    /**
     *
     * @return
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     *
     * @return
     */
    public String getUser() {
        return user;
    }

    /**
     *
     * @param User
     */
    public void setUser(String User) {
        this.user = User;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param Password
     */
    public void setPassword(String Password) {
        this.password = Password;
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     */
    public String getDriver() {
        return driver;
    }

    /**
     *
     * @param Driver
     */
    public void setDriver(String Driver) {
        this.driver = Driver;
    }
    
    /**
     *
     * @return
     */
    public JoyDBProvider getDB() {
        
        // Connection to the DB, get the main Datasource name first
        getLog().info ("Get new DB connection in the pool");
        JoyDBProvider DbConn = new JoyDBProvider();
        if (this.dataSource != null) {
            DbConn.initFromDataSource(this.dataSource); 
        } 
        if (!DbConn.isInitialized()) {
            getLog().info("DataSource unavailable or not reacheable, try a direct connection now.");
            // try with a JDBC direct connection now
            String drivername = this.driver;
            String urlname = this.url;
            String username = this.user;
            String pwd = this.password;
            DbConn.init(drivername, urlname, username, pwd); 
        }
        getLog().log(Level.INFO, "Connection opened successfully ? {0}", DbConn.isInitialized());
        
        getLog().info("Queries Initialization.");
        if (DbConn.isInitialized()) {
            for (JoyInitQuery  query : initqueries) {
                if (query.getDBProvider().equalsIgnoreCase(DbConn.getDBProvider())) 
                    DbConn.executeSQL(query.getSQL());
            }
        }
        getLog().info("Connection initialized successfully.");
        
        return DbConn;
    }
}
