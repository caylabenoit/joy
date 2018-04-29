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

/**
 *
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOCompositeSorter {
    private String fieldName;
    private String alias;
    private boolean descSort;

    /**
     *
     * @param field
     * @param alias
     * @param desc
     */
    public BOCompositeSorter(String field, String alias, boolean desc) {
        this.fieldName = field;
        this.descSort = desc;
        this.alias = (alias == null ? "" : alias);
    }
    
    /**
     *
     * @param field
     * @param alias
     */
    public BOCompositeSorter(String field, String alias) {
        this.fieldName = field;
        this.alias = (alias == null ? "" : alias);
        this.descSort = false;
    }
    
    /**
     *
     * @param field
     */
    public BOCompositeSorter(String field) {
        this.fieldName = field;
        this.alias = "";
        this.descSort = false;
    }
    
    /**
     *
     * @return
     */
    public String getSort() {
        return (alias.isEmpty() ? fieldName : alias + "." + fieldName) + " " + (this.descSort ? "DESC" : "ASC");
    }
}
