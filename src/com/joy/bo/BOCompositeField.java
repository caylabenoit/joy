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
 * This class manages Fields into a composite Entity object
 * @author Benoit CAYLA (benoit@famillecayla.fr)
 */
public class BOCompositeField {
    private String name;
    private String alias;
    private String as;
    
    /**
     *
     * @param name
     * @param alias
     * @param as
     */
    public BOCompositeField(String name, String alias, String as) {
        this.name = name;
        this.alias = (alias == null ? "" : alias);
        this.as = (as == null ? "" : as);
    }

    /**
     *
     * @return
     */
    public String getSelectField() {
        return (this.alias.isEmpty() ? this.name :  this.alias + "." + this.name) + 
               (this.as.isEmpty() ? "" : " AS " + this.as);
    }
}
