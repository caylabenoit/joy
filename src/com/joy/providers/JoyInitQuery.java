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

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class JoyInitQuery {
    private String dbProvider;
    private String sql;

    /**
     *
     * @param DBProvider
     * @param SQL
     */
    public JoyInitQuery(String DBProvider, String SQL) {
        this.dbProvider = DBProvider;
        this.sql = SQL;
    }

    /**
     *
     * @return
     */
    public String getDBProvider() {
        return dbProvider;
    }

    /**
     *
     * @param DBProvider
     */
    public void setDBProvider(String DBProvider) {
        this.dbProvider = DBProvider;
    }

    /**
     *
     * @return
     */
    public String getSQL() {
        return sql;
    }

    /**
     *
     * @param SQL
     */
    public void setSQL(String SQL) {
        this.sql = SQL;
    }
    
    
}
